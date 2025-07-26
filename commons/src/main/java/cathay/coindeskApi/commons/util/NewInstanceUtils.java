package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ReflectionUtils.getConstructor;
import static cathay.coindeskApi.commons.util.TypeUtils.isBoxedType;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NewInstanceUtils {

	public static <T> T checkInstantiable(Class<T> type, Object... initArgs) throws IllegalArgumentException {
		return checkInstantiable(null, type, initArgs);
	}
	
	/**
	 * 如果待建立之型別為 "非static" 巢狀類別
     *  ➜ 呼叫java原生newInstance時, 須傳入外部類別之instance
	 */
	public static <T> T checkInstantiable(Object instanceOfEnclosingType, Class<T> type, Object... initArgs) throws IllegalArgumentException {
		if (type == null)
			throw new NullPointerException("參數type不可為空");
		
		T newInst = null;
		if (type.isPrimitive() || isBoxedType(type)) {
			newInst = TypeUtils.getDefaultValue(type, true);
			return newInst;
		}
		
		// 是否為inner class, 建立物件方式會有所不同
		final boolean isInnerClass = type.getEnclosingClass() != null;
		
		final Class<?>[] paramTypes = stream(initArgs).map((arg) -> arg.getClass())
			.filter(Objects::nonNull)
			.toArray(Class<?>[]::new);
		
		Constructor<T> ctor = null;
		try {
			ctor = getConstructor(type, paramTypes);
			if (isInnerClass) {
				if (isStatic(type.getModifiers()))
					newInst = ctor.newInstance(initArgs);
				else {
					if (instanceOfEnclosingType == null)
						throw new IllegalArgumentException("欲建立\"非static類別\"之物件, 需傳入外部類別的實體作為第1個參數.");
					
					final ArrayList<Object> argList = new ArrayList<Object>();
					// 呼叫 inner "static"類別 的建構子 --> 第1個參數必須是parent class物件實體
					argList.add(instanceOfEnclosingType);
					// 蒐集傳入的建構子參數
					for (Object arg : initArgs)
						argList.add(arg);
					
					// call constructor to instantiate
					newInst = ctor.newInstance(argList.toArray(new Object[argList.size()]));
				}
			}
			else {
				newInst = ctor.newInstance();
			}
		}
		// 呼叫"非公開"建構子
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException("無法建立 '" + type.getName() + "' 型別的物件, 請使用public程度較高的型別", e);
		}
		// 欲建立"抽象"型別之物件
		catch (InstantiationException e) {
			throw new IllegalArgumentException("無法建立 '" + type.getName() + "' 型別的物件, 請提供具體類別, 或其他可實體化之型別", e);
		}
		// 建構子執行過程發生的錯誤
		catch (InvocationTargetException e) {
			throw new RuntimeException("無法呼叫constructor建立instance", e);
		}
		return newInst;
	}
	
	/**
	 * create only 1 new instance
	 */
	public static <T> T newInstance(Class<T> type, Object... initArgs) throws IllegalArgumentException {
		return newInstance(null, type, initArgs);
	}
	
	/**
	 * create only 1 new instance
	 *  (for non-static inner type)
	 */
	public static <T> T newInstance(Object instanceEnclosingType, Class<T> type, Object... initArgs) throws IllegalArgumentException {
		if (type == null)
			throw new NullPointerException("參數type不可為空");
		T newInst = checkInstantiable(instanceEnclosingType, type);
		return newInst;
	}
	
	
	/**
	 * new empty array()
	 */
	public static <T> T[] newInstance(Class<T> componentType, int size) {
		return newInstance(componentType, size, (BiConsumer<Integer, T>) null);
	}
	
	/**
	 * new empty array(), and allows traversing each new element
	 */
	public static <T> T[] newInstance(Class<T> componentType, int size, Consumer<T> eachElement) {
		return newInstance(componentType, size, (index, instance) -> eachElement.accept(instance));
	}
	
	/**
	 * new empty array(), and allows traversing each new element with index
	 */
	public static <T> T[] newInstance(Class<T> componentType, int size, BiConsumer<Integer, T> eachElement) {
		if (componentType == null)
			throw new NullPointerException("參數componentType不可為空");
		
		// 測試componentType類別是否可實體化
		final T firstInstance = checkInstantiable(componentType);
		// ➜ 未發生錯誤, 此類別是可實體化的
		
		T[] newInstances = (T[]) Array.newInstance(componentType, size);
		for (int i=0; i<size; i++) {
			try {
				T newInst = (i ==0)? firstInstance : componentType.newInstance();
				newInstances[i] = newInst;
				if (eachElement != null) {
					eachElement.accept(i, newInst);
				}
			}
			catch (InstantiationException | IllegalAccessException e) {}
		}		
		return newInstances;
	}
}

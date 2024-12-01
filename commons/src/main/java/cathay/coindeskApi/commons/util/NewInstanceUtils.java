package cathay.coindeskApi.commons.util;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NewInstanceUtils {

	public static <T> T checkInstantiable(Class<T> type) throws IllegalArgumentException {
		T newInst = null;
		try {
			newInst = type.newInstance();
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("無法建立 '" + type.getName() + "' 型別的物件, 請使用public程度較高的型別", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("無法建立 '" + type.getName() + "' 型別的物件, 請提供具體類別, 或其他可實體化之型別", e);
		}
		return newInst;
	}
	
	/**
	 * create only 1 new instance
	 */
	public static <T> T newInstance(Class<T> type) throws IllegalArgumentException {
		if (type == null)
			throw new NullPointerException("參數type不可為空");
		
		T newInst = checkInstantiable(type);
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
		T firstInstance = checkInstantiable(componentType);
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

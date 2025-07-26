package cathay.coindeskApi.commons.util;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectionUtils {

	public static <T> Constructor<T> getDefaultConstructor(Class<T> type) {
		return getDefaultConstructor(type, null);
	}
	
	public static <T> Constructor<T> getDefaultConstructor(Class<T> type, Consumer<Constructor<T>> successThen) {
		Constructor<T> ctor = getConstructor(type, new Class<?>[] {});
		if (successThen != null)
			successThen.accept(ctor);
		return ctor;
	}
	
	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... paramTypes) {
		return getConstructor(type, paramTypes, (Consumer) null);
	}
	
	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>[] paramTypes, Consumer<Constructor<T>> successThen) {
		// 判斷type是否為inner class
		final boolean isInnerClass = type.getEnclosingClass() != null;
		
		// 不可含有null
		checkParamTypes(paramTypes);
		
		Constructor<T> ctor = null;
		try {
			if (isInnerClass) {
				if (isStatic(type.getModifiers())) {
					ctor = type.getDeclaredConstructor(paramTypes);
				}
				else {
					final ArrayList<Class<?>> paramList = new ArrayList<Class<?>>();
					// 呼叫 inner "static"類別 的建構子 --> 第1個參數必須是parent class
					paramList.add(type.getEnclosingClass());
					// 蒐集各paramType, 並去除null元素
					stream(paramTypes).filter((c) -> c != null).forEach((c) -> {
						paramList.add(c);
					});
					ctor = type.getDeclaredConstructor(paramList.toArray(new Class<?>[paramList.size()]));
				}
			}
			else {
				ctor = type.getDeclaredConstructor(paramTypes);
			}
			
			if (successThen != null)
				successThen.accept(ctor);
		}
		// Constructor not found
		catch (NoSuchMethodException e) {
			return null;
		}
		catch (SecurityException e) {
			/*
			 * If a security manager, s, is present and any of thefollowing conditions is met:
			 * • the caller's class loader is not the same as theclass loader of this class
			 *   and invocation of s.checkPermission method with RuntimePermission("accessDeclaredMembers")
			 *   denies access to the declared constructor
			 *    
			 * • the caller's class loader is not the same as or anancestor of the class loader
             *   for the current class andinvocation of s.checkPackageAccess() denies access to the packageof this class 
			*/
			throw e;
		}
		return ctor;
	}
	
	private static void checkParamTypes(Class<?>... paramTypes) {
		if (paramTypes != null && anyNull(paramTypes))
			throw new IllegalArgumentException("All the classes of paramTypes must not be null.");
	}
	
	public static <T> Class<T> loadClass(String classname) {
		Class<T> type = null;
		try {
			type = (Class<T>) Class.forName(classname);
			return type;
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Failed to load the class 'StreamBuilderImpl'", e);
		}
	}
	
	/**
	 * 取得一型別內的存取方法 (getter, setter)
	 * 
	 * @param accessType 可以是"get" | "set"
	 */
	public static Map<String, Method> findDeclaredAccessors(Class<?> type, String accessType) {
		return findDeclaredAccessors(type, accessType, (method) -> true);
	}
	
	/**
	 * 取得一型別內的存取方法 (getter, setter)
	 * 
	 * @param accessType 可以是"get" | "set"
	 * @param filter 外界傳入的filter
	 */
	public static Map<String, Method> findDeclaredAccessors(Class<?> type, String accessType, Predicate<Method> filter) {
		if (!"set".equals(accessType) && !"get".equals(accessType))
			throw new IllegalArgumentException("The argument 'accessType' can only be 'set' | 'get'");
		
		final Map<String, Method> declaredAccessors = (type != null)
				? stream(type.getDeclaredMethods())
				  // 取出is, get, set開頭的methods
				  .filter((method) -> {
					  if ("get".equals(accessType)) {
						  // 布林型別
						  if (method.getName().substring(0, 2).equalsIgnoreCase("is"))
							  return boolean.class == method.getReturnType();
							  		 // 寬鬆限制: Boolean也能搭配 "is"前綴
									 //|| Boolean.class == method.getReturnType();
						  // 一般型別
						  return method.getName().startsWith("get");
					  }
					  else if ("set".equals(accessType)) {
						  if (!method.getName().startsWith("set"))
							  return false;
						  return method.getParameters().length > 0;
					  }
					  return false;
				  })
				  // 外界傳入的filter
				  .filter(filter)
				  .collect(Collectors.toMap(
				      (method) -> method.getName(),
					  (method) -> {
				         return method;
				  }))
				: new HashMap<String, Method>();
		return declaredAccessors;
	}
	
	public static Field[] getFields(Class<?> type, String... fieldnames) {
		return stream(fieldnames).map((fieldname) -> {
			try {
				return type.getDeclaredField(fieldname);				
			} catch (NoSuchFieldException | SecurityException e) {
				return null;
			}
		})
		.filter((field) -> field != null)
		.toArray(Field[]::new);
	}
	
	public static Method currentRunningMethod(Predicate<StackTraceElement> checkProxyMethod, Class<?>...paramTypes) {
		return currentRunningMethod((elem, index) -> checkProxyMethod.test(elem), paramTypes);
	}
	
	public static Method currentRunningMethod(BiPredicate<StackTraceElement, Integer> checkProxyMethod, Class<?>...paramTypes) {
		// Get the call stack
		StackTraceElement[] callStackElements = Thread.currentThread().getStackTrace();

		// -> move to the current stackElement
		StackTraceElement currentStackElem = null;
		for (int i=0; i<callStackElements.length; i++) {
			if (checkProxyMethod.test(callStackElements[i], i)) {
				currentStackElem = callStackElements[i];
				break;
			}
		}
		
		if (currentStackElem == null)
			return null;
		
		Class<?> type = null;
		Method method = null;		
		try {
			type = Class.forName(currentStackElem.getClassName());
			method = type.getMethod(currentStackElem.getMethodName(), paramTypes);
			return method;
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("An error occurred when getting the currently running method", e);
		}
	}
	
	/*
	public static CallFlowCompletionStage getConstructor(Class<?> type, Class<?>...argType) {
		CallFlowCompletionStage stage = new CallFlowCompletionStage(type);
		try {
			stage.setConstructor(type.getDeclaredConstructor(argType));
		} catch (NoSuchMethodException | SecurityException e) {
			stage.exception = e;
		}
		return stage;
	}
	
	public static CallFlowCompletionStage getMethod(Class<?> type, String method, Class<?>...argType) {
		CallFlowCompletionStage stage = new CallFlowCompletionStage(type);
		try {
			stage.setMethod(type.getDeclaredMethod(method, argType));
		} catch (NoSuchMethodException | SecurityException e) {
			stage.exception = e;
		}
		return stage;
	}
	
	// 需要給this?
	public static CallFlowCompletionStage getField(Class<?> type, String field) {
		CallFlowCompletionStage stage = new CallFlowCompletionStage(type);
		try {
			stage.setField(type.getDeclaredField(field));
		} catch (NoSuchFieldException | SecurityException e) {
			stage.exception = e;
		}
		return stage;
	}
	
	// 非static inner class時, 需要給this??
	public static CallFlowCompletionStage getInnerType(Class<?> enclosingType, String innerTypeName) {
		CallFlowCompletionStage stage = new CallFlowCompletionStage(enclosingType);
		stage.setInnerType(
			stream(enclosingType.getDeclaredClasses())
			.filter((each) -> each.getName().equals(enclosingType.getName() + '$' + innerTypeName))
			.findFirst().orElseGet(null)
		);
		return stage;
	}
	*/
}

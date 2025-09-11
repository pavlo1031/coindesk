package cathay.coindeskApi.commons.util.proxy;

import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.sizeGreaterOrEqualTo;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.checkCondition;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import cathay.coindeskApi.commons.util.ReflectionUtils;

public class ProxyUtils {
	/**
	 * 代理不同類別, 相同名稱與參數型式的method
	 * 
	 * @param findProxyMethod 指定stackElement中的目標method
	 */
	public static ProxyCompletionStage proxyMethod(Predicate<StackTraceElement> findProxyMethod, Class<?> proxyClass, Class<?>[] paramTypes) {
		return proxyMethod((elem, index) -> findProxyMethod.test(elem), proxyClass, paramTypes);
	}
	
	/**
	 * 代理不同類別, 相同名稱與參數型式的method
	 * 
	 * @param findProxyMethod 指定stackElement中的目標method
	 */
	public static ProxyCompletionStage proxyMethod(BiPredicate<StackTraceElement, Integer> findProxyMethod, Class<?> proxyClass, Class<?>[] paramTypes) {
		// find target method name
		final Method currentMethod = ReflectionUtils.currentRunningMethod(findProxyMethod, paramTypes);
		
		// load the target method
		Method proxyMethod = null;
		try {
			proxyMethod = proxyClass.getMethod(currentMethod.getName(), currentMethod.getParameterTypes());
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return new ProxyCompletionStage().proxyClass(proxyClass)
										  .proxyMethod(proxyMethod)
										  .proxyMethodParameterTypes(paramTypes);
	}

	public static <T, R> R exec(ProxyCompletionStage completionStage) throws Throwable {
		Objects.requireNonNull(completionStage.proxyInstance, "proxy instance must be present");
		
		int paramCount = completionStage.proxyMethodParamTypes.length;
		if (paramCount > 0) {
			// check: presence of the arguments
			requireNonNull(completionStage.proxyMethodArguments, "proxy instance must be present");
			
			// check: argument count
			Parameter lastParam = completionStage.proxyMethod.getParameters()[0];
			// 參數如果是variable argument → 可有可無 → 必要參數數量-1
			if (lastParam.isVarArgs())
				paramCount--;
			checkCondition(completionStage.proxyMethodArguments, sizeGreaterOrEqualTo(paramCount), "Too few arguments. At least %d arguments must be given, which means some arguments are not passed to the method.".formatted(paramCount));
		}
		
		Object ret = null;
		try {
			// before
			if (completionStage.handlerBefore != null)
				completionStage.handlerBefore.apply(completionStage.proxyMethodArguments);
			
			// call the method
			ret = completionStage.proxyMethod.invoke(completionStage.proxyInstance, completionStage.proxyMethodArguments);
			
			// after calling the method successfully
			if (completionStage.handlerAfter != null)
				completionStage.handlerAfter.apply(completionStage.proxyMethodArguments);
		}
		catch(IllegalAccessException | InvocationTargetException e) {
			if (completionStage.handlerCatchError != null)
				completionStage.handlerCatchError.apply(completionStage.proxyMethodArguments, e);
			throw e;
		}
		finally {
			if (completionStage.handlerFinally != null)
				completionStage.handlerFinally.apply(completionStage.proxyMethodArguments);
		}

		// Return the result
		try {
			if (void.class == completionStage.proxyMethod.getReturnType())
				return (R) null;
			return (R) ret;
		}
		finally {
			if (completionStage.handlerAfterReturn != null)
				completionStage.handlerAfterReturn.accept(completionStage.proxyMethodArguments, ret);
		}
	}
}

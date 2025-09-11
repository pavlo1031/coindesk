package cathay.coindeskApi.commons.util.proxy;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import cathay.coindeskApi.commons.util.StringUtils;

public class ProxyCompletionStage {
	// Callbacks
	Function<Object[], Object> handlerBefore;
	Function<Object[], Object> handlerAfter;
	BiFunction<Object[], Throwable, Object> handlerCatchError;
	Function<Object[], Object> handlerFinally;
	BiConsumer<Object[], Object> handlerAfterReturn;
	
	// 靜態資訊
	Class<?> proxyClass;
	Method proxyMethod;
	Class<?>[] proxyMethodParamTypes;
	Class<?> proxyMethodReturnType;
	
	// Runtime資訊
	Object[] proxyMethodArguments;
	Object proxyInstance;
	
	public ProxyCompletionStage() {
		
	}
	
	public ProxyCompletionStage(Method proxyMethod) {
		Objects.requireNonNull(proxyMethod, "The argument 'proxyMethod' cannot be null.");
		this.proxyClass = proxyMethod.getDeclaringClass();
		this.proxyMethod = proxyMethod;
		this.proxyMethodParamTypes = proxyMethod.getParameterTypes();
		this.proxyMethodReturnType = proxyMethod.getReturnType();
	}
	
	public ProxyCompletionStage(Class<?> proxyClass, String methodName) {
		this(proxyClass, methodName, new Class[0]);
	}
	
	public ProxyCompletionStage(Class<?> proxyClass, String methodName, Class<?>... paramTypes) {
		Objects.requireNonNull(proxyClass, "The argument 'proxyClass' cannot be null.");
		Objects.requireNonNull(methodName, "The argument 'methodName' cannot be null.");
		Objects.requireNonNull(paramTypes, "The argument 'paramTypes' cannot be null.");
		Method method = null;
		try {
			method = proxyClass.getMethod(methodName, paramTypes);
			this.proxyClass = proxyClass;
			this.proxyMethod = method;
			this.proxyMethodParamTypes = paramTypes;
			this.proxyMethodReturnType = method.getReturnType();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("An error occurred when loading the method: " + StringUtils.doubleQuoteString(methodName), e);
		}
	}
	
	//////////////////////////////////////////////////////////////////
	
	public ProxyCompletionStage proxyClass(Class<?> proxyClass) { this.proxyClass = proxyClass; return this; }
	
	public Class<?> proxyClass() { return this.proxyClass; }
	
	public ProxyCompletionStage proxyMethod(Method proxyMethod) { this.proxyMethod = proxyMethod; return this; }
	
	public Method proxyMethod() { return this.proxyMethod; }
	
	public ProxyCompletionStage proxyMethodParameterTypes(Class<?>...paramTypes) { this.proxyMethodParamTypes = paramTypes; return this; }
	
	public Class<?>[] proxyMethodParameterTypes() { return this.proxyMethodParamTypes; }
	
	public ProxyCompletionStage methodReturnType(Class<?> proxyMethodReturningType) { this.proxyMethodReturnType = proxyMethodReturningType; return this; }
	
	public Class<?> methodReturnType() { return this.proxyMethodReturnType; }
	
	
	public ProxyCompletionStage proxyRuntime(Object instance, Object... args) { this.proxyInstance = instance; this.proxyMethodArguments = args; return this; }
	
	public ProxyCompletionStage proxyInstance(Object instance) { this.proxyInstance = instance; return this; }
	
	public Object proxyInstance() { return this.proxyInstance; }
	
	public ProxyCompletionStage proxyMethodArguments(Object...args) { this.proxyMethodArguments = args; return this; }
	
	public Object[] proxyMethodArguments() { return this.proxyMethodArguments; }
	
	//////////////////////////////////////////////////////////////////
	
	public ProxyCompletionStage interceptBefore(Consumer<Object[]> handlerBefore) {
		this.handlerBefore = (x) -> {
			handlerBefore.accept(x);
			return null;
		};
		return this;
	}
	
	public ProxyCompletionStage interceptBefore(Function<Object[], ?> handlerBefore) {
		this.handlerBefore = (Function<Object[], Object>) handlerBefore;
		return this;
	}
	
	public ProxyCompletionStage interceptAfter(Consumer<Object[]> handlerAfter) {
		this.handlerAfter = (x) -> {
			handlerAfter.accept(x);
			return null;
		};
		return this;
	}
	
	public ProxyCompletionStage interceptAfter(Function<Object[], ?> handlerAfter) {
		this.handlerAfter = (Function<Object[], Object>) handlerAfter;
		return this;
	}
	
	
	public ProxyCompletionStage interceptThrow(Consumer<? extends Throwable> handlerCatchError) {
		this.handlerCatchError = (args, error) -> {
			((Consumer<Throwable>) handlerCatchError).accept(error);
			return null;
		};
		return this;
	}
	
	public <ParamType> ProxyCompletionStage interceptThrow(Function<? extends Throwable, ?> handlerCatchError) {
		this.handlerCatchError = (args, error) -> ((Function<Throwable, Object>) handlerCatchError).apply(error);
		return this;
	}
	
	public <ParamType> ProxyCompletionStage interceptThrow(BiConsumer<Object[], ? extends Throwable> handlerCatchError) {
		this.handlerCatchError = (args, error) -> {
			((BiConsumer<Object[], Throwable>) handlerCatchError).accept(args, error);
			return null;
		};
		return this;
	}
	
	public <ParamType> ProxyCompletionStage interceptThrow(BiFunction<Object[], ? extends Throwable, ?> handlerCatchError) {
		this.handlerCatchError = (BiFunction<Object[], Throwable, Object>) handlerCatchError;
		return this;
	}
	
	public ProxyCompletionStage interceptFinally(Consumer<Object[]> handlerFinally) {
		this.handlerFinally = (args) -> {
			handlerFinally.accept(args);
			return null;
		};
		return this;
	}
	
	public ProxyCompletionStage interceptFinally(Function<Object[], ?> handlerFinally) {
		this.handlerFinally = (Function<Object[], Object>) handlerFinally;
		return this;
	}
	
	public ProxyCompletionStage interceptAfterReturn(BiConsumer<Object[], ?> handlerAfterReturn) {
		this.handlerAfterReturn = (BiConsumer<Object[], Object>) handlerAfterReturn;
		return this;
	}
	
	//////////////////////////////////////////////////////////////////
	
	public <R> R exec(Object inst, Object...args) throws Throwable {
		this.proxyInstance = inst;
		this.proxyMethodArguments = args;
		return ProxyUtils.exec(this);
	}
}

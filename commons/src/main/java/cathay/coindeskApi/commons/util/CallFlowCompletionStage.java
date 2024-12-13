package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.TypeUtils.isLamda;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import cathay.coindeskApi.commons.util.function.NoArgConsumer;

/**
 * ▪newInstance (constructor, type, innerType),
 * ▪get, set (field),
 * ▪call (method)
 * 
 * ▪中間運算
 *  [明確語意]
 *   - applyMethodArguments
 *  [廣義語意]
 *   - setArgument
 *   - bindThis
 *  
 * ▪計算結果:
 *  [明確語意]
 *   - getOperationResult
 *  [廣義語意]
 *   - get
 *   - orElseGet, orElse, orElseThrow
 *  
 * ▪Callback:
 *  onSuccess, OnError
 *  then (連接 "同屬性" 的handler)
 *  
 * ▪擴充功能:
 *  cast (導到別的型別的鏈式運算, 再導回來)
 */
@SuppressWarnings("unchecked")
public class CallFlowCompletionStage {

	private final Class<?> type;
	
	private final Class<?> enclosingType;

	// 如果是從其他地方導過來
	// CallFlowCompletionStage from;
	// TODO: cast to another stage (擴充新操作, 嵌入其他型別的操作)
	//Function<?, Object> cast;
	

	/* ---------- target ----------- */
	Object target;
	
	Constructor<?> constructor;
	Field field;
	Method method;
	Class<?> innerType;

	// this
	Object instance;
	// newInstance
	Object instanceOType;       // target是constructor
	Object instanceOfInnerType; // target是innerType
	
	// field
	Object fieldValue;
	
	// Arguments
	final Class<?>[] paramTypes;
	Object[] arguments;
	Collection<?> argumentCollection;
	Map<?, ?> argumentMappings;
	
	// 有找到的element, 匹配條件的目標物件
	Object hitTarget;
	// 有匹配某條件, 有找到某element
	Boolean hit = false;

	// execution
	Object operationResult;
	
	
	// 不同物件instance, 不同method, 不同參數 ➜ 都會重新設定此變數為null
	boolean isAnyCompletedCallNotHandled;
	boolean isAnyHitMissCallbackNotHandled;
	boolean isAnyErroNotHandled;
	
	// errors (constructor call, method call)
	Throwable exception;
	
	// callback (無誤地完成一項操作)
	Object onSuccessCallback;
	
	// hit
	Object hitCallback;
	// miss
	Object missCallback;
	// error handler
	Object onErrorHandler;
	
	static Consumer<String> onSuccessMessagePrinter = (msg) -> System.out.println(msg);
	static Consumer<String> onFailMessagePrinter = (msg) -> System.out.println(msg);
	
	/* ---------- Accesssors ---------- */
	// Set from outside:
	/*package*/ CallFlowCompletionStage setConstructor(Constructor<?> constructor) { this.constructor = constructor; this.target = constructor; return this; }
	/*package*/ CallFlowCompletionStage setField(Field field) { this.field = field; this.target = field; return this; }
	/*package*/ CallFlowCompletionStage setMethod(Method method) { this.method = method; this.target = method; return this; }
	/*package*/ CallFlowCompletionStage setInnerType(Class<?> innerType) { this.innerType = innerType; this.target = innerType; return this; }

	// Get:
	//public <T> Optional<Constructor<T>> getConstructor() { return Optional.ofNullable((Constructor<T>) this.constructor); }
	//public Optional<Field> getField() { return Optional.ofNullable(this.field); }
	//public Optional<Method> getMethod() { return Optional.ofNullable(this.method); }
	//public <T> Optional<Class<?>> getInnerType() { return Optional.ofNullable((Class<T>) this.innerType); }
	
	// Common
	public <T> Class<T> getType() { return (Class<T>) this.type; }
	public Class<?> getEnclosingType() { return this.type; }
	
	// Targets
	public <T> T getTarget() { return (T) this.target; }
	
	// Arguments
	public <T> CallFlowCompletionStage setArgument(T... arguments) { this.arguments = arguments; return this; }
	public <T> CallFlowCompletionStage setArgument(Collection<T> arguments) { this.argumentCollection = arguments; return this; }
	public <K,V> CallFlowCompletionStage setArgument(Map<K, V> arguments) { this.argumentMappings = arguments; return this; }

	// 綁定this
	public CallFlowCompletionStage bind(Object thiz) { this.instance = thiz; return this; }
	// innerType操作會用到
	public CallFlowCompletionStage bindEnclosingInstance(Object enclosingInstance) { this.instance = enclosingInstance; return this; }
	public Object getThis() { return this.instance; }

	// method call
	public <T> T getOperationResult() { return (T) this.operationResult; }
	
	// 更廣義的取值方式
	//public <T> T get() { return (T) this.operationResult; }
	
	
	public CallFlowCompletionStage(Class<?> type, Class<?>...initParamTypes) {
		Objects.requireNonNull(type, "type cannot be null.");
		this.type = type;
		this.paramTypes = initParamTypes;
		
		// 需要find default constructor ??
		this.target = type;
		this.enclosingType = type.getEnclosingClass();
	}

	////////////////////////////////////////////////////
	
	public <T> T getInstance() {
		Objects.requireNonNull(this.target, "this.target is null. The target can be 'Constructor', 'Field', 'Method' or 'Inner Type'.");
		if (this.constructor == this.target) {
			return (T) this.instance;	
		} else if (this.innerType == this.target) {
			return (T) this.instanceOfInnerType;	
		}
		return (T) this.instance;
	}
	
	public <T> T getInstanceOfInnerType() {
		Objects.requireNonNull(this.target, "this.target is null. The target can be 'Constructor', 'Field', 'Method' or 'Inner Type'.");
		if (this.innerType == this.target) {
			return (T) this.instanceOfInnerType;
		}
		return null;
	}

	
	/* ---------- Callback handlers ----------- */
	
	// internal setter
	CallFlowCompletionStage setOnSuccessCallback(Object successCallback) { this.onSuccessCallback = successCallback; return this; }
	// internal setter
	CallFlowCompletionStage setErrorHandler(Object errorHandler) { this.onErrorHandler = errorHandler; return this; }
	// internal setter
	CallFlowCompletionStage setHitCallback(Object hitCallback) { this.hitCallback = hitCallback; return this; }
	// internal setter
	CallFlowCompletionStage setMissCallback(Object missCallback) { this.missCallback = missCallback; return this; }
	
	/*package*/ CallFlowCompletionStage invokeSucessCallback() {
		if (this.onSuccessCallback == null)
			return this;
		
		// 未有"待呼叫"的callback, 則不需往下執行
		if (!isAnyCompletedCallNotHandled)
			return this;
		
		if (NoArgConsumer.class.isAssignableFrom(this.onSuccessCallback.getClass())) {
			((NoArgConsumer) this.onSuccessCallback).accept();
		}
		else if (Consumer.class.isAssignableFrom(this.onSuccessCallback.getClass())) {
			((Consumer<Object>) this.onSuccessCallback).accept(this.operationResult);
		}
		
		// clear suspension state
		this.isAnyCompletedCallNotHandled = false;
		return this;
	}
	
	/*package*/ CallFlowCompletionStage invokeHitMissCallback() {
		// 未有"待呼叫"的callback, 則不需往下執行
		if (!isAnyHitMissCallbackNotHandled)
			return this;
		
		if (this.hit) {
			if (this.hitCallback == null)
				return this;
			
			if (NoArgConsumer.class.isAssignableFrom(this.hitCallback.getClass())) {
				((NoArgConsumer) this.hitCallback).accept();
			}
			else if (Consumer.class.isAssignableFrom(this.hitCallback.getClass())) {
				((Consumer<Object>) this.hitCallback).accept(this.hitTarget);
			}
		}
		else {
			if (this.missCallback == null)
				return this;
			
			if (NoArgConsumer.class.isAssignableFrom(this.missCallback.getClass())) {
				((NoArgConsumer) this.missCallback).accept();
			}
			else if (Runnable.class.isAssignableFrom(this.missCallback.getClass())) {
				((Runnable) this.missCallback).run();
			}
		}
		
		// clear suspension state
		this.isAnyHitMissCallbackNotHandled = false;
		return this;
	}
	
	/*package*/ CallFlowCompletionStage invokeErrorHandler() {
		if (this.onErrorHandler == null)
			return this;
		
		// 未有"待呼叫"的錯誤處理, 則不需往下執行
		if (!isAnyErroNotHandled)
			return this;
		
		if (NoArgConsumer.class.isAssignableFrom(this.onErrorHandler.getClass())) {
			((NoArgConsumer) this.onErrorHandler).accept();
		}
		else if (Consumer.class.isAssignableFrom(this.onErrorHandler.getClass())) {
			((Consumer<Throwable>) this.onErrorHandler).accept(this.exception);
		}
		
		// clear suspension state
		this.isAnyErroNotHandled = false;
		return this;
	}
	
	
	/* ---------- Instantiation ----------- */
	public CallFlowCompletionStage newInstance(Object...args) {
		// check target existence
		Objects.requireNonNull(this.target, "this.target is null. The target can be 'Constructor', 'Field', 'Method' or 'Inner Type'.");
		
		// 只有type就好不行嗎？一定要有target嗎？
		if (this.target == null) {
			
		}
		
		// constructor
		if (this.constructor == this.target) {
			try {
				this.instance = this.constructor.newInstance(args);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
					 InvocationTargetException e) {
				this.exception = e;
			}
			return this;
		}
		// innerType
		else if (this.innerType == this.target) {
			if (this.target == this.innerType) {
				final Object instanceOfEnclosingType = this.instance;
				
				boolean isStatic = isStatic(this.innerType.getModifiers());
				if (!isStatic) {
					// TODO:
				}
				
				// Prepare constructor arguments
				ArrayList<Object> initArgs = new ArrayList<Object>(); 
				if (!isStatic) {
					Objects.requireNonNull(this.instance, "建立nonstatic inner type之物件時, enclosing type的實體不可為null");
					initArgs.add(instanceOfEnclosingType);
				}
				initArgs.addAll(asList(args));
				
				
				Constructor<?> ctorInnerType = null;
				try {
					// 取得innerType建構子
					ctorInnerType = this.innerType.getDeclaredConstructor(isStatic? new Class[0] : new Class[] {this.type});
					// instantiation
					this.instanceOfInnerType = ctorInnerType.newInstance(initArgs.toArray(Object[]::new));
				}
				// 類別載入時的 object init block {} 執行發生錯誤
				catch (ExceptionInInitializerError e) {
					this.exception = e;
				}
				// 未找到constructor, 或是牴觸到security policy的規則
				catch (NoSuchMethodException | SecurityException e) {
					this.exception = e;
				}
				// 欲建立 "抽象"型別
				catch (InstantiationException e) {
					this.exception = e;
				}
				// 執行建構子時發生錯誤 (尚未進入constructor)
				catch (IllegalAccessException | IllegalArgumentException e) {
					this.exception = e;
				}
				// 建構子"本身" 執行的錯誤 (已經進入constructor)
				catch (InvocationTargetException e) {
					this.exception = e;
				}
			}
			return this;
		}
		throw new UnsupportedOperationException("newInstance() can be done only for the target of 'Constructor'");
	}
	
	/*package*/ CallFlowCompletionStage applyMethodArguments(Object...args) {
		if (args != null && args.length > 0)
			this.arguments = args;
		return this;
	}
	
	public CallFlowCompletionStage call(Object... args) { return call(args, null); }
	
	public CallFlowCompletionStage call(Consumer<?> callback) { return call(new Object[0], callback); }
	
	public CallFlowCompletionStage call(Object arg1, Consumer<?> callback) { return call(new Object[] {arg1}, callback); }
	
	public CallFlowCompletionStage call(Object arg1, Object arg2, Consumer<?> callback) { return call(new Object[] {arg1, arg2}, callback); }
	
	public CallFlowCompletionStage call(Object[] args, Consumer<?> callback) {
		//if (isAnyCompletedCallNotHandled) {
		//    // 是否需判斷 this.isAnyCompletedCallNotHandled, 而決定是否要往下執行
		//}
		
		// check parameter integrity
		Objects.requireNonNull(this.method, "this.method is not present.");
		
		// 檢查傳入的參數, 和 this.method 接受的參數, 是否一致?
		// 如果不一致, 印出警告訊息
		
		// 優先使用傳入的args
		applyMethodArguments(args);

		// 優先使用傳入的callback
		if (callback != null && isLamda(callback))
			this.onSuccessCallback = callback;
		
		try {
			// 此區塊是否要 "非同步" 執行??
			try {
				if (isStatic(this.method.getModifiers()))
					this.operationResult = this.method.invoke(this.instance, this.arguments);
				else
					this.operationResult = this.method.invoke(this.instance, this.arguments);	
				
				// set flag
				this.isAnyCompletedCallNotHandled = true;
			}			
			catch (ExceptionInInitializerError e) {
				// class載入過程 static {} , object init block {}
				this.exception = e;
				this.isAnyErroNotHandled = true;
				return this;
			}
			catch (IllegalAccessException | IllegalArgumentException e) {
				// Java reflection引發的exception
				this.exception = e;
				this.isAnyErroNotHandled = true;
				return this;
			}
			catch (InvocationTargetException e) {
				// 執行的方法對象拋出出錯誤
				this.exception = e;
				this.isAnyErroNotHandled = true;
				return this;
			}
			
			// TODO: 此處的呼叫, 會和then打架??
			invokeSucessCallback();
		}
		catch (Throwable t) {
			// TODO errors to handle (此method本身引發的)
			this.exception = t;
			this.isAnyErroNotHandled = true;
		}
		return this;
	}
	
	// 集合/陣列操作, find, 判斷, .....
	
	public CallFlowCompletionStage then(NoArgConsumer onSuccess) { return then((Object) onSuccess); }
	public CallFlowCompletionStage then(Consumer<?> onSuccess) { return then((Object) onSuccess); }
	private CallFlowCompletionStage then(Object onSuccess) {
		/*
		 * TODO: lock until:
		 *  this.isAnyCompletedCallNotHandled is true
		 */
		return handleCallback(onSuccess, this.isAnyCompletedCallNotHandled, this::setOnSuccessCallback, this::invokeSucessCallback);
	}
	
	/**
	 * 有找到某element, 有匹配某條件
	 */
	public CallFlowCompletionStage onHit(NoArgConsumer hitCallback) { return onHit((Object) hitCallback); }
	public CallFlowCompletionStage onHit(Consumer<?> hitCallback) { return onHit((Object) hitCallback); }
	private CallFlowCompletionStage onHit(Object hitCallback) {
		/*
		 * TODO: lock until:
		 *  this.isAnyCompletedCallNotHandled is true
		 */
		return handleCallback(hitCallback, this.isAnyHitMissCallbackNotHandled, this::setHitCallback, this::invokeHitMissCallback);
	}
	
	/**
	 * "未"找到某element, "未"匹配某條件
	 */
	public CallFlowCompletionStage onMiss(NoArgConsumer missCallback) { return onMiss((Object) missCallback); }
	public CallFlowCompletionStage onMiss(Runnable missCallback) { return onMiss((Object) missCallback); }
	private CallFlowCompletionStage onMiss(Object missCallback) {
		/*
		 * TODO: lock until:
		 *  this.isAnyCompletedCallNotHandled is true
		 */
		return handleCallback(missCallback, this.isAnyHitMissCallbackNotHandled, this::setMissCallback, this::invokeHitMissCallback);
	}
	
	public CallFlowCompletionStage onError(Consumer<? extends Throwable> errorHandler) { return onError((Object) errorHandler); }
	public CallFlowCompletionStage onError(NoArgConsumer errorHandler) { return onError((Object) errorHandler); }
	private CallFlowCompletionStage onError(Object errorHandler) {
		/*
		 * TODO: lock until:
		 *  this.isAnyErroNotHandled is true
		 */
		return handleCallback(errorHandler, this.isAnyErroNotHandled, this::setErrorHandler, this::invokeErrorHandler);
	}
	
	/*package*/ CallFlowCompletionStage handleCallback(Object handler, boolean isHandledFlag, Consumer<Object> handlerSetter, NoArgConsumer performer) {
		if (handler == null)
			throw new NullPointerException("The argument 'handler' cannot be null.");
		else if (!isLamda(handler))
			throw new IllegalArgumentException("The argument 'handler' must be a Lamda type");
		
		// set callback
		handlerSetter.accept(handler);
		
		// callback
		if (isHandledFlag) {
			// TODO: 要立即執行? 或以非同步方式執行?
			performer.accept();
		}
		return this;
	}
	
	
	/* ---------- 設值方式 (field) ----------- */		
	public CallFlowCompletionStage set(String key, Object value) {
		Objects.requireNonNull(this.target, "this.target is null.");
		// map
		// pojo
		return this;
	}
	
	/**
	 * 僅field可用
	 */
	public CallFlowCompletionStage set(Object value) {
		Objects.requireNonNull(this.target, "this.target is null, no present target available.");
		// 僅有field能操作
		if (this.field == this.target) {
			Objects.requireNonNull(this.instance, "'this' is not present.");
			try {
				this.fieldValue = value;
				this.field.set(this.instance, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO errors to handle
				e.printStackTrace();
			}	
		}
		else if (Map.class.isAssignableFrom(this.target.getClass())) {
			// Map
		}
		else {
			// POJO
		}
		return this;
	}
	
	/* ---------- 取值方式 ----------- */
	
	/**
	 * ▪newInstance (constructor, type, innerType),
	 * ▪get, set (field),
	 * ▪call (method)
	 * 
	 * ▪中間運算
	 *  setArgument
	 *  bindThis
	 *  
	 * ▪計算結果
	 *  getOperationResult
	 *  
	 * ▪cast (導到別的型別的鏈式運算, 再導回來)
	 */
	public <T> T get() {
		Objects.requireNonNull(this.target, "this.target is null. The target can be 'Constructor', 'Field', 'Method' or 'Inner Type'.");
		
		Object result = null;
		if (this.type == this.target) {
			// ????
		}
		else if (this.constructor == this.target) {
			result = this.instance;
		}
		else if (this.field == this.target) {
			try {
				this.fieldValue = this.field.get(this.instance);
				result = this.fieldValue;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO errors to handle
				e.printStackTrace();
			}
		}
		else if (this.method == this.target) {
			result = this.operationResult;
			// 設定為均已處理
			this.isAnyCompletedCallNotHandled = false;
		}
		else if (this.innerType == this.target) {
			result = this.innerType;
		}
		return (T) result;
	}
	
	// 集合: array, Collection
	//public Object get(int index) {
	//	// TODO: 存取的是 instance, instanceOfInnerType 或 methodReturnedValue ?
	//	this.isAnyCompletedCallNotHandled = false;
	//	throw new UnsupportedOperationException();
	//}
	
	// map, POJO
	//public <T> T get(String... propertyNames) {
	//	// TODO: 存取的是 instance, instanceOfInnerType 或 methodReturnedValue ?
	//	this.isAnyCompletedCallNotHandled = false;
	//	throw new UnsupportedOperationException();
	//}
	
	
	/* ---------- Task ----------- */
	ArrayList<Object> thenTasks = new ArrayList<Object>();
	
	/*package*/ synchronized CallFlowCompletionStage runThenTasks() {
		if (!this.isAnyCompletedCallNotHandled)
			// do nothing
			return this;
		
		for (Object task : thenTasks) {
			if (Consumer.class.isAssignableFrom(task.getClass())) {
				((Consumer<Object>) task).accept(null/* TODO:傳入什麼值? */);
			}
			else if (NoArgConsumer.class.isAssignableFrom(task.getClass())) {
				((NoArgConsumer) task).accept();
			}
			else if (Runnable.class.isAssignableFrom(task.getClass())) {
				((Runnable) task).run();
			}
		}
		thenTasks.clear();
		return this;
	}
	
	/**
	 * 注意: task內部可能有"阻塞"行為
	 */
	/*package*/ CallFlowCompletionStage addThen(Object task) {
		// check non-null
		Objects.requireNonNull(task, "The argument 'task' cannot be null");
		
		// check if it is Runnable or Lamda
		if (!isLamda(task))
			throw new IllegalArgumentException("task must be a lamda express, i.e. Runnable, Consumer, or any type annotated with @FunctionalInterface.");
		
		thenTasks.add(task);
		return this;
	}
	
	public CallFlowCompletionStage clear() {
		this.target = null;
		this.constructor = null;
		this.field = null;
		this.method = null;
		this.innerType = null;
		
		// this
		this.instance = null;
		// created instances
		this.instanceOType = null;
		this.instanceOfInnerType = null;
		
		// field
		this.fieldValue = null;
		
		// argument
		this.arguments = null;
		this.argumentCollection = null;
		this.argumentMappings = null;
		
		// operation result
		this.operationResult = null;
		
		// status
		this.isAnyCompletedCallNotHandled = false;
		
		// exception:
		this.exception = null;
		
		// Tasks
		if (this.thenTasks != null)
			this.thenTasks.clear();
		this.thenTasks = null;
		
		// callbacks
		this.onSuccessCallback = null;
		this.onErrorHandler = null;
		return this;
	}
}

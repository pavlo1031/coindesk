package cathay.coindeskApi.commons.util.validate;

import static cathay.coindeskApi.commons.util.ExceptionUtils.filterStackTraceElements;
import static cathay.coindeskApi.commons.util.ExceptionUtils.setCause;
import static cathay.coindeskApi.commons.util.ExceptionUtils.setStackTraceElements;
import static cathay.coindeskApi.commons.util.validate.Hoc.Fails.failMessage;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Function;

import cathay.coindeskApi.commons.util.CastClassPair;
import cathay.coindeskApi.commons.util.function.NoArgConsumer;

public class ValidationUtils {
	
	public static <T> T checkCondition(T data, java.util.function.Predicate<? super T> condition, String failMessage) {
		return checkCondition(
				data,
				condition,
				failMessage(failMessage),
				(Function<T, T>) null
		);
	}
	
	public static <T> T checkCondition(T data, java.util.function.Predicate<? super T> condition, String failMessage, Consumer<?> successThen) {
		return checkCondition(
				data,
				condition,
				failMessage(failMessage),
				(t) -> {
					if (successThen != null)
						((Consumer<Object>) successThen).accept(t);
					return (T) t;
				}
		);
	}
	
	public static <T> T checkCondition(T data, java.util.function.Predicate<? super T> condition, String failMessage, NoArgConsumer successThen) {
		return checkCondition(
				data,
				condition,
				failMessage(failMessage),
				(t) -> {
					if (successThen != null)
						successThen.accept();
					return (T) t;
				}
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, String failMessage, Function<?, R> successThenRetun) {
		return checkCondition(
				data,
				condition,
				failMessage(failMessage),
				successThenRetun
		);
	}
	
	//////////
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Function<?, R> failsThen) {
		return checkCondition(
				data,
				condition,
				failsThen,             // failsThen
				(Function<T, R>) null  // successThen
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Function<?, R> failsThen, Consumer<?> successThen) {
		return checkCondition(
				data,
				condition,
				failsThen,
				(t) -> {
					if (successThen != null)
						((Consumer) successThen).accept(t);
					return (R) t;
				}
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Function<?, R> failsThen, NoArgConsumer successThen) {
		return checkCondition(
				data,
				condition,
				failsThen,
				(t) -> {
					if (successThen != null)
						successThen.accept();
					return (R) t;
				}
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Consumer<?> failsThen, Consumer<?> successThen) {
		return checkCondition(
				data,
				condition,
				(t) -> {
					if (failsThen != null)
						((Consumer) failsThen).accept(t);
					return (R) t;
				},
				(t) -> {
					if (successThen != null)
						((Consumer) successThen).accept(t);
					return (R) t;
				}
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Consumer<?> failsThen, NoArgConsumer successThen) {
		return checkCondition(
				data,
				condition,
				(t) -> {
					if (failsThen != null)
						((Consumer) failsThen).accept(t);
					return (R) t;
				},
				(t) -> {
					if (successThen != null)
						successThen.accept();
					return (R) t;
				}
		);
	}
	
	public static <T, R> R checkCondition(T data, java.util.function.Predicate<? super T> condition, Function<?, R> failsThenReturn, Function<?, R> successThenReturn) {
		// data不一定要"非null" ➜ 檢核對象可能是: 系統/環境變數
		requireNonNull(condition, "The argument 'condition' cannot be null.");
		
		// callback 'theReturn' 回傳值
		R returnValue = null;
		
		// 記錄例外來源 (複合檢核過程失敗時)
		Throwable cause = null;
		
		// 全部檢核函數都執行完, 沒有中途離開
		boolean allPredicatesCompleted = false;
		
		boolean pass = false;
		try {
			pass = ((java.util.function.Predicate<Object>) condition).test(data);
			// 有完整執行完檢核流程
			allPredicatesCompleted = true;
		}
		catch (Exception e) {
			if (!ShortCircuitException.class.isAssignableFrom(e.getClass())) {
				cause = e;
				return __failsThen(data, failsThenReturn, cause);
			}			

			// 先行離開的狀況: 取出判定的檢核結果
			pass = ((ShortCircuitException) e).getResult();
			if (pass)
				return returnValue = (R) data;
			
			// 取出檢核失敗資訊
			cause = e.getCause();
		}
		
		// 成功
		if (pass) {
			if (successThenReturn != null)
				return returnValue = ((Function<Object, R>) successThenReturn).apply(data);
			// No callback given, return the data
			return (R) data;
		}
		// 失敗: 
		else {
			return __failsThen(data, failsThenReturn, cause);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	
	private static final Predicate<StackTraceElement> FilterStackTraceElements = (stackElement) -> {
		return "app".equals(stackElement.getClassLoaderName());
	};
	
	private static <T, R> R __failsThen(T data, Function<?, ?> failsThenThrow, Throwable cause) throws RuntimeException {
		return __failsThen(data, failsThenThrow, cause, null);
	}
	
	private static <T, R> R __failsThen(T data, Function<?, ?> failsThen, Throwable cause, StackTraceElement[] stackTraceElements) throws RuntimeException {
		// 預設傳回data (未指定callback)
		if (failsThen == null)
			return (R) data;
		
		// 由於failsThen型別為Function<?,?>, input Type為"任意"
		// ➜ 此行可能發生ClassCastException
		Object returnValue = null;
		try {
			returnValue = ((Function) failsThen).apply(data);
		} catch (ClassCastException e) {
			CastClassPair castClassPair = CastClassPair.of(e);
			throw new IllegalArgumentException("傳入的failsThen參數型別%s, 無法轉型為%s".formatted(castClassPair.fromClass.getName(), castClassPair.toClass.getName()), e);
		}
		
		if (returnValue == null)
			return (R) null;
		
		if (returnValue instanceof RuntimeException) {
			RuntimeException returnedException = (RuntimeException) returnValue;

			/* Stack Trace elements */
			if (stackTraceElements != null) {
				// 允許自行傳入exception過程
				setStackTraceElements(returnedException, stackTraceElements, FilterStackTraceElements);
			} else {
				// 如果不傳入stackTraceElements
				// ➜ 濾除對象是exception本身的stackTraceElement
				//    (濾除: 太深/相關性不足的節點)
				filterStackTraceElements(returnedException, FilterStackTraceElements);
			}
			
			/* Cause */
			if (cause != null) {
				// 濾除: 太深/相關性不足的節點
				//       (濾除對象是「外部傳入的cause」的stackTraceElement)
				filterStackTraceElements(cause, FilterStackTraceElements);
				
				// 設定cause
				try {
					throw setCause(returnedException, cause);
				}
				catch (IllegalStateException setCauseError) {
					final String errorMessage = returnedException.getMessage();
					final Class<? extends RuntimeException> exceptionClass = returnedException.getClass();
					RuntimeException newException = null;
					try {
						Constructor<? extends RuntimeException> ctor = (Constructor<RuntimeException>) exceptionClass.getConstructor((errorMessage != null)? new Class[] {String.class, Throwable.class} : new Class[] {Throwable.class});
						newException = ctor.newInstance((errorMessage != null)? new Object[] {errorMessage, cause} : new Object[] {cause});
						
						// 同時也更新stack trace
						newException.setStackTrace(returnedException.getStackTrace());
					}
					catch (NoSuchMethodException | SecurityException  e) {
						// FIXME find constructor 失敗
					}
					catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						// FIXME 呼叫失敗
					}
					returnedException = newException;
				}
			}
			throw returnedException;
		}
		return (R) returnValue;
	}

	///////////////////////////////////////////////////////////////////////////
	
    public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
    	return and((java.util.function.Predicate<? super T>[]) predicates);
    }
    
    public static <T> Predicate<T> and(java.util.function.Predicate<? super T>... predicates) {
        requireNonNull(predicates);
    	return (t) -> {
    		if (predicates.length == 0)
    			return false;
    		
    		RuntimeException exception = null;
    		for (int i=0; i<predicates.length; i++) {
    			java.util.function.Predicate p = predicates[i];
    			try {
	    			if (!p.test(t)) {
	    				if (/*cathay.coindeskApi.commons.util.validate.Predicate.class.isAssignableFrom(p.getClass()) &&*/ i < predicates.length-1)
	    					throw new ShortCircuitException("在第" + (i+1) + "個條件判斷為false後, 提早離開", false);
	    				return false;
	    			}
	    			
    			} catch (RuntimeException e) {
    				// 記下最後發生的exception
    				exception = e;
    				
    				// 是"提早"離開的狀況
					if (/*cathay.coindeskApi.commons.util.validate.Predicate.class.isAssignableFrom(p.getClass()) &&*/ i < predicates.length-1)
    					throw new ShortCircuitException("在第" + (i+1) + "個條件判斷為false後, 提早離開", false, e);
    			}
    		}
    		
    		if (exception != null)
    			throw exception;
    		return true;
    	};
    }
    

    public static <T> Predicate<T> or(Predicate<? super T>... predicates) {
    	return or((java.util.function.Predicate<? super T>[]) predicates);
    }
    
    public static <T> Predicate<T> or(java.util.function.Predicate<? super T>... predicates) {
        requireNonNull(predicates, "The argument 'predicates' cannot be null.");
    	return (t) -> {
    		if (predicates.length == 0)
    			return false;
    		
    		RuntimeException exception = null;
    		for (int i=0; i<predicates.length; i++) {
    			java.util.function.Predicate p = predicates[i];
    			try {
	    			if (p.test(t)) {
	    				if (/*cathay.coindeskApi.commons.util.validate.Predicate.class.isAssignableFrom(p.getClass()) &&*/ i < predicates.length-1)
	    					throw new ShortCircuitException("在第" + (i+1) + "個條件判斷為true後, 提早離開", true);
	    				return true;
	    			}
	    			
    			} catch (RuntimeException e) {
    				// 記下最後發生的exception
    				exception = e;
    				
    				// 是"提早"離開的狀況
    				if (/*cathay.coindeskApi.commons.util.validate.Predicate.class.isAssignableFrom(p.getClass()) &&*/ i < predicates.length-1)
    					throw new ShortCircuitException("在第" + (i+1) + "個條件判斷為false後, 提早離開", false, e);
    			}
    		}
    		
    		if (exception != null)
    			throw exception;
    		return false;
    	};
    }
}

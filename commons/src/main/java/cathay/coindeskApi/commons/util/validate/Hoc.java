package cathay.coindeskApi.commons.util.validate;

import static cathay.coindeskApi.commons.util.MultiElementUtils.getLength;
import static cathay.coindeskApi.commons.util.StringUtils.quoteString;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import cathay.coindeskApi.commons.util.MultiElementUtils;

/**
 * Validation 檢核用途的Hoc
 */
public class Hoc {

	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);

	/**
	 * 檢核成功的處理
	 */
	public static class Success {
		
		public static <T, R> Function<? super T, R> successThen(Consumer<?> successThen) {
			return (T t) -> {
				if (successThen != null)
					((Consumer) successThen).accept(t);
				return (R) t;
			};
		}
		
		public static <T, R> Function<? super T, R> successThenReturn(Function<?, ?> successThenReturn) {
			return (T t) -> {
				if (successThenReturn != null)
					return ((Function<Object, R>) successThenReturn).apply(t);	
				return (R) t;
			};
		}
	}
	
	/**
	 * 檢核失敗的處理
	 */
	public static class Fails {

		public static <T, R> Function<? super T, R> failsThen(Consumer<?> failsThen) {
			return (T t) -> {
				if (failsThen != null)
					((Consumer) failsThen).accept(t);
				return (R) t;
			};
		}
		
		public static <T, R> Function<? super T, R> failsThenReturn(Function<?, ?> failsThenReturn) {
			return (T t) -> {
				if (failsThenReturn != null)
					return ((Function<Object, R>) failsThenReturn).apply(t);
				return (R) t;
			};
		}

		public static <T> Function<? super T, ?> failMessage(String errorMessage) {
			return failMessageAndErrorClass(errorMessage, null);
		}
		
		public static <T> Function<? super T, ?> failMessage(Function<? super T, String> errorMessageSupplier) {
			return failMessageAndErrorClass(errorMessageSupplier, null);
		}
		
		public static <T> Function<? super T, ?> failMessageAndErrorClass(String errorMessage, Class<? extends RuntimeException> errorClass) {
			return failMessageAndErrorClass((t) -> errorMessage, errorClass);
		}
		
		/**
		 * create exception instance, but not throwing it
		 */
		public static <T> Function<? super T, ?> failMessageAndErrorClass(Function<? super T, String> errorMessageSupplier, Class<? extends RuntimeException> errorClass) {
			AtomicReference<Class<? extends RuntimeException>> errorClassRef = new AtomicReference((errorClass != null)? errorClass : IllegalArgumentException.class);
			
			// Workaround: 避開「lamda內使用的變數必須為final」的問題
			AtomicReference<String> errorMessageRef = new AtomicReference();
			
			// obtain constructor of errorClass
			Constructor<? extends RuntimeException> ctor;
			try {
				ctor = errorClassRef.get().getDeclaredConstructor((errorMessageSupplier != null)? new Class[] {String.class} : new Class[0]);
				return (T t) -> {
					// error message
					errorMessageRef.set(((Function<T, String>) errorMessageSupplier).apply(t));
					
					// create instance of errorClass
					try {
						return ctor.newInstance((errorMessageRef != null)
												 ? new Object[] { errorMessageRef.get() } : new Object[0]);
					}
					catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | ExceptionInInitializerError instantiateError) {
						// FIXME: 失敗: constructor存取權限不足, 參數不正確
						throw new IllegalStateException("無法建立exception, type = " + errorClassRef.get().getName(), instantiateError);
					}
				};
			}
			catch (NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException(errorClass.getName() + "型別中無相應參數列的建構子: " + ((errorMessageSupplier != null)? "constructor(String)":"constructor()"));
			}
		}
	}

	public static class Validator {
		
		public static <T> Predicate<T> isNull() {
			return isNull(null, null);
		}
		
		public static <T> Predicate<T> isNull(String variableName) {
			return isNull(variableName, null);
		}
		
		public static <T> Predicate<T> isNull(String variableName, String errorMsg) {
			StringBuilder buffer = threadLocalBuffer.get();
			if (isNoneEmpty(errorMsg))
				buffer.append(errorMsg);
			else
				buffer.append("The argument ").append(isNoneEmpty(variableName)? "%s ":"").append("must be null");
			
			try {
				return doReturnHocFlow(variableName, IllegalArgumentException.class, buffer.toString(), (o) -> o == null);
			} finally {
				buffer.setLength(0);
			}
		}
		
		//////////////////////////////////////////////////
		
		public static <T> Predicate<T> isNotNull() {
			return isNotNull(null, null);
		}
		
		public static <T> Predicate<T> isNotNull(String variableName) {
			return isNotNull(variableName, null);
		}
		
		public static <T> Predicate<T> isNotNull(String variableName, String errorMsg) {
			StringBuilder buffer = threadLocalBuffer.get();
			if (isNoneEmpty(errorMsg))
				buffer.append(errorMsg);
			else
				buffer.append("The argument ").append(isNoneEmpty(variableName)? "%s ":"").append("cannot be null");
			
			try {
				return doReturnHocFlow(variableName, NullPointerException.class, buffer.toString(), (o) -> o != null);
			} finally {
				buffer.setLength(0);
			}
		}
		
		///////////////////////////////// 數值 /////////////////////////////////
	
		/*package*/ static Predicate numberEquals(Number constant) {
			return numberEquals((String) null, constant);
		}
		
		/*package*/ static Predicate numberEquals(String variableName, Number constant) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName)? "%s ":"")
														  .append("must equal to ").append(constant);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return Float.compare(n.floatValue(), constant.floatValue()) == 0;
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static Predicate numberGreaterThan(Number constant) {
			return numberGreaterThan((String) null, constant);
		}
		
		public static Predicate numberGreaterThan(String variableName, Number constant) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName)? "%s ":"")
														  .append("must be greater than ").append(constant);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return Float.compare(n.floatValue(), constant.floatValue()) > 0;
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
	
		public static Predicate numberGreaterOrEqualTo(Number constant) {
			return numberGreaterOrEqualTo((String) null, constant);
		}
		
		public static Predicate numberGreaterOrEqualTo(String variableName, Number constant) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName)? "%s ":"")
														  .append("must be greater or equal to ").append(constant);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return Float.compare(n.floatValue(), constant.floatValue()) >= 0;
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static Predicate numberLessThan(Number constant) {
			return numberLessThan((String) null, constant);
		}
		
		public static Predicate numberLessThan(String variableName, Number constant) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must be less than ").append(constant);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return Float.compare(n.floatValue(), constant.floatValue()) < 0;
				});
			} finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static Predicate numberLessOrEqualTo(Number constant) {
			return numberLessOrEqualTo((String) null, constant);
		}
		
		public static Predicate numberLessOrEqualTo(String variableName, Number constant) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must be less or equal to ").append(constant);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return Float.compare(n.floatValue(), constant.floatValue()) <= 0;
				});
			} finally {
				buffer.setLength(0);
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////
		
		public static Predicate numberMatches(Predicate<? extends Number> condition) {
			return numberMatches(null, condition);
		}
		
		public static Predicate numberMatches(String variableName, Predicate<? extends Number> condition) {
			return numberMatches(null, condition, null);
		}
		
		public static Predicate numberMatches(String variableName, Predicate<? extends Number> condition, String conditionDescription) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must match the condition");
			if (isNoneEmpty(conditionDescription)) {
				buffer.append(": ").append(quoteString(conditionDescription));
			} else {
				buffer.append('.');
			}
			
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Number n) -> {
					if (n == null)
						return false;
					return ((Predicate<Number>) condition).test(n);
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		//////////////////////////// 容器 (array, collection) ////////////////////////////
	
		public static <T> Predicate<T> sizeEquals(int size) {
			return sizeEquals((String) null, size);
		}
		
		public static <T> Predicate<T> sizeEquals(String variableName, int size) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "").append("(which is a container) ")
														  .append("must have a size of ").append(size);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T argument) -> {
					if (argument == null)
						return false;
					
					final Class<?> argumentClass = argument.getClass();
					if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
						return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) == size);
					}
					throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static <T> Predicate<T> sizeGreaterThan(int minSize) {
			return sizeGreaterThan((String) null, minSize);
		}
		
		public static <T> Predicate<T> sizeGreaterThan(String variableName, int minSizeExlusive) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "").append("(which is a container) ")
														  .append("must have a size > ").append(minSizeExlusive);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T argument) -> {
					if (argument == null)
						return false;
					
					final Class<?> argumentClass = argument.getClass();
					if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
						return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) > minSizeExlusive);
					}
					throw new IllegalArgumentException("sizeGreaterThan(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static <T> Predicate<T> sizeGreaterOrEqualTo(int minSize) {
			return sizeGreaterThan((String) null, minSize);
		}
		
		public static <T> Predicate<T> sizeGreaterOrEqualTo(String variableName, int minSize) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "").append("(which is a container) ")
														  .append("must have a size >= ").append(minSize);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T argument) -> {
					if (argument == null)
						return false;
					
					final Class<?> argumentClass = argument.getClass();
					if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
						return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) >= minSize);
					}
					throw new IllegalArgumentException("sizeGreaterOrEqualTo(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static <T> Predicate<T> sizeLessThan(int upboundExclusive) {
			return sizeLessThan((String) null, upboundExclusive);
		}
		
		public static <T> Predicate<T> sizeLessThan(String variableName, int upboundExclusive) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "").append("(which is a container) ")
														  .append("must have a size < ").append(upboundExclusive);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T argument) -> {
					if (argument == null)
						return false;
					
					final Class<?> argumentClass = argument.getClass();
					if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
						return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) < upboundExclusive);
					}
					throw new IllegalArgumentException("sizeLessThan(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static <T> Predicate<T> sizeLessOrEqualTo(int upbound) {
			return sizeLessOrEqualTo((String) null, upbound);
		}
		
		public static <T> Predicate<T> sizeLessOrEqualTo(String variableName, int upbound) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "").append("(which is a container) ")
														  .append("must have a size <= ").append(upbound);
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T argument) -> {
					if (argument == null)
						return false;
					
					final Class<?> argumentClass = argument.getClass();
					if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
						return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) <= upbound);
					}
					throw new IllegalArgumentException("sizeLessOrEqualTo(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		////////////////
		
		public static <T> Predicate<T> existsIn(Object... constants) {
			return existsIn((String) null, constants);
		}
		
		public static <T> Predicate<T> existsIn(String variableName, Object... constants) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must exist in the array.");
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T data) -> {
					if (data == null)
						return false;
					for (int i=0; i < constants.length; i++)
						if (data.equals(constants[i]))
							return true;
					return false;
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		public static <T> Predicate<T> existsIn(Collection collection) {
			return existsIn((String) null, Arrays.asList(collection));
		}
		
		public static <T> Predicate<T> existsIn(String variableName, Collection collection) {
			StringBuilder buffer = threadLocalBuffer.get().append("The argument ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must exist in the collection.");
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (T data) -> {
					if (data == null)
						return false;
					return collection.stream().anyMatch((elem) -> data.equals(elem));
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		//////////////////////////// 容器 (map) /////////////////////////////
		
		public static Predicate containsKey(Object key) {
			return containsKey((String) null, key);
		}
		
		public static Predicate containsKey(String variableName, Object key) {
			StringBuilder buffer = threadLocalBuffer.get().append("The map ")
														  .append(isNoneEmpty(variableName) ? "%s " : "")
														  .append("must have the key ").append(quoteString(key));
			try {
				return doReturnHocFlow(variableName, buffer.toString(), (Map map) -> {
					if (map == null)
						return false;
					return map.containsKey(key);
				});
			}
			finally {
				buffer.setLength(0);
			}
		}
		
		/**
		 * 「hoc執行流程」
		 *        
		 * @param variableName 待檢查目標物件之變數名稱
		 * @param predicate 真正要執行的predicate
		 */
		private static <T> Predicate<T> doReturnHocFlow(String variableName, Predicate<? super T> predicate) {
			return doReturnHocFlow(variableName, IllegalArgumentException.class, "incorrect argument " + (isNoneEmpty(variableName)? "%s":""), predicate);
		}
		
		/**
		 * 「hoc執行流程」
		 *  (指定變數名稱, 錯誤訊息)
		 *        
		 * @param variableName 待檢查目標物件之變數名稱
		 * @param errMessage 錯誤訊息(可含有%格式, 表示法請參考System.printf)
		 * @param predicate 真正要執行的predicate
		 */
		private static <T> Predicate<T> doReturnHocFlow(String variableName, String errorMessage, Predicate<? super T> predicate) {
			return doReturnHocFlow(variableName, IllegalArgumentException.class, errorMessage, predicate);
		}
		
		/**
		 * 「hoc執行流程」之實現
		 *  (指定變數名稱, 錯誤類別, 錯誤訊息)
		 *        
		 * @param variableName 待檢查目標物件之變數名稱
		 * @param orElseThrow predicate執行結果為false時, 拋出此錯誤
		 * @param errMessage 錯誤訊息(可含有%格式, 表示法請參考System.printf)
		 * @param predicate 真正要執行的predicate
		 */
		private static <T> Predicate<T> doReturnHocFlow(String variableName, final Class<? extends RuntimeException> orElseThrow, String errorMessage, Predicate<? super T> predicate) {
			return (o) -> {
				if (predicate.test(o))
					return true;
				
				if (orElseThrow == null)
					return false;
				
				// build error message
				String resolvedErrMessage = String.format(errorMessage,
											 isNoneEmpty(variableName)? format("'%s'", variableName) : "");
				
				// create instance of errorClass and throw
				Constructor<? extends RuntimeException> ctor = null;
				try {
					ctor = orElseThrow.getDeclaredConstructor(String.class);
					throw ctor.newInstance(resolvedErrMessage);
				}
				catch (NoSuchMethodException | InvocationTargetException | SecurityException | InstantiationException | IllegalAccessException e) {
					throw new NullPointerException(resolvedErrMessage);
				}
			};
		}
	}
}

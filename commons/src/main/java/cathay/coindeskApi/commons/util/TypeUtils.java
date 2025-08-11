package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.containsMixedTypes;
import static cathay.coindeskApi.commons.util.ArrayUtils.getDimension;
import static cathay.coindeskApi.commons.util.MultiElementUtils.getLength;
import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.or;
import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.sizeGreaterThan;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.checkCondition;
import static java.util.Arrays.stream;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypeUtils {
	
	/**
	 * 高階函數都定義在此處
	 */
	public static class Hoc {
		
	}
	
	public static boolean isVarargsWrapped(Object[] values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		// 檢查: 不可為空否則無法判斷
		checkCondition(values, sizeGreaterThan(0), "The argument array 'values' must have at least 1 element.");
		
		if (!Object[].class.equals(values) || !Object.class.equals(values[0].getClass()))
			return false;
		
		// >>>>> 此處: 元素型別不可能為Object.class >>>>> 
		
		// 檢查: 若為同質陣列 ➜ true (可能性更大)
		if (!containsMixedTypes(values))
			return true;
		
		// 檢查: 是否存在"非Object"的共同父類別
		return Arrays.stream(values)
				.<Class<?>>map((elem) -> elem.getClass())
				.distinct()
				.parallel()
				.reduce(TypeUtils::findCommonSuperTypeBetweenTwo)
				.get()
				.equals(Object.class) != true;
	}
	
	/**
	 * 推斷: 集合物件的元素型別, 支援array, collection
	 */
	public static <T> Class<T> inferElementType(Object values) {
		return inferElementType(values, null);
	}
	
	/**
	 * 推斷: 集合物件的元素型別, 支援array, collection
	 * 
	 * @param values
	 * @param defaultElemType 若values為空陣列, 可指定默認元素型別
	 */
	public static <T> Class<T> inferElementType(Object values, Class<T> defaultElemType) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		final Class<?> valuesType = values.getClass();
		// 檢查: 型別
		checkCondition(valuesType, ((Predicate<Class<?>>) Map.class::isAssignableFrom).negate() , "Map type is not supported, please call inferMapElementType(), inferMapKeyType() or inferMapValueType.");
		// 必須為array或collection
		checkCondition(valuesType, or(Class::isArray, (Predicate<Class<?>>) Collection.class::isAssignableFrom), "The type of argument 'values' is not supported, must be an array or a collection.");

		// 檢查: 集合長度
		final int length = getLength(values);
		if (length == 0) {
			if (!Object[].class.equals(valuesType))
				return (Class<T>) valuesType.getComponentType();
			else if (defaultElemType != null)
				return defaultElemType;
			
			return null;
		}
		
		// FIX ↓ 宣告時指定<?>
		Stream<?> stream = (valuesType.isArray())? Arrays.stream((Object[]) values) : ((Collection<?>) values).stream();

		// FIX 在.map指定泛型<?>          ↓
		return (Class<T>) stream.<Class<?>>map((elem) -> elem.getClass())
								.distinct()
								.parallel()
								.reduce(TypeUtils::findCommonSuperTypeBetweenTwo)
								.orElse(Object.class);
	}
	
	/**
	 * 推斷: 參數map的 key型別 (取所有key資料的共同parent type) 和
	 * 				  value型別 (取所有value資料的共同parent type)
	 * ①.分治處理
	 * ②.合併
	 * ③.一次傳回entry(keyType, valueType)
	 */
	public static <K, V> Entry<Class<? extends K>, Class<? extends V>> inferMapElementType(Map<?, ?> map) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null.");
		Class<? extends K> keyType = inferMapKeyType(map);
		Class<? extends V> valueType = inferMapValueType(map);
		return Map.entry(keyType, valueType);
	}
	
	public static <K> Class<K> inferMapKeyType(Map map) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null.");
		Stream<Class<?>> stream = map.keySet().stream().map((key) -> key.getClass());
		return (Class<K>) stream
			  .distinct()
			  .parallel()
			  .reduce(TypeUtils::findCommonSuperTypeBetweenTwo)
			  .orElse(Object.class);
	}
	
	public static <V> Class<? extends V> inferMapValueType(Map map) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null.");
		Stream<Class<?>> stream = map.values().stream().map((elem) -> elem.getClass());	
		return (Class<V>) stream
			  .distinct()
			  .parallel()
			  .reduce(TypeUtils::findCommonSuperTypeBetweenTwo)
			  .orElse(Object.class);
	}
	
	public static Class<?> findCommonSuperType(Class<?>... classes) {
		if (classes == null || classes.length == 0)
			return Object.class;

		Class<?> commonType = classes[0];
		for (int i = 1; i < classes.length; i++) {
			commonType = findCommonSuperTypeBetweenTwo(commonType, classes[i]);
		}
		return commonType;
	}

	public static Class<?> findCommonSuperTypeBetweenTwo(Class<?> c1, Class<?> c2) {
		if (c1.isAssignableFrom(c2))
			return c1;
		if (c2.isAssignableFrom(c1))
			return c2;

		final boolean isArray1 = c1.isArray();
		final boolean isArray2 = c2.isArray();
		final int dimension = getDimension(c1);
		if (dimension != getDimension(c2))
			return Object.class;
		
		// Traverse upwards from c1 until we find a superclass/interface that is assignable from c2
		Class<?> current = c1;
		while (current != null) {
			if (current.isArray()) {
				current = current.getComponentType();
				if (c2.isArray()) 
					c2 = c2.getComponentType();
			}
			else {
				if (current.isAssignableFrom(c2))
					return (isArray1 && isArray2)? Array.newInstance(current, new int[dimension]).getClass() : current;
				
				// Traverse upwards ↑ 
				current = current.getSuperclass();
			}
		}
		// If no superclass found, return Object as the ultimate common type
		return Object.class;
	}
	
	
	public static boolean isInnerType(Class<?> type) {
		Objects.requireNonNull(type, "The argument 'type' cannot be null");
		return type.getEnclosingClass() != null;
	}
	
	public static boolean isStaticInnerType(Class<?> type) {
		Objects.requireNonNull(type, "The argument 'type' cannot be null");
		return Modifier.isStatic(type.getModifiers());
	}
	
	/**
	 * 判斷2型別, 是否互為primitive與boxed type
	 */
	public static boolean arePrimitiveAndBoxedPair(Class<?> type1, Class<?> type2) {
	    if (isBoxedType(type1) && type2.isPrimitive())
	    	return toPrimitive(type1) == type2;
	    if (type1.isPrimitive() && isBoxedType(type2))
	    	return toBoxed(type1) == type2;
	    return false;
	}
	
	/**
	 * 判斷是否為primitive型別, 或boxed型別
	 * 
	 * @param value 可以是一般資料物件, 或Class<?>
	 */
	public static boolean isPrimitiveOrBoxedType(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");		
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		// 判斷型別
		return type.isPrimitive()||isBoxedType(type);
	}

	/**
	 * 判斷是否為boxed型別
	 * 
	 * @param value 可以是一般資料物件, 或Class<?>
	 */
	public static boolean isBoxedType(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		// 判斷型別
		return Byte.class == type || Short.class == type  || Integer.class == type || Long.class == type ||
			   Float.class == type || Double.class == type || Boolean.class == type || Character.class == type;
	}
	
	/**
	 * 判斷為真的範圍:
	 *  ➜ 僅有primitive的6個數值型別
	 *  
	 * @param value 可以是一般資料物件, 或Class<?>
	 */
	public static boolean isPrimitiveNumericType(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		// 判斷型別
		return byte.class == type || short.class == type || int.class == type ||
			   long.class == type || float.class == type || double.class == type;
	}
	
	/**
	 * 判斷為真的範圍:
	 *  ➜ 參考型別中所有繼承Number的 (包括boxed數值型別)
	 *  
	 *  @param value 可以是一般資料物件, 或是Class<?>
	 */
	public static boolean isNumericType(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		
		// 判斷型別:
		// boxed numeric type
		if (Number.class.isAssignableFrom(type))
			return true;
		// primitive type
		return byte.class == type || short.class == type || int.class == type ||
				long.class == type || float.class == type || double.class == type;
	}
	
	public static boolean isFloatingType(Object value) {
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		if (ArrayUtils.existsIn(type, double.class, float.class, Double.class, float.class, BigDecimal.class)) {
			return true;
		}
		return false;
	}
	
	public static boolean isIntegralType(Object value) {
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		if (ArrayUtils.existsIn(type, byte.class, short.class, int.class, long.class,
									  Byte.class, Short.class, Integer.class, Long.class, BigInteger.class)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判斷為真的範圍:
	 *  ➜ 僅有primitive數值型別 對應的boxed型別
	 *  
	 * @param value 可以是一般資料物件, 或是Class<?>
	 */
	public static boolean isBoxedNumericType(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		// 判斷型別
		return Byte.class == type || Short.class == type || Integer.class == type ||
				Long.class == type || Float.class == type || Double.class == type;
	}
	
	public static Byte toBoxed(byte b) { return Byte.valueOf(b); }
	public static Short toBoxed(short s) { return Short.valueOf(s); }
	public static Integer toBoxed(int i) { return Integer.valueOf(i); }
	public static Long toBoxed(long l) { return Long.valueOf(l); }
	public static Float toBoxed(float f) { return Float.valueOf(f); }
	public static Double toBoxed(double d) { return Double.valueOf(d); }
	public static Boolean toBoxed(boolean b) { return Boolean.valueOf(b); }
	public static Character toBoxed(char c) { return Character.valueOf(c); }
	
	/**
	 * 將primitive type array 轉換為boxed type array
	 */
	public static <T> T[] toBoxedArray(Object array) {
		return toBoxedArray(array, null, (BiFunction<Object, Integer, T>) null);
	}
	
	public static <T> T[] toBoxedArray(Object array, Class<T> targetElementType, Function<?, T> mapper) {
		return toBoxedArray(array, targetElementType, (elem, index) -> ((Function<Object, T>) mapper).apply(elem));
	}
	
	public static <T> T[] toBoxedArray(Object array, Class<T> targetElementType, BiFunction<?, Integer, T> mapper) {
		Objects.requireNonNull(array, "The argument 'array' cannot be null");

		// 檢核: array
		checkCondition(array.getClass(), Class::isArray, "The argument 'array' must be an array.");
		final Class<?> arrayType = array.getClass();
		final Class<?> elemType = (Class<?>) arrayType.getComponentType();

		// 檢核: targetElementType
		if (targetElementType != null)
			checkCondition(targetElementType, TypeUtils::isPrimitiveOrBoxedType, "The argument 'targetElementType' must be a primitive or boxed type.");
		
		if (isBoxedType(elemType))
			return (T[]) array;

		// 呼叫端可能透過Object...傳遞, 被java包裝成Object[]
		if (Object[].class.equals(arrayType)) {
			final AtomicInteger index = new AtomicInteger(0);
			Stream<T> stream = stream((Object[]) array).map((o) -> {
									final Class<T> elementType = (Class<T>) o.getClass();
									if (isBoxedType(elementType))
										return (T) o;
									if (Object.class.equals(elementType))
										return elementType.cast(o);
									else {
										if (mapper != null)
											return ((BiFunction<Object, Integer, T>) mapper).apply(o, index.getAndIncrement());
										else
											return null;
									}
								 })
								 .filter(Objects::nonNull);
			
			return stream.toArray(ArrayUtils.Hoc.newInstance(targetElementType));
		}
		else if (elemType.isPrimitive()) {
			Class<T> boxedElemType = toBoxed(elemType);
			T[] boxedArray = (T[]) Array.newInstance(boxedElemType, getLength(array));
						
			Method methodValueOf = null;
			try {
				methodValueOf = boxedElemType.getDeclaredMethod("valueOf", elemType);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Errors occurred durig looking up for 'valueOf' method. See the attached cause.", e);
			}
			
			for (int i=0; i<getLength(array); i++) {
				Object boxedValue = null;
				try {
					boxedValue = methodValueOf.invoke(null, Array.get(array, i));
					boxedArray[i] = (T) boxedValue;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
			return boxedArray;
		}
		// 非基礎8大型別
		throw new IllegalArgumentException("The argument must be an array of primitive element type, but the actual element type is " + doubleQuoteString(elemType.getName()));
	}
	
	/**
	 * 將boxed type array 轉換為 primitive type array
	 */
	public static <T, R> R toPrimitiveArray(T... array) {
		Objects.requireNonNull(array, "The argument 'array' cannot be null");
		
		final int len = array.length;
		final Class<T> elemType = (Class<T>) array.getClass().getComponentType();
		if (elemType.isPrimitive())
			return (R) array;
		
		// obtain corresponding primitive type of its element
		final Class<T> primitiveElemType = toPrimitive(elemType);
		
		Object primitiveArray = Array.newInstance(primitiveElemType, len);
		for (int i=0; i<len; i++)
			Array.set(primitiveArray, i, array[i]);
		return (R) primitiveArray;
	}
	
	/**
	 * 傳回boxed型別的相對應的primitive型別
	 * 如果參數type就是primitive型別, 直接傳回type
	 * 
	 * @param type 以此型別為參數, 若為boxed型別, 傳回其primitive type
	 * @throws IllegalArgumentException 若參數非boxed型別、非primitive型別, 拋出錯誤
	 */
	public static <R> R toPrimitive(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
				
		if (type.isPrimitive())
			return (R) type;

		Object primitive = null;
		if (Byte.class == type)
			primitive = (valueIsNotClass)? ((Byte) value).byteValue() : byte.class;
		else if (Short.class == type)
			primitive = (valueIsNotClass)? ((Short) value).shortValue() : short.class;
		else if (Integer.class == type)
			primitive = (valueIsNotClass)? ((Integer) value).intValue() : int.class;
		else if (Long.class == type)
			primitive = (valueIsNotClass)? ((Long) value).longValue() : long.class;
		else if (Float.class == type)
			primitive = (valueIsNotClass)? ((Float) value).floatValue() : float.class;
		else if (Double.class == type)
			primitive = (valueIsNotClass)? ((Double) value).doubleValue() : double.class;
		else if (Character.class == type)
			primitive = (valueIsNotClass)? ((Character) value).charValue() : char.class;
		else if (Boolean.class == type)
			primitive = (valueIsNotClass)? ((Boolean) value).booleanValue() : boolean.class;
		else
			// 非boxed型別, 非primitive ➜ 視為錯誤
			throw new IllegalArgumentException("Illegal argument 'value': The type must be a value of boxed type, or a boxed type itself.");
		
		return (R) primitive;
	}
	
	/**
	 * 傳回primitive型別的"相對應"的boxed型別
	 * 如果參數type就是boxed型別, 直接傳回type
	 * 
	 * @param type 以此型別為參數, 若為primitive型別, 傳回其boxed type
	 * @throws IllegalArgumentException 若參數非primitive型別、非boxed型別, 拋出錯誤
	 */
	public static <T> Class<T> toBoxed(Class<?> type) {
		Objects.requireNonNull(type, "The argument 'type' cannot be null");
		if (isBoxedType(type))
			return (Class<T>) type;

		Class<?> boxedType = null;
		if (byte.class == type)
			boxedType = Byte.class;
		else if (short.class == type)
			boxedType = Short.class;
		else if (int.class == type)
			boxedType = Integer.class;
		else if (long.class == type)
			boxedType = Long.class;
		else if (float.class == type)
			boxedType = Float.class;
		else if (double.class == type)
			boxedType = Double.class;
		else if (char.class == type)
			boxedType = Character.class;
		else if (boolean.class == type)
			boxedType = Boolean.class;
		else
			// 非primitive, 非boxed型別 ➜ 視為錯誤
			throw new IllegalArgumentException("Illegal type argument: The type must be a primitive type.");
		
		return (Class<T>) boxedType;
	}
	
	/**
	 * 傳回type型別的java預設值 (如果參數type為primitive, 預設值可參考java規格文件)
	 */
	public static <T> T getDefaultValue(Class<?> type) {
		Objects.requireNonNull(type, "The argument 'type' cannot be null");
		return getDefaultValue(type, false);
	}
	
	/**
	 * 傳回type型別的java預設值 (如果參數type為primitive, 預設值可參考java規格文件)
	 * 
	 * @param providingNonNullDefaultValueForBoxedType 如果為true, 在type為boxed型別時, 傳回該型別的相應的primitive type的預設值
	 * 例: type傳入Integer時, 得到的預設值為0 (本來應為null)
	 */
	public static <T> T getDefaultValue(Class<?> type, boolean providingNonNullDefaultValueForBoxedType) {
		Objects.requireNonNull(type, "The argument 'type' cannot be null");
		if (providingNonNullDefaultValueForBoxedType)
			if (isBoxedType(type))
				type = toPrimitive(type);
	    return (T) Array.get(Array.newInstance(type, 1), 0);
	}
	
	public static boolean isLamda(Object obj) {
		if (obj == null)
			return false;
		return isFunctionalInterface(obj.getClass());
	}
	
	public static boolean isLamdaType(Class<?> type) {
		if (type == null)
			return false;
		return isFunctionalInterface(type);
	}
	
	public static boolean isFunctionalInterface(Object value) {
		if (value == null)
			return false;
		
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		
		/*
		 * 這樣的用法是因為, 傳進去的lamda是一種anonymous類別的實例？
		 *       所以直接呼叫isAnnotationPresent 就不起作用？
		 * 
		 * 1.先取得 getAnnotatedInterfaces
		 * 2.再對各自元素判斷是否有 @FunctionalInterface
		 *    ➜ 是為了避免 臨時的Lamda表達式
		 *      (型別名以$$lamda/xxxx結尾)
		 *      
		 * 待解決: getAnnotatedInterfaces() 的結果, 確定只會有一個嗎？
		 */
		AnnotatedType[] annotatedInterfaces = type.getAnnotatedInterfaces();
		if (annotatedInterfaces.length > 1)
			throw new RuntimeException("annotatedInterfaces count > 1 的狀況發生了!!");
		
		return Arrays.stream(annotatedInterfaces)
		 .map(elem -> (Class<?>) elem.getType())
		 .filter((Class<?> each) -> each.isAnnotationPresent(FunctionalInterface.class))
		 .findAny()
		 .isPresent();
	}	
}

package cathay.coindeskApi.commons.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import cathay.coindeskApi.commons.util.types.TypeUtils;

/**
 * 具有集合特性的物件的操作
 */
public class MultiElementUtils {
	/**
	 * 將具有集合特性的物件, 取得其stream傳回
	 */
	public static <E> Stream<E> stream(E[] values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		return (Stream<E>) Arrays.stream(values);
	}
	
	/**
	 * 將具有集合特性的物件, 取得其stream傳回
	 */
	public static <E> Stream<E> stream(Object values) {
		Objects.requireNonNull(values, "The argument 'value' cannot be null.");
		final Class<?> valueType = values.getClass();
		final boolean isArray = valueType.isArray();
		
		if (!isArray && !Collection.class.isAssignableFrom(valueType))
			throw new IllegalArgumentException("The argument value must be an array or collection.");
		
		// Array
		if (isArray) {
			if (valueType.getComponentType().isPrimitive())
				values = TypeUtils.toBoxedArray(values);
			return Arrays.<E>stream(TypeUtils.toBoxedArray(values));
		}
		// Collection
		return ((Collection<E>) values).stream();
	}
	
	/**
	 * 如果參數value有集合特性, 執行then
	 */
	public static <T, R> T ifThen(T values, Consumer<?> then) {
		final Class<?> valueClass = values.getClass();
		if (valueClass.isArray()) {
			((Consumer<Object>) then).accept(values);
			return values;
		}
		else if (Collection.class.isAssignableFrom(valueClass)) {
			((Consumer<Object>) then).accept((T) values);
			return values;
		}
		else if (Map.class.isAssignableFrom(valueClass)) {
			((Consumer<Object>) then).accept((T) values);
			return values;
		}
		throw new IllegalArgumentException("ifThen(): Unsupported type " + StringUtils.doubleQuoteString(values.getClass().getName()));
	}
	
	/**
	 * 如果參數values有集合特性, 執行then
	 */
	public static <R> R ifThenReturn(Object values, Function<?, ? extends R> thenReturn) {
		final Class<?> valueClass = values.getClass();
		if (valueClass.isArray())
			return ((Function<Object, R>) thenReturn).apply(values);
		if (Collection.class.isAssignableFrom(valueClass))
			return ((Function<Object, R>) thenReturn).apply(values);
		if (Map.class.isAssignableFrom(valueClass))
			return ((Function<Object, R>) thenReturn).apply(values);
		throw new IllegalArgumentException("ifThenReturn(): Unsupported type " + StringUtils.doubleQuoteString(values.getClass().getName()));
	}
	
	/**
	 * 取得array, collection, 或是map的長度
	 */
	public static Integer getLength(Object values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		if (values.getClass().isArray())
			return Array.getLength(values);
		if (Collection.class.isAssignableFrom(values.getClass()))
			return ((Collection) values).size();
		if (Map.class.isAssignableFrom(values.getClass()))
			return ((Map) values).size();
		throw new IllegalArgumentException("Illegal type of argument 'values', only supports array, collection, map");
	}
	
	/**
	 * 取得array, List位於index的元素
	 */
	public static <E> E get(Object values, int index) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		final Class<?> valuesClass = values.getClass();
		
		if (!valuesClass.isArray() && !Collection.class.isAssignableFrom(valuesClass))
			throw new IllegalArgumentException("Illegal 'values' type \"" + valuesClass.getName() + "\", The type of argument 'values' must be array or Collection");
		
		if (index < 0 || index >= getLength(values))
			throw new IllegalArgumentException("Illegal type of argument 'values', only supports array, collection, map");
		
		// check type
		if (valuesClass.isArray())
			return (E) Array.get(values, index);		
		else if (List.class.isAssignableFrom(valuesClass))
			return (E) ((List<?>) values).get(index);
		//else if (Map.class.isAssignableFrom(valuesClass))
		//	throw new UnsupportedOperationException("不支援map的索引取值操作, 僅支援array, list的get(int index)");
		throw new UnsupportedOperationException("Illegal type of argument 'values', only indexible operation of the type of array, list are supported.");
	}
	
	public static <E> List<E> toList(Object values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		final Class<?> valuesClass = values.getClass();
		// 必須為array或collection
		if (!valuesClass.isArray() && !Collection.class.isAssignableFrom(valuesClass))
			throw new IllegalArgumentException("Illegal 'values' type \"" + valuesClass.getName() + "\", The type of argument 'values' must be array or Collection");
		
		List list = null;
		if (valuesClass.isArray()) {
			final Class elementType = valuesClass.getComponentType();

			list = new ArrayList();
			if (byte.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Byte.valueOf(Array.getByte(values, i)));
			} else if (short.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Short.valueOf(Array.getShort(values, i)));
			} else if (int.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Integer.valueOf(Array.getInt(values, i)));
			} else if (long.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Long.valueOf(Array.getLong(values, i)));
			} else if (float.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Float.valueOf(Array.getFloat(values, i)));
			} else if (double.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Double.valueOf(Array.getDouble(values, i)));
			} else if (char.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Character.valueOf(Array.getChar(values, i)));
			} else if (boolean.class.equals(elementType)) {
				for (int i=0; i<Array.getLength(values); i++) list.add(Boolean.valueOf(Array.getBoolean(values, i)));
			} else {
				for (int i=0; i<Array.getLength(values); i++) list.add(Array.get(values, i));
			}
		}
		else if (Collection.class.isAssignableFrom(valuesClass)) {
			if (List.class.isAssignableFrom(valuesClass))
				list = (List) values;
			else
				list = new ArrayList((Collection) values);
		}
		return (List<E>) list;
	}
}

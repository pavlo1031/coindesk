package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.TypeUtils.toBoxedArray;
import static cathay.coindeskApi.commons.util.TypeUtils.toBoxed;
import static cathay.coindeskApi.commons.enums.Direction.*;
import static java.util.Arrays.stream;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import cathay.coindeskApi.commons.enums.Direction;
import cathay.coindeskApi.commons.util.regex.RegExUtils;

public class ArrayUtils {

	public static class Iteration {
		
		public static <T> Iterable<T> forwardOrder(Object array) {
			Objects.requireNonNull(array, "The argument 'array' cannot be null");
			final Class<?> arrayType = array.getClass();
			if (!arrayType.isArray())
				throw new IllegalArgumentException("The argument 'array' must be an array, but actual type is " + doubleQuoteString(arrayType.getName()));
			
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return forwardOrderIterator(array);
				}
			};
		}
		
		public static <T> Iterable<T> reverseOrder(Object array) {
			Objects.requireNonNull(array, "The argument 'array' cannot be null");
			final Class<?> arrayType = array.getClass();
			if (!arrayType.isArray())
				throw new IllegalArgumentException("The argument 'array' must be an array, but actual type is " + doubleQuoteString(arrayType.getName()));
			
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return reverseOrderIterator(array);
				}
			};
		}
		
		public static <T> Iterator<T> forwardOrderIterator(Object array) {
			Objects.requireNonNull(array, "The argument 'array' cannot be null");
			return createArrayIterator(array, Forward);
		}
		 
		public static <T> Iterator<T> reverseOrderIterator(Object array) {
			Objects.requireNonNull(array, "The argument 'array' cannot be null");
			return createArrayIterator(array, Reverse);
		}
		
		private static <T> Iterator<T> createArrayIterator(Object array, Direction direction) {
			Objects.requireNonNull(array, "The argument 'array' cannot be null");
			final Class<?> arrayType = array.getClass();
			if (!arrayType.isArray())
				throw new IllegalArgumentException("The argument 'array' must be an array, but actual type is " + doubleQuoteString(arrayType.getName()));

			// type information
			final Class<?> componentType = array.getClass().getComponentType();
			final Class<T> elementType = (Class<T>) ((componentType.isPrimitive())? toBoxed(componentType):componentType); 
			
			final T[] array_ = (T[]) (componentType.isPrimitive()? toBoxedArray(array) : array);
			final int length = Array.getLength(array);
			
			return new Iterator<T>() {
				int position = (Forward == direction)? 0 : length;
				public boolean hasNext() {
					return (Forward == direction)
							? position < length
							: position > 0;
				}
				public T next() {
					return (Forward == direction)
							? array_[position++]
							: array_[--position];
				}
			};
		}
	}
	
	public static <T> T[] spread(Object array) {
		final Class<?> arrayType = array.getClass();
		if (!arrayType.isArray())
			throw new IllegalArgumentException("The argument must be an array, but actual type is " + arrayType);
		
		// obtain element type of the array
		Class<T> elemType = (Class<T>) array.getClass().getComponentType();
		
		List<T> resultList = null;
		try {
			resultList = spread0(new ArrayList(), array.getClass().getComponentType(), array);		
			return (T[]) resultList.toArray(Hoc.newInstance(elemType));
		}
		finally {
			resultList.clear();
			resultList = null;
			elemType = null;
		}
	}
	
	// internal implementation
	private static List<Object> spread0(List<Object> bufferList, Class<?> T, Object array) {
		final int len = Array.getLength(array);
		for (int i=0; i < len; i++) {
			final Object element = Array.get(array, i);
			
			// assert not-null
			if (element == null)
				continue;
			
			// Not an array
			if (!element.getClass().isArray()) {
				bufferList.add(element);
				continue;
			}
			
			// >>>> Here the element[i] is an array 			
			Class<?> subArrayElemType = element.getClass().getComponentType();
			final Object[] elementArray = (subArrayElemType.isPrimitive())
										   ? toBoxedArray(element)
										   : (Object[]) element;
			spread0(bufferList, T, elementArray);
		}
		return bufferList;
	}
	
	public static int getDimension(Object array) {
		Objects.requireNonNull(array, "The arugment 'array' cannot be null.");
		if (!array.getClass().isArray())
			return 0;
		
		return RegExUtils.matches("(?:class )?(\\[++).*+", array.toString(), (matcher) -> {
			return matcher.group(1).length();
		});
	}
	
	/**
	 * 陣列是否含有不同型別之元素
	 */
	public static boolean containsMixedTypes(Object[] values) {
		Objects.requireNonNull(values, "The arugment 'values' cannot be null.");
		return containsMixedTypes(values, (BiConsumer<Object, Integer>) null);
	}
	
	/**
	 * 陣列是否含有不同型別之元素; 若找到不同型別元素, 呼叫handler
	 */
	public static boolean containsMixedTypes(Object[] values, Consumer<Object> handler) {
		Objects.requireNonNull(values, "The arugment 'values' cannot be null.");
		return containsMixedTypes(values, (elem, index) -> handler.accept(elem));
	}
	
	/**
	 * 陣列是否含有不同型別之元素; 若找到不同型別元素, 呼叫handler(帶有index)
	 */
	public static boolean containsMixedTypes(Object[] values, BiConsumer<Object, Integer> handler) {
		Objects.requireNonNull(values, "The arugment 'values' cannot be null.");
		if (values.length == 0)
			return false;

		Class<?> lastElemType = values[0].getClass();
		for (int i = 1; i < values.length; i++) {
			Object element = values[i];
			if (!element.getClass().equals(lastElemType)) {
				if (handler != null)
					handler.accept(element, i);
				else
					return true;
			}
		}
		return false;
	}
	
   /**
	* 多維陣列的重構, 可轉換為總元素數量一樣多, 但維度結構不同的陣列
	*
	* @param array 待轉換的陣列
	* @param lengths 各維度的長度
	* @return 轉換結果陣列
	* @throws IllegalArgumentException if the product of lengths does not match array length
	*/
	public static Object reshapeArray(Object array, int... lengths) {
		if (!array.getClass().isArray())
			throw new IllegalArgumentException("Input must be an array.");

		int totalLength = java.lang.reflect.Array.getLength(array);
		int expectedLength = 1;
		for (int len : lengths)
			expectedLength *= len;
		
		if (totalLength != expectedLength)
			throw new IllegalArgumentException("Total elements (" + totalLength + ") "
					+ "does not match product of dimensions (" + expectedLength + ").");

		// Create new multi-dimensional array
		Object reshaped = Array.newInstance(array.getClass().getComponentType(), lengths);
		fillArray(array, reshaped, new int[lengths.length], lengths, 0);
		return reshaped;
	}	
	
	/**
	 * <pre>
	 * @param indices 是一個「指標陣列」記錄目前走到 (row, column, depth...) 這個位置
	 *         使我們能正確把元素放進去對應的座標。
	 *         ➜ 是"路徑紀錄器"的角色，用來使遞迴知道「目前要把元素放在哪一格」
	 * </pre>
	 */
	private static void fillArray(Object source, Object dest, int[] indices, int[] lengths, int dimension) {
		fillArray(source, dest, indices, lengths, dimension, new AtomicInteger(0));
	}

	/**
	 * <pre>
	 * @param indices 是一個「指標陣列」記錄目前走到 (row, column, depth...) 這個位置
	 *         使我們能正確把元素放進去對應的座標。
	 *         ➜ 是"路徑紀錄器"的角色，用來使遞迴知道「目前要把元素放在哪一格」
	 * @param dim 目前遞迴所在維度 (0 表最外層)
	 * </pre>
	 */
	private static void fillArray(Object source, Object dest, int[] indices, int[] lengths, int dimension, AtomicInteger counter) {
		if (dimension == lengths.length - 1) {
			for (int i = 0; i < lengths[dimension]; i++) {
				Object value = Array.get(source, counter.getAndIncrement());
				Array.set(dest, i, value);
			}
		}
		else {
			for (int i = 0; i < lengths[dimension]; i++) {
				Object subArray = Array.get(dest, i);
				indices[dimension] = i;
				fillArray(source, subArray, indices, lengths, dimension + 1, counter);
			}
		}
	}
	
	public static boolean isEmpty(Object... array) {
		return array == null || array.length == 0;
	}
	
	public static boolean isNotEmpty(Object... array) {
		return array != null && array.length > 0;
	}
	
	public static <T> boolean allMatch(T[] array, Predicate<T> condition) {
		return Arrays.stream(array).allMatch(condition);
	}
	
	public static <T> boolean nonMatch(T[] array, Predicate<T> condition) {
		return Arrays.stream(array).noneMatch(condition);
	}
	
	public static <T> boolean anyMatch(T[] array, Predicate<T> condition) {
		return Arrays.stream(array).anyMatch(condition);
	}
	
	public static <T> boolean anyNotMatching(T[] array, Predicate<T> condition) {
		return Arrays.stream(array).allMatch(condition) != true;
	}
	
	public static <T> boolean isAllMatching(T[] array, Predicate<T> check, Consumer<Object> eachFail) {
		return isAllMatching(array, check, (elem, index) -> eachFail.accept(elem));
	}
	
	public static <T> boolean isAllMatching(T[] array, Predicate<T> check, BiConsumer<Object, Number> eachFail) {
		Objects.requireNonNull(array, "The argument 'array' cannot be null.");
		if (check == null) check = (t) -> true;
		
		final int dimension = getDimension(array);
		if(getDimension(array) > 1)
			throw new UnsupportedOperationException("only 1-dimension array is supported. Actual dimension of the argument 'array' is " + dimension);
		
		if(array.getClass().getComponentType().isPrimitive())
			array = TypeUtils.toBoxedArray(array);
		
		final int length = Array.getLength(array);
		for (int i=0, failCount=0; i<length; i++) {
			final Object elem = Array.get(array, i);
			
			if (((Predicate<Object>) check).test(elem) != true) {
				if (eachFail == null)
					return false;

				// try-catch 避免caller撰寫的callback拋錯
				try {
					eachFail.accept(elem, Integer.valueOf(i));
				} catch (Throwable t) {
					// 攔截錯誤, 略過
				}
				
				// 檢核不成功: "不"直接return false
				// ➜ 因為在每次失敗時, 要調用eachFail
				failCount++;
			}
			
			if (i == length-1)
				return failCount == 0;
		}
		return true;
	}
	
	/**
	 * 取得最後一個元素
	 */
	public static <T> T getLast(T[] array) {
		Objects.requireNonNull(array, "The argument 'array' cannot be null.");
		return getLast(array, 0);
	}
	
	/**
	 * 取得倒數第index的元素
	 */
	public static <T> T getLast(T[] array, int index) {
		Objects.requireNonNull(array, "The argument 'array' cannot be null.");
		int last = array.length-1;
		return array[last-index];
	}
	
	public static int indexOf(Object valueToFind, Object... array) {
		if (array == null || array.length == 0)
			return -1;
		
		for (int i=0; i<array.length; i++) {
			final Object element = array[i];
			if (element == null)
				continue;
			if (element.equals(valueToFind))
				return i;
		}
		return -1;
	}
	
	public static boolean existsIn(Object valueToFind, Object... array) {
		return indexOf(valueToFind, array) != -1;
	}
	
	/**
	 * @param U 集合之元素型別
	 * @param R 回傳型別
	 * @param values 待搜尋元素的目標集合, 可以是array, 或Collection
	 */
	public static <U, R> R firstNonNull(Object values) {
		return firstNonNull(values, (BiFunction<U, Integer, R>) null);
	}
	
	/**
	 * @param U 集合之元素型別
	 * @param R 回傳型別
	 * @param values 待搜尋元素的目標集合, 可以是array, 或Collection
	 * @param foundThenPeek 如果找到目標, 可在callback查看內容
	 */
	public static <U, R> R firstNonNull(Object values, Consumer<?> foundThenPeek) {
		return firstNonNull(values, (BiFunction<U, Integer, ? extends R>)((elem, index) -> {((Consumer<Object>) foundThenPeek).accept(elem); return null;}));
	}
	
	/**
	 * @param U 集合之元素型別
	 * @param R 回傳型別
	 * @param values 待搜尋元素的目標集合, 可以是array, 或Collection
	 * @param foundThenPeek 如果找到目標, 可在callback查看內容, 並且可知道目標元素位置
	 */
	public static <U, R> R firstNonNull(Object values, BiConsumer<U, Integer> foundThenPeek) {
		return firstNonNull(values, (BiFunction<Object, Integer, ? extends R>)((elem, index) -> {((BiConsumer<Object, Integer>) foundThenPeek).accept(elem, index); return null;}));
	}
	
	/**
	 * @param U 集合之元素型別
	 * @param R 回傳型別
	 * @param values 待搜尋元素的目標集合, 可以是array, 或Collection
	 * @param foundThenReturn 如果找到目標, 自訂回傳結果
	 */
	public static <U, R> R firstNonNull(Object values, Function<U, ? extends R> foundThenReturn) {
		return firstNonNull(values, (BiFunction<Object, Integer, ? extends R>)((elem, index) -> ((Function<Object, R>) foundThenReturn).apply(elem)));
	}
	
	
	/**
	 * @param U 集合之元素型別
	 * @param R 回傳型別
	 * @param values 待搜尋元素的目標集合, 可以是array, 或Collection
	 * @param foundThenReturn 如果找到目標, 自訂回傳結果
	 */
	public static <U, R> R firstNonNull(Object values, BiFunction<U, Integer, R> foundThenReturn) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		// 檢查型別
		final Class<?> valuesType = values.getClass();
		final boolean isArray = valuesType.isArray();
		if (!isArray && !Collection.class.isAssignableFrom(valuesType))
			throw new IllegalArgumentException("參數values: 僅支援array、List, 但實際型別為: " +
				values.getClass().getName());
		
		if (isArray && valuesType.getComponentType().isPrimitive())
			values = toBoxedArray(values);
		
		// intermediate container
		final List<U> list = new ArrayList<U>((isArray)? Arrays.asList((U[]) values) : (Collection<U>) values);

		/* 索引範圍的stream */
		return IntStream.range(0, MultiElementUtils.getLength(values))
	    .mapToObj(MapUtils.Hoc.createEntry((index) -> list.get(index)))
		.filter((e) -> e.getValue() != null)
	    .findFirst()
	    .map((e) -> {
	    	R ret = null;
			if ((ret = foundThenReturn.apply(e.getValue(), e.getKey())) != null) {
				return ret;
			}
			return (R) e.getValue();
	    })
	    .orElse(null);
	}
	
	/**
	 * "第1個" 符合條件的元素
	 */
	public static <T,R> Optional<R> findFirstMatch(Predicate<? extends T> condition, T... values) {
		return findFirstMatch(condition, null, values);
	}
	
	/**
	 * 找到"第1個"符合條件的元素, 可以自訂回傳結果
	 */
	public static <T,R> Optional<R> findFirstMatch(Predicate<? extends T> condition, Function<? extends T, R> foundThenReturn, T... values) {
		Objects.requireNonNull(condition, "The argument 'condition' cannot be null.");
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		Optional<R> result = stream((Object[]) values).filter((Predicate) condition).findFirst();
		if (foundThenReturn != null)
			return Optional.<R>ofNullable(((Function<Object, R>) foundThenReturn).apply(result.get()));
		return result;
	}
	
	/**
	 * "所有"符合條件的元素
	 */
	public static <T,R> R findAnyMatch(Predicate<? extends T> condition, T... values) {
		return findAnyMatch(condition, null, values);
	}
	
	/**
	 * 找到"所有"符合條件的元素, 可以自訂回傳結果
	 */
	public static <T,R> R findAnyMatch(Predicate<? extends T> condition, Function<T[], R> foundThenReturn, T... values) {
		Class elemType = values.getClass().getComponentType();
		T[] result = stream(values).filter((Predicate<T>) condition).toArray(ArrayUtils.Hoc.<T>newInstance(elemType));
		if (foundThenReturn != null)
			return foundThenReturn.apply(result);
		return (R) result;
	}

	////////////////////////////////////////////////////////////

	/**
	 * 第一個"不"符合
	 */
	public static <T,R> Optional<R> findFirstNotMatching(Predicate<? extends T> condition, T...values) {
		return findFirstNotMatching(condition, null, values);
	}
	
	/**
	 * 找到"第一個"不符合, 可以自訂回傳結果
	 */
	public static <T,R> Optional<R> findFirstNotMatching(Predicate<? extends T> condition, Function<? extends T, R> foundThenReturn, T...values) {
		Objects.requireNonNull(condition, "The argument 'condition' cannot be null.");
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		Optional<R> result = stream((Object[]) values)
							 .filter(((Predicate) condition).negate())
							 .findFirst();
		if (foundThenReturn != null)
			return Optional.<R>ofNullable(((Function<Object, R>) foundThenReturn).apply(result.get()));
		return result;
	}

	/**
	 * 所有"不"符合
	 */
	public static <T,R> R findAnyNotMatching(Predicate<T> condition, T...values) {
		return findAnyNotMatching(condition, null, values);
	}
	
	/**
	 * 找到"所有"不符合, 可以自訂回傳結果
	 */
	public static <T,R> R findAnyNotMatching(Predicate<T> condition, Function<T[], R> foundThenReturn, T...values) {
		Objects.requireNonNull(condition, "The argument 'condition' cannot be null.");
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		final Class elemType = values.getClass().getComponentType();
		T[] result = stream(values).filter((Predicate<T>) condition)
					 .toArray(ArrayUtils.Hoc.<T>newInstance(elemType));
		if (foundThenReturn != null)
			return foundThenReturn.apply(result);
		return (R) result;
	}
	
	////////////////// HOC //////////////////

	public static class Hoc {
		public static <E> IntFunction<E[]> newInstance(Class<E> componentType) {
			return (int size) -> {
				return (E[]) Array.newInstance(componentType, size);
			};
		}	
	}
	
	////////////////// Validation //////////////////
	
	public static <T> T[] requireAllMatch(T[] array, Predicate<T> check) {
		return requireAllMatch(array, check, (String) "The argument 'array' does not pass the check.");
	}

	public static <T> T[] requireAllMatch(T[] array, Predicate<T> check, String msg) {
		if (!isAllMatching(array, check, (BiConsumer) null))
			throw new RuntimeException(msg);
		return array;
	}
	
	public static <T> T[] requireAllMatch(T[] array, Predicate<T> check, String msg, Function<String, ? extends RuntimeException> exceptionClass) {
		if (!isAllMatching(array, check, (BiConsumer) null))
			throw exceptionClass.apply(msg);
		return array;
	}
}

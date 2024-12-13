package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.TypeUtils.inferElementType;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectUtils {
	
	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	/**
	 * 高階函數都定義在此處
	 */
	public static class Hoc {
		
	}
	
	/**
	 * 以建議的型別elementType 重新裝載元素 (以特定型別的array)
	 */
	public static <T> T[] reboxArray(Object[] values) {
		return reboxArray(values, null);
	}
	
	/**
	 * 以建議的型別elementType 重新裝載元素 (以特定型別的array)
	 * 
	 * @param values 重新裝載之陣列
	 * @param elementType 欲使用此型別的陣列重新裝載values; 如果未指定, 會自行推斷
	 * @throws IllegalArgumentException 有傳入建議型別elementType時, 若此不適用於所有元素, 拋出例外
	 */
	public static <T> T[] reboxArray(Object[] values, Class<T> elementType) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null");
		final Class<?> valuesType = values.getClass();
		
		// 僅支援陣列
		if (!valuesType.isArray())
			throw new IllegalArgumentException("Illegal argument 'values': it must be an array, but actual type is " + doubleQuoteString(values.getClass().getName()));
		
		// 檢查: 參數elementType是否為null？
		final AtomicReference<Class<T>> elementTypeRef = new AtomicReference<Class<T>>(elementType);		
		if (elementType == null)
			elementTypeRef.set(inferElementType(values));

		// 檢查: 參數values為空陣列
		if (Array.getLength(values) == 0)
			return (T[]) Array.newInstance(elementTypeRef.get(), 0);	
		
		// 檢查異質元素
		String errorMsg = stream(values)
						  .filter((elem) -> !elementTypeRef.get().isAssignableFrom(elem.getClass()))
						  .collect(mapping((elem) -> doubleQuoteString(elem.getClass().getName()), joining(", ")));
		if (!errorMsg.isEmpty())
			throw new IllegalArgumentException("Illegal elementType: 參數values包含不適用elementType之元素: " + errorMsg);

		
		// 被java用Object[]包裝
		if (Object.class.equals(valuesType.getComponentType()) && !Object.class.equals(values[0].getClass())) {
			T[] outputArray = (T[]) Array.newInstance(elementTypeRef.get(), values.length);
			for (int i=0; i<values.length; i++)
				outputArray[i] = elementTypeRef.get().cast(values[i]);
			return outputArray;
		}
		return (T[]) values;
	}
	
	public static int[] findNull(final Object... values) {
		if (isEmpty(values))
			return new int[0];
		
		final ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
		for (int i=0; i<values.length; i++) {
			final Object element = values[i];
			if (element == null)
				nullIndexes.add(i);
		}
		return nullIndexes.stream().mapToInt(Integer::intValue).toArray();
    }
	
	public static int[] findNonNull(final Object... values) {
        return findNonNull(values, (BiConsumer<Object, Integer>) null);
    }
	
	public static int[] findNonNull(final Object[] values, Consumer<Object> eachIsNotNull) {
		return findNonNull((Object[])values, (BiConsumer<Object, Integer>) null);
    }
	
	public static int[] findNonNull(final Object[] values, BiConsumer<Object, Integer> eachIsNotNull) {
		if (isEmpty(values))
			return new int[0];
		
		final ArrayList<Integer> nonNullIndexes = new ArrayList<Integer>();
		for (int i=0; i<values.length; i++) {
			final Object element = values[i];
			if (element != null) {
				nonNullIndexes.add(i);
				if (eachIsNotNull != null)
					eachIsNotNull.accept(element, i);
			}
		}
		return nonNullIndexes.stream().mapToInt(Integer::intValue).toArray();
    }
	
	public static Object foreachField(Object target, Consumer<Field> eachProperty) {
		if (target == null)
			throw new NullPointerException("The argument 'target' cannot be null");
		return foreachField(target, (field, value) -> eachProperty.accept(field));
	}
	
	public static Object foreachField(Object target, BiConsumer<Field, Object> eachProperty) {
		if (target == null)
			throw new NullPointerException("The argument 'target' cannot be null");
		
		Field[] fields = target.getClass().getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
			final Field field = fields[i];
			boolean accessible = field.isAccessible();
			Object value = null;
			try {
				field.setAccessible(true);
				value = field.get(target);
				if (!accessible) field.setAccessible(false);
				
				if (eachProperty != null)
					eachProperty.accept(field, value);
			}
			catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				continue;
			}
			catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				continue;
			}
		}
		return target;
	}

	public static boolean allSamePresenceStatus(Object...values) {
		requireNonNull(values, "The argument 'values' cannot be null");
		if (values.length <= 1)
			return true;
		
		for (int i=1; i<values.length; i++) {
			final Object curr = values[i];
			final Object prev = values[i-1];
			if (curr == null && prev == null)
				continue;
			if (curr != null && prev != null)
				continue;
			return false;
		}
		return true;
	}
	
	/**
	 * 如果各元素是boolean, 以其true/false值來判斷;
	 * 如果該元素非boolean型別, 只要不是null就視為true
	 */
	private static Predicate<Object> evalTrue = (elem) -> {
		if (elem == null)
			return false;
		
		if (boolean.class == elem.getClass() || Boolean.class == elem.getClass())
			return (boolean) elem;
		
		// TODO: 判斷字串、字元?
		//       空字串、不含可見字元的字串
		//       ➜ 該判讀為true 或 false ?
		return true;
	};
	
	public static <T> T[] requireNonEmpty(T... array) {
		requireNonNull(array, "The argument array cannot be null.");
		return requireNonEmpty(array, null);
	}
	
	public static <T> T[] requireNonEmpty(T[] array, CharSequence message) {
		final StringBuilder buffer = threadLocalBuffer.get();
        requireNonNull(array, "The argument 'array' cannot be null.");
        
		final Class<T> elementType = (Class<T>) array.getClass().getComponentType();
		try {
	        if (array.length == 0) {
	        	if (isNotBlank(message)) {
	        		throw new IllegalArgumentException(message.toString());
	        	}
	        	else {
	        		buffer.append("The argument array of ").append(elementType.getSimpleName()).append(" ")
		        		  .append("cannot be empty, it must have at least 1 element.");
	        		throw new IllegalArgumentException(buffer.toString());
	        	}
	        }
	        return array;
		}
		finally {
			buffer.setLength(0);
		}
    }
	
	public static boolean isEmpty(final Object... values) {
		return values == null || values.length == 0;
	}
	
	/**
	 * 所有元素都判斷為false
	 */
	public static boolean noneIsTrue(Object... args) {
		return Arrays.stream(args).filter(evalTrue).count() == 0;
	}
	
	/**
	 * 僅有一元素都判斷為true
	 */
	public static boolean onlyOneIsTrue(Object... args) {
		return Arrays.stream(args).filter(evalTrue).count() == 1;
	}
	
	public static boolean onlyOneIsNull(Object... args) {
		return Arrays.stream(args).filter(Objects::isNull).count() == 1;
	}
	
	public static boolean onlyOneNotNull(Object... args) {
		return Arrays.stream(args).filter(((Predicate<Object>) Objects::isNull).negate()).count() == 1;
	}
	
	public static String[] getPropertNameArray(Object o) {
		List<String> names = getPropertNames(o, (field, value) -> true);
		return names.toArray(new String[names.size()]);
	}
	
	public static String[] getPropertNameArray(Object o, Predicate<Object> predicate) {
		List<String> names = getPropertNames(o, predicate);
		return names.toArray(new String[names.size()]);
	}
	
	public static List<String> getPropertNames(Object o) {
		return getPropertNames(o, (field, value) -> true);
	}
	
	public static List<String> getPropertNames(Object o, Predicate<Object> predicate) {
		ArrayList<String> list = new ArrayList<String>();
		foreachField(o, (field, value) -> {
			if (predicate.test(value))
				list.add(field.getName());
		});
		return list;
	}
	
	public static String[] getPropertNameArray(Object o, BiPredicate<Field, Object> predicate) {
		List<String> names = getPropertNames(o, predicate);
		return names.toArray(new String[names.size()]);
	}
	
	public static List<String> getPropertNames(Object o, BiPredicate<Field, Object> predicate) {
		ArrayList<String> list = new ArrayList<String>();
		foreachField(o, (field, value) -> {
			if (predicate.test(field, value))
				list.add(field.getName());
		});
		return list;
	}
	
	public static <V> V getFieldValue(Object obj, Field field) throws IllegalArgumentException {
		boolean isAccessible = field.isAccessible();
		V value = null;
		try {
			if (!isAccessible) field.setAccessible(true);
			value = (V) field.get(obj);
			if (!isAccessible)
				field.setAccessible(false);
		}
		catch (IllegalAccessException e) {
			// 已處理
		}
		catch (IllegalArgumentException e) {
			// 設定之值型別與field不同
			throw e;
		}
		return value;
	}
	
	public static <V> void setFieldValue(Object obj, Field field, V value) throws IllegalArgumentException {
		boolean isAccessible = field.isAccessible();
		try {
			field.setAccessible(true);
			field.set(obj, value);
			if (!isAccessible) field.setAccessible(true);
		}
		catch (IllegalAccessException e) {
			// 已處理
		}
		catch (IllegalArgumentException e) {
			// 設定之值型別與field不同
			throw e;
		}
		finally {
			if (!isAccessible)
				field.setAccessible(false);
		}
	}
}

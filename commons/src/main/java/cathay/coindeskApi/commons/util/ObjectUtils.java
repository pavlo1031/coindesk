package cathay.coindeskApi.commons.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectUtils {

	public static Object foreachProperties(Object target, Consumer<Field> eachProperty) {
		return foreachProperties(target, (field, value) -> eachProperty.accept(field));
	}
	
	public static Object foreachProperties(Object target, BiConsumer<Field, Object> eachProperty) {
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
		foreachProperties(o, (field, value) -> {
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
		foreachProperties(o, (field, value) -> {
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

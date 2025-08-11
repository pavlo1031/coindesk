package cathay.coindeskApi.commons.util.function;

import static cathay.coindeskApi.commons.util.MultiElementUtils.getLength;

import java.util.Collection;
import java.util.Map;

import cathay.coindeskApi.commons.util.MultiElementUtils;
import cathay.coindeskApi.commons.util.NumberUtils;

/**
 * 一般用途的Hoc
 */
public class Hoc {

	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	public static <T> Predicate<T> isNull() {
		return (o) -> o == null;
	}

	public static <T> Predicate<T> isNull(String variableName) {
		return (o) -> o == null;
	}
	
	public static <T> Predicate<T> isNotNull() {
		return (o) -> o != null;
	}

	public static <T> Predicate<T> isNotNull(String variableName) {
		return (o) -> o != null;
	}
	
	///////////////////////////////// 數值 /////////////////////////////////
	
	public static Predicate numberEquals(Number constant) {
		return numberMatches((n) -> n.equals(constant));
	}
	
	////////////////

	public static Predicate numberGreaterThan(Number constant) {
		return numberMatches((n) -> NumberUtils.substract(n, constant).intValue() > 0);
	}
	
	public static Predicate numberGreaterOrEqualTo(Number constant) {
		return numberMatches((n) -> NumberUtils.substract(n, constant).intValue() >= 0);
	}
	
	////////////////
	
	public static Predicate numberLessThan(Number constant) {
		return numberMatches((n) -> NumberUtils.substract(n, constant).intValue() < 0);
	}
	
	public static Predicate numberLessOrEqualTo(Number constant) {
		return numberMatches((n) -> NumberUtils.substract(n, constant).intValue() <= 0);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/*package*/ static Predicate<? extends Number> numberMatches(Predicate<? extends Number> condition) {
		return numberMatches(condition, null);
	}
	
	/*package*/ static Predicate<? extends Number> numberMatches(Predicate<? extends Number> condition, String conditionDescription) {
		return (Number n) -> {
			return ((Predicate<Number>) condition).test(n);
		};
	}
	
	//////////////////////////// 容器 (array, collection) ////////////////////////////

	public static <T> Predicate<T> sizeEquals(int size) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
				return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) == size);
			}
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}

	////////////////
	
	public static <T> Predicate<T> sizeGreaterThan(int minSize) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
				return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) > minSize);
			}
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	public static <T> Predicate<T> sizeGreaterOrEqualTo(int minSize) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
				return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) >= minSize);
			}
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	////////////////
	
	public static <T> Predicate<T> sizeLessThan(int upboundExclusive) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
				return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) < upboundExclusive);
			}
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	public static <T> Predicate<T> sizeLessOrEqualTo(int upbound) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray() || Collection.class.isAssignableFrom(argumentClass) || Map.class.isAssignableFrom(argumentClass)) {
				return MultiElementUtils.ifThenReturn(argument, (values) -> getLength(values) <= upbound);
			}
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	////////////////
	
	public static <T> Predicate<T> existsIn(Object... constants) {
		return (T data) -> {
			if (data == null)
				return false;
			for (int i=0; i < constants.length; i++)
				if (data.equals(constants[i]))
					return true;
			return false;
		};
	}
	
	public static <T> Predicate<T> existsIn(Collection collection) {
		return (T data) -> {
			if (data == null)
				return false;
			return collection.stream().anyMatch((elem) -> data.equals(elem));
		};
	}
	
	//////////////////////////// 容器 (map) /////////////////////////////
	
	public static Predicate containsKey(Object key) {
		Predicate<Map> predicate = (Map map) -> {
			if (map == null)
				return false;
			return map.containsKey(key);
		};
		return predicate;
	}
}

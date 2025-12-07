package cathay.coindeskApi.commons.util.function;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

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
	
	public static Predicate numberGreaterThan(final Number constant) {
		return numberMatches((n) ->  Float.compare(n.floatValue(), constant.floatValue()) > 0);
	}
	
	public static Predicate numberGreaterOrEqualTo(Number constant) {
		return numberMatches((n) ->  Float.compare(n.floatValue(), constant.floatValue()) >= 0);
	}
	
	////////////////
	
	public static Predicate numberLessThan(Number constant) {
		return numberMatches((n) ->  Float.compare(n.floatValue(), constant.floatValue()) < 0);
	}
	
	public static Predicate numberLessOrEqualTo(Number constant) {
		return numberMatches((n) ->  Float.compare(n.floatValue(), constant.floatValue()) <= 0);
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

	public static <T> Predicate<T> sizeEquals(final int size) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray())
				return size == Array.getLength(argument);	
			if (Collection.class.isAssignableFrom(argumentClass))
				return size == ((Collection) argument).size();
			if (Map.class.isAssignableFrom(argumentClass))
				return size == ((Map) argument).size();
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}

	////////////////
	
	public static <T> Predicate<T> sizeGreaterThan(final int minSize) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray())
				return Array.getLength(argument) > minSize;	
			if (Collection.class.isAssignableFrom(argumentClass))
				return ((Collection) argument).size() > minSize;
			if (Map.class.isAssignableFrom(argumentClass))
				return ((Map) argument).size() > minSize;
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	public static <T> Predicate<T> sizeGreaterOrEqualTo(int minSize) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray())
				return Array.getLength(argument) >= minSize;	
			if (Collection.class.isAssignableFrom(argumentClass))
				return ((Collection) argument).size() >= minSize;
			if (Map.class.isAssignableFrom(argumentClass))
				return ((Map) argument).size() >= minSize;
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	////////////////
	
	public static <T> Predicate<T> sizeLessThan(final int upboundExclusive) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray())
				return Array.getLength(argument) < upboundExclusive;	
			if (Collection.class.isAssignableFrom(argumentClass))
				return ((Collection) argument).size() < upboundExclusive;
			if (Map.class.isAssignableFrom(argumentClass))
				return ((Map) argument).size() < upboundExclusive;
			throw new IllegalArgumentException("sizeEquals(): argument type " + "'" + argumentClass + "'" + " not supported; the argument must be an array, a collection, or a map.");
		};
	}
	
	public static <T> Predicate<T> sizeLessOrEqualTo(final int upbound) {
		return (T argument) -> {
			if (argument == null)
				return false;
			
			final Class<?> argumentClass = argument.getClass();
			if (argumentClass.isArray())
				return Array.getLength(argument) <= upbound;	
			if (Collection.class.isAssignableFrom(argumentClass))
				return ((Collection) argument).size() <= upbound;
			if (Map.class.isAssignableFrom(argumentClass))
				return ((Map) argument).size() <= upbound;
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
	
	///////////////////////////////// 日期時間 /////////////////////////////////

	public static Predicate isDateBetween(final Date startDate, final Date endDate) {
		Objects.requireNonNull(startDate, "'startDate' argument must not be null");
		Objects.requireNonNull(endDate, "'endDate' argument must not be null");
		if (startDate.getTime() < endDate.getTime() != true)
			throw new IllegalArgumentException("起始日期startDate必須早於endDate");
		
		return (Predicate<Date>) (Date date) -> {
			return date.getTime() >= startDate.getTime() && date.getTime() < endDate.getTime();
		};
	}
	
	public static Predicate isDateBetween(final LocalDate startDate, final LocalDate endDate) {
		return isDateBetween(java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));
	}
	
	public static Predicate isDateBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		Objects.requireNonNull(startDateTime, "'startDateTime' argument must not be null");
		Objects.requireNonNull(endDateTime, "'endDateTime' argument must not be null");
		if (startDateTime.isBefore(endDateTime) != true)
			throw new IllegalArgumentException("起始日期startDateTime必須早於endDateTime");
		return isDateBetween(
				  Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
				  Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant()));
	}

	public static Predicate isDateBefore(final Date before) { return isDateBefore(before, false); }
	
	public static Predicate isDateBeforeInclusive(final Date before) { return isDateBefore(before, true); }
	
	public static Predicate isDateBefore(final Date before, final boolean inclusive) {
		Objects.requireNonNull(before, "The argument 'before' must not be null");
		return (Predicate<Date>) ((Date date) -> {
			if (inclusive)
				return date.getTime() <= before.getTime();
			return date.getTime() <= before.getTime();
		});
	}
	
	public static Predicate isDateAfter(final Date after) { return isDateAfter(after, false); }
	
	public static Predicate isDateInclusive(final Date after) { return isDateAfter(after, true); }
	
	public static Predicate isDateAfter(final Date after, final boolean inclusive) {
		Objects.requireNonNull(after, "The argument 'after' must not be null");
		return (Predicate<Date>) ((Date date) -> {
			if (inclusive)
				return date.getTime() > after.getTime();
			return date.getTime() >= after.getTime();
		});
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

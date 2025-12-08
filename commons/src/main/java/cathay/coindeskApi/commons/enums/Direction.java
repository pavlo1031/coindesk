package cathay.coindeskApi.commons.enums;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Direction {
	
	Forward {
		public <E> Iterable<E> iterable(Object values) {
			Objects.requireNonNull(values, "The argument 'values' cannot be null");
			final Class valuesClass = values.getClass();
			if (!valuesClass.isArray() && !List.class.isAssignableFrom(valuesClass) && !LinkedHashMap.class.isAssignableFrom(valuesClass))
				throw new IllegalArgumentException("Unsupporetd values type '" + valuesClass.getName() + "' , only array, List, LinkedHashMap are allowed.");
			
			return new Iterable<E>() {
				public Iterator<E> iterator() {
					return Forward.<E>iterator(values);
				}
			};
		}

		public <E> Iterator<E> iterator(Object values) {
			Objects.requireNonNull(values, "The argument 'values' cannot be null");
			final Class valuesClass = values.getClass();
			if (!valuesClass.isArray() && !List.class.isAssignableFrom(valuesClass) && !LinkedHashMap.class.isAssignableFrom(valuesClass))
				throw new IllegalArgumentException("Unsupporetd values type '" + valuesClass.getName() + "' , only array, List, LinkedHashMap are allowed.");
			
			final int length = length(values, valuesClass);	
			
			final ArrayList buffer = new ArrayList();
			if (valuesClass.isArray()) {
				final Class<E> elementType = valuesClass.getComponentType();
				for (int i=0; i<length; i++)
					buffer.add(getFromArray(values, i));
			}
			else if (Collection.class.isAssignableFrom(valuesClass)) {
				buffer.addAll((Collection) values);
			}
			else if (Map.class.isAssignableFrom(valuesClass)) {
				buffer.addAll(((Map) values).entrySet());
			}
			
			return new Iterator<E>() {
				int position = 0;
				public boolean hasNext() { return position < length; }
				public E next() { return (E) getFromList(buffer, position++); }
			};
		}
	},

	Reverse {
		public <E> Iterable<E> iterable(Object values) {
			Objects.requireNonNull(values, "The argument 'values' cannot be null");
			final Class valuesClass = values.getClass();
			if (!valuesClass.isArray() && !List.class.isAssignableFrom(valuesClass) && !LinkedHashMap.class.isAssignableFrom(valuesClass))
				throw new IllegalArgumentException("Unsupporetd values type '" + valuesClass.getName() + "' , only array, List, LinkedHashMap are allowed.");
			
			return new Iterable<E>() {
				public Iterator<E> iterator() {
					return Reverse.<E>iterator(values);
				}
			};
		}

		public <E> Iterator<E> iterator(Object values) {
			Objects.requireNonNull(values, "The argument 'values' cannot be null");
			final Class valuesClass = values.getClass();
			if (!valuesClass.isArray() && !List.class.isAssignableFrom(valuesClass) && !LinkedHashMap.class.isAssignableFrom(valuesClass))
				throw new IllegalArgumentException("Unsupporetd values type '" + valuesClass.getName() + "' , only array, List, LinkedHashMap are allowed.");
			
			final int length = length(values, valuesClass);
			
			// 依照目前集合的狀態, 以此時的順序取出
			final ArrayList buffer = new ArrayList();
			if (valuesClass.isArray()) {
				final Class<E> elementType = valuesClass.getComponentType();
				for (int i=0; i<length; i++)
					buffer.add(getFromArray(values, i));
			}
			else if (Collection.class.isAssignableFrom(valuesClass)) {
				buffer.addAll((Collection) values);
			}
			else if (Map.class.isAssignableFrom(valuesClass)) {
				buffer.addAll(((Map) values).entrySet());
			}
			
			return new Iterator<E>() {
				int position = length;
				public boolean hasNext() { return position > 0; }
				public E next() { return (E) getFromList(buffer, --position); }
			};
		}
	};
	
	private static <T> int length(Object values, Class<?> valuesClass) {
		if (valuesClass.isArray())
			return Array.getLength(values);	
		if (List.class.isAssignableFrom(valuesClass))
			return ((List) values).size();
		if (LinkedHashMap.class.isAssignableFrom(valuesClass))
			return ((Map) values).size();
		throw new IllegalArgumentException("Illegal values type '" + valuesClass.getName() + "' , only 'array', 'List', 'LinkedHashMap' are allowed.");
	}
	
	private static <E> E getFromList(List<E> list, int index) {
		return (E) list.get(index);
	}
	
	private static <E> E getFromArray(Object array, int index) {
		final Class elementType = array.getClass().getComponentType();
		final boolean isPrimitive = array.getClass().getComponentType().isPrimitive();
		
		Object elem = null;	
		if (!isPrimitive)
			return (E) Array.get(array, index);
			
		if (byte.class.equals(elementType)) {
			elem = Byte.valueOf(Array.getByte(array, index));
		} else if (short.class.equals(elementType)) {
			elem = Short.valueOf(Array.getShort(array, index));
		} else if (int.class.equals(elementType)) {
			elem = Integer.valueOf(Array.getInt(array, index));
		} else if (long.class.equals(elementType)) {
			elem = Long.valueOf(Array.getLong(array, index));
		} else if (float.class.equals(elementType)) {
			elem = Float.valueOf(Array.getFloat(array, index));
		} else if (double.class.equals(elementType)) {
			elem = Double.valueOf(Array.getDouble(array, index));
		} else if (char.class.equals(elementType)) {
			elem = Character.valueOf(Array.getChar(array, index));
		} else if (boolean.class.equals(elementType)) {
			elem = Boolean.valueOf(Array.getBoolean(array, index));
		}
		return (E) elem;
	}
	
	public abstract <E> Iterator<E> iterator(Object array);
	
	public abstract <E> Iterable<E> iterable(Object array);
	
}

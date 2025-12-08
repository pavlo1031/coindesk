package cathay.coindeskApi.commons.util.types;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Data;

@Data
public class IndexRange {
	private int beginIndex;
    private int endIndex;
    private int length;
    
    private Object values;
    private Boolean isArray;
    private Boolean isList;
    
    public IndexRange(Object values) {
    	this.setValues(values);
    	this.beginIndex = 0;
    	this.endIndex = length - 1;
    }
    
    public IndexRange(Object values, int beginIndex) {
    	this.setValues(values);
    	this.beginIndex = beginIndex;
    	this.endIndex = length - 1;
    }
    
    public IndexRange(Object values, int beginIndex, int endIndex) {
    	this.setValues(values);
    	this.beginIndex = beginIndex;
    	this.endIndex = endIndex;
    }
    
    private IndexRange setValues(Object values) {
    	if (values != null) {
    		Class<?> valueClass = values.getClass();
    		this.values = values;
    		this.isArray = valueClass.isArray();
    		this.isList = List.class.isAssignableFrom(valueClass);
    		
    		// set length
    		if (isArray)
    			this.length = Array.getLength(values);
    		else if (Collection.class.isAssignableFrom(valueClass))
    			this.length = ((Collection) values).size();
    	}
    	return this;
    }
    
    public <E> IndexRange forEach(Consumer<E> iterate) {
    	if (iterate == null)
    		return this;    	
    	for (int i=beginIndex; i<endIndex; i++) {
    		E elem = get(values, i);
    		((Consumer) iterate).accept(elem);
    	}
    	return this;
    }
    
    public <E> IndexRange forEach(BiConsumer<E, ? extends Number> iterate) {
    	if (iterate == null)
    		return this;
    	for (int i=beginIndex; i<endIndex; i++) {
    		E elem = get(values, i);
    		((BiConsumer<E, Number>) iterate).accept(elem, Integer.valueOf(i));
    	}
    	return this;
    }
    
	private static <E> E get(Object array, int index) {
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
}

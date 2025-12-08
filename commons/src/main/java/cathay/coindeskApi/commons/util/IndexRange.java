package cathay.coindeskApi.commons.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Data;

@Data
public class IndexRange {
	private int beginIndex;
    private int endIndex;
    private int length;
    
    private Object value;
    private Boolean isArray;
    private Boolean isList;
    
    public IndexRange(Object value) {
    	this.setValue(value);
    	this.beginIndex = 0;
    	this.endIndex = length - 1;
    }
    
    public IndexRange(Object value, int beginIndex) {
    	this.setValue(value);
    	this.beginIndex = beginIndex;
    	this.endIndex = length - 1;
    }
    
    public IndexRange(Object obj, int beginIndex, int endIndex) {
    	this.setValue(obj);
    	this.beginIndex = beginIndex;
    	this.endIndex = endIndex;
    }
    
    public IndexRange setValue(Object obj) {
    	if (obj != null) {
    		Class<?> valueClass = value.getClass();
    		this.value = obj;
    		this.isArray = valueClass.isArray();
    		this.isList = List.class.isAssignableFrom(valueClass);
    		// set length
    		if (isArray)
    			this.length = Array.getLength(obj);
    		else if (isList)
    			this.length = ((List) obj).size();
    	}
    	return this;
    }
    
    public <E> IndexRange forEach(Consumer<E> iterate) {
    	if (iterate == null)
    		return this;    	
    	for (int i=beginIndex; i<endIndex; i++) {
    		E elem = MultiElementUtils.<E>get(value, i);
    		((Consumer) iterate).accept(elem);
    	}
    	return this;
    }
    
    public <E> IndexRange forEach(BiConsumer<E, ? extends Number> iterate) {
    	if (iterate == null)
    		return this;
    	for (int i=beginIndex; i<endIndex; i++) {
    		E elem = MultiElementUtils.<E>get(value, i);
    		((BiConsumer<E, Number>) iterate).accept(elem, i);
    	}
    	return this;
    }
}

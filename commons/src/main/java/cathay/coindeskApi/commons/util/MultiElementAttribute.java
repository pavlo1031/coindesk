package cathay.coindeskApi.commons.util;

import java.util.Objects;
import java.util.function.Consumer;

public class MultiElementAttribute {

	private final boolean isArray;
	private final boolean isList;
	private final boolean isCollection;
	private final boolean isMap;
	
	Object value;
	
	public MultiElementAttribute(Object value) {
		Objects.requireNonNull(value);
		final Class<?> valueType = value.getClass();
		this.isArray = valueType.isArray();
		this.isList = java.util.List.class.isAssignableFrom(valueType);
		this.isMap = java.util.Map.class.isAssignableFrom(valueType);
		this.isCollection = java.util.Collection.class.isAssignableFrom(valueType);
	}
	
	public static MultiElementAttribute of(Object value) {
		return new MultiElementAttribute(value);
	}
	
	public boolean isArray() { return this.isArray; }
	
	public <T> MultiElementAttribute isArray(Consumer<?> then) {
		if (then != null)
			if (this.isArray)
				((Consumer<Object>) then).accept(value);
		return this;
	}
	
	public boolean isList() { return this.isList; }
	
	public <T> MultiElementAttribute isList(Consumer<T> then) {
		if (then != null)
			if (this.isList)
				((Consumer<Object>) then).accept(value);
		return this;
	}
	
	public boolean isMap() { return this.isMap; }
	
	public <T> MultiElementAttribute isMap(Consumer<T> then) {
		if (then != null)
			if (this.isMap)
				((Consumer<Object>) then).accept(value);
		return this;
	}
}

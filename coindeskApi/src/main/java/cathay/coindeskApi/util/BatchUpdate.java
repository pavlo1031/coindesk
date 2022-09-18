package cathay.coindeskApi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BatchUpdate<FieldType> {

	private HashMap<FieldType, Object> data = new HashMap<FieldType, Object>();
	
	private static BatchUpdate instance;
	
	
	public static <FieldType> BatchUpdate<FieldType> updateFieldValues() {
		if (instance == null)
			instance = new BatchUpdate<FieldType>();
		return instance;
	}
	
	public static <FieldType> BatchUpdate<FieldType> updateFieldValues(FieldType field, Object value) {
		if (instance == null)
			instance = new BatchUpdate<FieldType>();
		if (field != null)
			instance.set(field, value);
		return instance;
	}
	
	private BatchUpdate() {
		instance = this;
	}
	
	private BatchUpdate(FieldType field, Object value) {
		if (field == null)
			throw new IllegalArgumentException("the argument 'field' cannot be null");
		this.data.put(field, value);
		instance = this;
	}
	
	public BatchUpdate<FieldType> set(FieldType field, Object value) {
		this.data.put(field, value);
		return this;
	}
	
	public Set<FieldType> keySet() {
		return this.data.keySet();
	}
	
	public Set<Map.Entry<FieldType, Object>> entrySet() {
		return this.data.entrySet();
	}
	
	public int size() {
		return this.data.size();
	}
	
	public BatchUpdate<FieldType> reset() {
		this.data.clear();
		return this;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}

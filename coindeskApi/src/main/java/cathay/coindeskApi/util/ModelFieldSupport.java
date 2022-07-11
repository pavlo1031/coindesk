package cathay.coindeskApi.util;

public interface ModelFieldSupport<ModelType, FieldType> {

	Object get(FieldType field);
	
	ModelType set(FieldType field, Object value);
}

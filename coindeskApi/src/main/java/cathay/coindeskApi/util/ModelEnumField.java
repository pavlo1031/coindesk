package cathay.coindeskApi.util;

/**
 * 可透過 "field selector" 讀寫欄位
 * （field selector最好使用enum）
 */
public interface ModelEnumField<ModelType, FieldType extends Enum<?>> {

	Object get(FieldType field);
	
	ModelType set(FieldType field, Object value);
}

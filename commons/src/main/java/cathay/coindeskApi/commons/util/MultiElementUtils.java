package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.TypeSignatureExpression.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 具有集合特性的物件的操作
 */
public class MultiElementUtils {
	/**
	 * 將具有集合特性的物件, 取得其stream傳回
	 */
	public static <E> Stream<E> stream(E[] values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		return (Stream<E>) Arrays.stream(values);
	}
	
	/**
	 * 將具有集合特性的物件, 取得其stream傳回
	 */
	public static <E> Stream<E> stream(Object value) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null.");
		if (!isMultiElementType(value))
			throw new IllegalArgumentException("The argument value must be an array or collection.");

		final Class<?> valueType = value.getClass();
		final boolean isArray = valueType.isArray();
		if (isArray) {
			if (valueType.getComponentType().isPrimitive())
				value = TypeUtils.toBoxedArray(value);
			return Arrays.<E>stream(TypeUtils.toBoxedArray(value));
		}
		// Collection
		// Map
		return (Stream<E>)((Collection) value).stream();
	}
	
	/**
	 * 如果參數value有集合特性, 執行then
	 */
	public static <T, R> T ifThen(T values, Consumer<?> then) {
		final Class<?> valueClass = values.getClass();
		if (valueClass.isArray()) {
			((Consumer<Object>) then).accept(values);
			return values;
		}
		else if (Collection.class.isAssignableFrom(valueClass)) {
			((Consumer<Object>) then).accept((T) values);
			return values;
		}
		else if (Map.class.isAssignableFrom(valueClass)) {
			((Consumer<Object>) then).accept((T) values);
			return values;
		}
		throw new IllegalArgumentException("ifThen(): Unsupported type " + StringUtils.doubleQuoteString(values.getClass().getName()));
	}
	
	/**
	 * 如果參數values有集合特性, 執行then
	 */
	public static <R> R ifThenReturn(Object values, Function<?, ? extends R> thenReturn) {
		final Class<?> valueClass = values.getClass();
		if (valueClass.isArray()) {
			return ((Function<Object, R>) thenReturn).apply(values);
		}
		else if (Collection.class.isAssignableFrom(valueClass)) {
			return ((Function<Object, R>) thenReturn).apply(values);
		}
		else if (Map.class.isAssignableFrom(valueClass)) {
			return ((Function<Object, R>) thenReturn).apply(values);
		}
		throw new IllegalArgumentException("ifThenReturn(): Unsupported type " + StringUtils.doubleQuoteString(values.getClass().getName()));
	}
	
	/**
	 * 取得array, collection, 或是map的長度
	 */
	public static Integer getLength(Object values) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		if (values.getClass().isArray())
			return Array.getLength(values);
		if (Collection.class.isAssignableFrom(values.getClass()))
			return ((Collection) values).size();
		if (Map.class.isAssignableFrom(values.getClass()))
			return ((Map) values).size();
		throw new IllegalArgumentException("Illegal type of argument 'values', only supports array, collection, map");
	}
	
	/**
	 * 取得array, List位於index的元素
	 */
	public static <E> E get(Object values, int index) {
		Objects.requireNonNull(values, "The argument 'values' cannot be null.");
		if (index < 0 || index >= getLength(values))
			throw new IllegalArgumentException("Illegal type of argument 'values', only supports array, collection, map");
		
		// get values' type
		final Class valuesType = values.getClass();
		
		// check type
		if (valuesType.isArray())
			return (E) Array.get(values, index);		
		else if (List.class.isAssignableFrom(valuesType))
			return (E) ((List<?>) values).get(index);
		else if (Map.class.isAssignableFrom(valuesType))
			throw new UnsupportedOperationException("不支援map的索引取值操作, 僅支援array, list的get(int index)");
		throw new UnsupportedOperationException("Illegal type of argument 'values', only indexing operation of the type of array, list are supported.");
	}
	
	
	/**
	 * 判斷參數value是否有集合特性, 例: array, collection, map
	 * 
	 * @param value 待判斷是否為集合的物件
	 * @param thens 結果為真, 且傳入的非型別時, 則呼叫callback
	 */
	public static boolean isMultiElementType(Object value, Consumer<?>...thens) {
		Objects.requireNonNull(value, "The argument 'value' cannot be null");
		// value的型別不同, type取值的方式也不同
		final boolean valueIsNotClass = Class.class != value.getClass();
		final Class<?> type = (valueIsNotClass)? value.getClass() : (Class<?>) value;
		
		// 判斷型別
		if (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			if (valueIsNotClass && (thens != null && thens.length > 0)) {
				for (Consumer<?> then : thens) {
					try {
						((Consumer<Object>) then).accept(value);
					}
					catch (ClassCastException e) {
						// 嘗試匹配
						final Pattern pattern = Pattern.compile(PATTERN_STRING_CLASS_CAST_EXCEPTION_MESSAGE);
						Matcher matcher = pattern.matcher(e.getMessage());
						if (!matcher.matches() /* 執行matches() 才能真正獲取結果 */)
							throw e;
						
						// 抽取出錯誤訊息中的: 來源型別, 和目標型別
						final TypeSignatureExpression typeExpr1 = of(matcher.group(1));
						final TypeSignatureExpression typeExpr2 = of(matcher.group(7));
						
						if (typeExpr1.isMultiElementType() && typeExpr2.isMultiElementType()) {
							// T的聚合型別 --> U[]
							if (typeExpr2.isArray()) {
								Class<?> elemType1 = null;
								Class<?> elemType2 = typeExpr2.getComponentType();

								int length = 0;								
								if (typeExpr1.isArray()) {
									length = Array.getLength(value);
									elemType1 = typeExpr1.getComponentType();
									// array --> array
								}
								else {
									length = ((Collection) value).size();
									elemType1 = null;									
									// collection --> array
								}
								
								// 都是array時
								Object array1 = value;
								Object array2 = Array.newInstance(elemType2, length);
								
								// copy to the target collection (lamda's argument)
								for (int i=0; i<length; i++) {
									final Object inElem = Array.get(array1, i);
									// 數值, 布林, 字元 (primitive, boxed)
									// 字串, CharSequence, StringBuffer, StringBuilder
									// 日期, 時間 (long, 特定格式字串, )
									// (...其他)
									
									Object outElem = inElem;
									// (其他處理)
									Array.set(array2, i, outElem);
								}
								
								// invoke lamda
								((Consumer) then).accept(array2);
							}
							else if (typeExpr2.isCollection()) {
								Class<?> elemType1 = null;
								Class<?> elemType2 = typeExpr2.getComponentType();

								int length = 0;					
								if (typeExpr1.isArray()) {
									length = Array.getLength(value);
									elemType1 = typeExpr1.getComponentType();
									// array --> collection
								}
								else {
									length = ((Collection) value).size();
									elemType1 = Object.class; // 未知
									// collection --> collection
								}
								
								// 如何決定collection具體型別？
								Object array1 = (elemType1.isPrimitive())? TypeUtils.toBoxedArray(value) : value;
								Collection<?> array2 = null;
								
								if (List.class.isAssignableFrom(typeExpr2.getType())) {
									array2 = new ArrayList();
								} else if (Set.class.isAssignableFrom(typeExpr2.getType())) {
									array2 = new LinkedHashSet();
								} else if (Queue.class.isAssignableFrom(typeExpr2.getType())) {
									array2 = new LinkedBlockingQueue();
								}
								
								// copy to the target collection (lamda's argument)
								for (int i=0; i<length; i++) {
									final Object element = Array.get(array1, i);
									((Collection) array2).add(element);
								}
								
								// invoke lamda
								((Consumer) then).accept(array2);
							}
							// 暫時不支援
							else if (typeExpr2.isMap()) {
								throw new UnsupportedOperationException("目前不支援Map型別的Lamda參數", e);
							}
							return true;
						}
						
						// 無法等效轉換, 確定型別轉換失敗
						throw e;
					}
				}
			}
			return true;
		}
		return false;
	}
}

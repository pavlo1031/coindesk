package cathay.coindeskApi.commons.util;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 此類別包裝一些簡單的json操作, 例如: 建立空白json、以及 不同型別和json的轉換
 *  (使得caller不需建立自己的objectMapper)
 */
public class JsonUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 獲取一空的json物件
	 * (此method使得caller不需建立自己的objectMapper)
	 */
	public static ObjectNode createEmptyJsonNode() {
		return objectMapper.createObjectNode();
	}
	
	/**
	 * POJO或map 轉換為jsonNode
	 * 
	 * @param o 欲轉換為json的物件
	 * @throws IllegalArgumentException 轉換失敗時 
	 */
	public static JsonNode getJson(Object o) throws IllegalArgumentException, JsonProcessingException {
		if (o == null)
			throw new NullPointerException("Cannot convert from null");
		
		try {
			// 若已經是json類型, 直接傳回
			if (JsonNode.class.isAssignableFrom(o.getClass()))
				return (JsonNode) o;
			
			if (CharSequence.class.isAssignableFrom(o.getClass()))
				return objectMapper.readTree(o.toString());
			
			// 非json類型, 進行轉換
			// (可以是pojo, map)
			return objectMapper.convertValue(o, JsonNode.class);
		}
		catch (JsonProcessingException e) {
			throw e;
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
	}
	
	public static String getJsonString(String label, Object o) {
		return getJsonString(label, o, false);
	}

	public static String getJsonString(Object o) {
		return getJsonString(null, o, false);
	}
	
	public static String getJsonStringPrettyFormat(Object o) {
		return getJsonString(null, o, true);
	}
	
	public static String getJsonStringPrettyFormat(String label, Object o) {
		return getJsonString(label, o, true);
	}
	
	public static String getJsonString(Object o, boolean prettyOutput) {
		return getJsonString(null, o, true);
	}
	
	/**
	 * 將物件轉換為json形式的字串
	 *  「"附加label在前" 的json顯示方式」 → 僅為個人喜好
	 * 
	 * @param label 加在json輸出最前方, 方便追蹤
	 * @throws IllegalArgumentException 轉換失敗時
	 */
	public static String getJsonString(String label, Object o, boolean prettyOutput) throws IllegalArgumentException {
		if (o == null)
			throw new NullPointerException("Cannot convert from null");
		
		// 決定輸出方式
		ObjectWriter jsonWriter = objectMapper.writer();
		if (prettyOutput)
			jsonWriter = objectMapper.writerWithDefaultPrettyPrinter(); 
		
		StringBuffer buffer = new StringBuffer();
		try {
			// 字串類型
			if (CharSequence.class.isAssignableFrom(o.getClass())) {
				/*
				 * 目前作法: 不轉換不驗證結構, 直接傳回
				 * 
				 * TODO: 如果是傳入字串, 需要驗證字串內容是否為有效JSON結構嗎??
				 *       → 萬一是亂輸入的怎麼辦?
				 */
				return o.toString();
			}
			
			// 進行轉換
			buffer.append(jsonWriter.writeValueAsString(o));
			
			// 附上label
			if (isNotEmpty(label))
				buffer.insert(0, label + " ");
			return buffer.toString();
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Failed to convert JsonNode from the type of " + o.getClass().getName() + 
					", content: \"" + o + "\"", e);
		}
	}
}
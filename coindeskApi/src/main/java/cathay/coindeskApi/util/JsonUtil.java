package cathay.coindeskApi.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author drew lee (李卓翰)
 */
public class JsonUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 獲取一空白的json物件
	 */
	public static ObjectNode createEmptyJsonNode() {
		return objectMapper.createObjectNode();
	}
	
	/**
	 * @param o 欲轉換為json的物件
	 * @return 轉換失敗, 回傳null??
	 * 	            需自訂exception帶回原物件??
	 * @throws IOException 
	 */
	public static JsonNode getJson(Object o) {
		JsonNode json = null;
		try {
			if (String.class == o.getClass()) {
				json = objectMapper.readValue(String.valueOf(o), JsonNode.class);
			}
			else if (Map.class.isAssignableFrom(o.getClass())) {
				String jsonString = objectMapper.writeValueAsString(o);
				json = objectMapper.readValue(jsonString, JsonNode.class);
			}
			else {
				// should throw exception ??
				// or just return null ??
				json = objectMapper.convertValue(o, JsonNode.class);
			}
			return json;
		}
		catch (JsonProcessingException e) {
			return json;
		}
	}
	
	public static String getJsonString(Object o) {
		return getJsonString(o, false);
	}
	
	public static String getJsonStringPrettyFormat(Object o) {
		return getJsonString(o, true);
	}
	
	public static String getJsonString(Object o, boolean prettyOutput) {
		Object obj = getJson(o);
		if (JsonNode.class != obj.getClass() && ObjectNode.class != obj.getClass())
			return String.valueOf(obj);
		
		String jsonString = null;
		try {
			if (prettyOutput)
				jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString((JsonNode) obj);
			else
				jsonString = ((JsonNode) obj).toString();
		}
		catch (JsonProcessingException e) {
			jsonString = ((JsonNode) obj).toString();
		}
		finally {
			if (jsonString == null)
				jsonString = ((JsonNode) obj).toString();
		}
		return jsonString;
	}
}
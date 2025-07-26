package cathay.coindeskApi.commons.util;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.FixedSpaceIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectWriter.GeneratorSettings;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 此類別包裝一些簡單的json操作, 例如: 建立空白json、以及 不同型別和json的轉換
 *  (使得caller不需建立自己的objectMapper)
 */
public class JsonUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static ObjectWriter jsonWriter;
	
	private static Field _generatorSettingsField = null;
	
	private static ObjectWriter.GeneratorSettings _generatorSettings;
	
	private static DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
	
	private static Indenter arrayIndenter = FixedSpaceIndenter.instance;
	
	static {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		JsonUtils.jsonWriter = objectMapper.writer();
		
		/*
		 * objectMapper結構階層: writer -> _generatorSettings -> prettyPrinter
		 */
		Field _generatorSettingsField = null;
		ObjectWriter.GeneratorSettings _generatorSettings = null;
		try {
			_generatorSettingsField = ObjectWriter.class.getDeclaredField("_generatorSettings");
			_generatorSettingsField.setAccessible(true);
			_generatorSettings = (GeneratorSettings) _generatorSettingsField.get(jsonWriter);
			// Keep the field in this class			
			JsonUtils._generatorSettingsField = _generatorSettingsField;
			// Keep the settings in this class
			JsonUtils._generatorSettings = _generatorSettings;
		}
		catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			e.printStackTrace();
		}		
	}
	
	public static ObjectMapper getObjectMapper() { return objectMapper; }
	
	
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
			// (可以是pojo, map, collection型別, 或array)
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
		if (prettyOutput) {
			/*
			 * objectMapper結構階層: writer -> _generatorSettings -> prettyPrinter
			 */
			if (_generatorSettings.prettyPrinter == null) {
				JsonUtils.arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
				JsonUtils.prettyPrinter.indentArraysWith(JsonUtils.arrayIndenter);
				
				// Update/keep in this class
				_generatorSettings = _generatorSettings.with(prettyPrinter);
				
				// restore settings to objectmapper's writer
				try {
					JsonUtils._generatorSettingsField.set(jsonWriter, _generatorSettings);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
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
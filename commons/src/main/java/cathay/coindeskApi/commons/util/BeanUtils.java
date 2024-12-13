package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.CollectionUtils.contains;
import static cathay.coindeskApi.commons.util.JsonUtils.getJsonStringPrettyFormat;
import static cathay.coindeskApi.commons.util.JsonUtils.getObjectMapper;
import static cathay.coindeskApi.commons.util.MapUtils.of;
import static cathay.coindeskApi.commons.util.ReflectionUtils.findDeclaredAccessors;
import static cathay.coindeskApi.commons.util.StringUtils.firstCharToLowercase;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.checkCondition;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.util.Arrays.stream;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 此類別僅為了包裝現有的beans相關的功能 (可能是別的library的實現)
 * 讓它們有額外特性, 例如: bean資料處理完, 會傳回對象
 */
public class BeanUtils {

	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	public static <TargetType> TargetType copyProperties(Object source, TargetType target) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
		return target;
	}
	
	/**
	 * 可用Enum實現不同property??
	 */
	public static <TargetType> TargetType copyProperties(Object source, TargetType target, String... propertyNames) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
		return target;
	}
	
	public static Map<String, Object> getProperty(Object instance, String...properties) {
		Map<String, Object> propertyValues = new HashMap<String, Object>();
		Class<?> type = instance.getClass();
		// ....
		return propertyValues;
	}
	
	/**
	 * key: property
	 * value: Setter name
	 */
	static Map<String, String> getSetterNameByProperty(String... properties) {
		Map<String, Class<?>> propertyTypeMappings = new HashMap<String, Class<?>>();
		for (String property : properties)
			propertyTypeMappings.put(property, null);
		return (Map<String, String>) getAccessorNameByProperty(null, "set", propertyTypeMappings);
	}
	
	/**
	 * key: property
	 * value: Getter name
	 */
	static Map<String, String> getGetterNameByProperty(String... properties) {
		Map<String, Class<?>> propertyTypeMappings = new HashMap<String, Class<?>>();
		for (String property : properties)
			propertyTypeMappings.put(property, null);
		return (Map<String, String>) getAccessorNameByProperty(null, "get", propertyTypeMappings);
	}
	
	/**
	 * key: property
	 * value: Setter name
	 */
	static Map<String, String> getSetterNameByProperty(Class<?> type, String... properties) {
		Map<String, Class<?>> propertyTypeMappings = new HashMap<String, Class<?>>();
		for (String property : properties)
			propertyTypeMappings.put(property, null);
		return (Map<String, String>) getAccessorNameByProperty(type, "set", propertyTypeMappings);
	}
	
	/**
	 * key: property
	 * value: Getter name
	 */
	static Map<String, String> getGetterNameByProperty(Class<?> type, String... properties) {
		Map<String, Class<?>> propertyTypeMappings = new HashMap<String, Class<?>>();
		for (String property : properties)
			propertyTypeMappings.put(property, null);
		return (Map<String, String>) getAccessorNameByProperty(type, "get", propertyTypeMappings);
	}
	
	/**
	 * key: property
	 * value: Setter name
	 */
	static Map<String, String> getSetterNameByProperty(Class<?> type, Map<String, Class<?>> propertyTypeMappings) {
		return (Map<String, String>) getAccessorNameByProperty(type, "set", propertyTypeMappings);
	}
	
	/**
	 * key: property
	 * value: Getter name
	 */
	static Map<String, String> getGetterNameByProperty(Class<?> type, Map<String, Class<?>> propertyTypeMappings) {
		return (Map<String, String>) getAccessorNameByProperty(type, "get", propertyTypeMappings);
	}
	
	/**
	 * @param accessType 可以是"get" | "set"
	 * @return mappings (property -> accessor method name)
	 */
	private static Map<String, ?> getAccessorNameByProperty(Class<?> type, String accessType, Map<String, Class<?>> propertyTypeMappings) {
		// check acessType:
		final int accessTypeLen = checkCondition(accessType, StringUtils::isNotBlank, "The argument 'accessType' cannot be null.").length();
		
		// check propertyTypeMappings:
		checkCondition(propertyTypeMappings, Objects::nonNull, "The argument 'propertyTypeMappings' cannot be null.", () -> {
			// 必須至少有一 "非空"entry
			if (propertyTypeMappings.size() == 1 && propertyTypeMappings.containsKey(null))
				throw new IllegalArgumentException("The argument 'propertyTypeMappings' must contain 1 non-null key.");	
		});

		/*
		 * declared accessor methods:
		 * K: method name
		 * V: the declared Method
		 */
		final Map<String, Method> declaredAccessorMethods = findDeclaredAccessors(type, accessType);
		
		/*
		 * method names that match accessor-method pattern
		 * (starting with "is", "get", "set")
		 */
		final Set<String> declaredAccessorNames = new HashSet<String>(declaredAccessorMethods.keySet());

		/*
		 * return value
		 * K: property name
		 * V: accessor method name
		 */
		Map<String, Object> returnAccessorNames = new LinkedHashMap<String, Object>();

		// Prepare buffer
		final StringBuilder buffer = threadLocalBuffer.get();
		
		// 要支援平行處理 ??
		propertyTypeMappings.keySet().stream().filter((s) -> s != null && s.trim().length() > 0)
		.forEach((property) -> {
			final Class<?> propertyType = propertyTypeMappings.get(property);
			final int propertyLen = property.length();

			final String prefix = (propertyType != null && boolean.class == propertyType)? "is" : accessType;			
			final int prefixLen = prefix.length();
			
			// 取出第1,2字元, 判斷大小寫
			final Character firstChar = property.charAt(0);
			final Character secondChar = (propertyLen >= 2)? property.charAt(1) : null;
			System.out.println("property: " + "\"" + property + "\"" + (propertyType != null? " (type: "+propertyType.getName()+"): " : ": "));
			
			// for these Test cases, we debug step by step
			switch(property) {
			  case "single":
			  case "nAme":
			  case "id":
			  case "isRich":
				System.out.print("");
				break;
			  case "xYz":
				System.out.print("");
				break;
			}
			
			// prepare flag
			boolean found = false;
			
			if (propertyLen == 1) {
				buffer.append(prefix)
					  .append(toUpperCase(firstChar));
				found = contains(declaredAccessorNames, buffer.toString());
			}
			else if (propertyLen >= 2) {
				buffer.append(prefix)
				      .append(property);
				
				// 特殊狀況(一): 第2字母大寫
				if (isUpperCase(secondChar)) {
					System.out.println("[WARN] >>>>>>>>>>> It's special case >>>>>>>>>>>");
					
					// 第1字母"無條件"轉大寫 (lombok)
					if (!found) {
						System.out.println("[INFO] 第1字母\"無條件\"轉大寫: ");
						
						// fix
						buffer.setCharAt(prefixLen, toUpperCase(firstChar));
						// lookup:
						final String buffereMethodName = buffer.toString();
						found = contains(declaredAccessorNames, buffereMethodName,
								 (hitMethod) -> {
									System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
								 },
								 () -> {
								 	System.out.println("[Fail] miss: " + buffereMethodName);
								 });
						
						// fix for boolean types
						if (!found) {
							String newPrefix = "get".equals(accessType)
												? "get".equals(prefix)? "is":"get"
												: prefix;
							// fix
							buffer.replace(0, prefixLen, newPrefix);
							// lookup for "is" prefix
							found = contains(declaredAccessorNames, buffer.toString(),
									 (hitMethod) -> {
										System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
									 },
									 () -> {
									 	System.out.println("[Fail] miss: " + buffer.toString());
									 	// 未找到, 還原回prefix
										buffer.replace(0, newPrefix.length(), prefix);
									 });
						}
					}
					
					// 全部大小寫和原本不變
					if (!found) {
						System.out.println("[INFO] 全部大小寫和原本不變: ");
						
						// fix:
						buffer.setCharAt(prefixLen, firstChar);
						buffer.setCharAt(prefixLen+1, secondChar);						
						// lookup:
						found = contains(declaredAccessorNames, buffer.toString(),
								 (hitMethod) -> {
									System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
								 },
								 () -> {
								 	System.out.println("[Fail] miss: " + buffer.toString());
								 });
						
						// fix for boolean types
						if (!found) {
							String newPrefix = "get".equals(accessType)
									? "get".equals(prefix)? "is":"get"
									: prefix;
							
							// Try fix
							buffer.replace(0, prefixLen, newPrefix);
							// lookup:
							found = contains(declaredAccessorNames, buffer.toString(),
									 (hitMethod) -> {
										System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
									 },
									 () -> {
									 	System.out.println("[Fail] miss: " + buffer.toString());
										// 未找到, 還原回prefix
									 	buffer.replace(0, newPrefix.length(), prefix);
									 });
						}
					}
				} // End of Special case
				
				
				// 特殊狀況(二): 傳回Boolean | boolean
				//             ①.Boolean: 前綴使用 "get",
				//             ②.boolean | Boolean: property的開頭是"is"
				
				// 一般狀況:
				// -> property第1字母轉大寫, 第2字母轉小寫
				if (!found) {
					System.out.println("[INFO] property第1字母轉大寫, 第2字母轉小寫");
					
					// fix
					buffer.setCharAt(prefixLen, toUpperCase(firstChar));
					buffer.setCharAt(prefixLen+1, toLowerCase(secondChar));
					// lookup
					final String buffereMethodName = buffer.toString();
					found = contains(declaredAccessorNames, buffer.toString(),
							 (hitMethod) -> {
								System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
							 },
							 () -> {
							 	System.out.println("[Fail] miss: " + buffereMethodName);
							 });
					
					// fix for boolean types
					if (!found) {
						String newPrefix = "get".equals(accessType)
								? "get".equals(prefix)? "is":"get"
								: prefix;
						// Try fix
						buffer.replace(0, prefixLen, newPrefix);
						// lookup for "is" prefix
						found = contains(declaredAccessorNames, buffer.toString(),
								 (hitMethod) -> {
									System.out.println("[OK] ➜ hit: " + hitMethod + "\n");
								 },
								 () -> {
								 	System.out.println("[Fail] miss: " + buffer.toString() + "\n");
								 	// 未找到, 還原回prefix
								 	buffer.replace(0, newPrefix.length(), prefix);
								 });
					}
				}

				// log
				if (!found) System.out.println("[Fail] ======== All not declared in the type ! ======== \n");
				
				// collect existing property
				if (found) returnAccessorNames.put(property, buffer.toString());
				else returnAccessorNames.put(property, null);
			}
			buffer.setLength(0);
			System.out.println();
		});
		return returnAccessorNames;
	}
	
	
	/**
	 * 由setter name, 取出property
	 */
	@SuppressWarnings("unchecked")
	static Map<String, String> getPropertyNameBySetter(String... setters) {
		return (Map<String, String>) getPropertyNameByAccessor(null, "set", setters);
	}
	
	/**
	 * 由getter name, 取出property
	 */
	@SuppressWarnings("unchecked")
	static Map<String, String> getPropertyNameByGetter(String... getters) {
		return (Map<String, String>) getPropertyNameByAccessor(null, "get", getters);
	}
	
	/**
	 * @param accessType 可以是"get" | "set"
	 */
	private static Map<String, ?> getPropertyNameByAccessor(Class<?> type, String accessType, String... accessors) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		// 要支援平行處理 ??
		stream(accessors)
		.parallel()
		.forEach((accessor) -> {
			String getter_ = null;
			if (accessor.startsWith(accessType))
				getter_ = accessor.substring(3);
			else
				getter_ = null;
			
			if (getter_ == null || getter_.length() == 0)
				map.put(accessor, null);
			else
				map.put(accessor, firstCharToLowercase(getter_));
		});
		return map;
	}
}

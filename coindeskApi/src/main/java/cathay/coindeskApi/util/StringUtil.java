package cathay.coindeskApi.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	public static String quoteString(String s) {
		return "\'" + s + "\'"; 
	}
	
	public static String quoteString(Object obj) {
		return ((obj != null)? "\'" + obj.toString() + "\'" : "null"); 
	}
	
	public static String doubleQuoteString(String s) {
		return "\"" + s + "\""; 
	}
	
	public static String doubleQuoteString(Object obj) {
		return ((obj != null)? "\"" + obj.toString() + "\"" : "null"); 
	}
	
	public static String quoteWithBrackets(String s) {
		return "[" + s + "]"; 
	}
	
	public static String quoteWithBrackets(Object obj) {
		return ((obj != null)? "[" + obj.toString() + "]" : "null"); 
	}
	
	public static String quoteWithBraces(String s) {
		return "{" + s + "}"; 
	}
	
	public static String quoteWithBraces(Object obj) {
		return ((obj != null)? "{" + obj.toString() + "}" : "null"); 
	}
	
	public static String repeat(String str, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<count; i++) {
			builder.append(str);
		}
		return builder.toString();
	}
	
	public static boolean isAnyNonBlank(String...array) {
		return StringUtils.isAllBlank(array);
	}
	
	public static boolean isAnyNonEmpty(String...array) {
		return StringUtils.isAllEmpty(array);
	}
}

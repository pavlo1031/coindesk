package cathay.coindeskApi.commons.util;

public class StringUtils {
	
	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	public static String quoteString(CharSequence s) {
		return quoteWith(s, "'", "'");
	}
	
	public static String quoteString(Object obj) {
		return quoteWith(obj, "'", "'");
	}
	
	public static String doubleQuoteString(CharSequence s) {
		return quoteWith(s, '"', '"');
	}
	
	public static String doubleQuoteString(Object obj) {
		return quoteWith(obj, '"', '"');
	}
	
	public static String quoteWithBrackets(CharSequence s) {
		return quoteWith(s, "[", "]");
	}
	
	public static String quoteWithBrackets(Object obj) {
		return quoteWith(obj, "[", "]");
	}

	public static String quoteWithBraces(CharSequence s) {
		return quoteWith(s, "{", "}");
	}
	
	public static String quoteWithBraces(Object obj) {
		return quoteWith(obj, "{", "}"); 
	}
	
	public static String quoteWith(Object obj, Character leadingQuote, Character endingQuote) {
		return quoteWith(obj, leadingQuote.toString(), endingQuote.toString()); 
	}
	
	public static String quoteWith(CharSequence s, String leadingQuote, String endingQuote) {
		return quoteWith((Object) s, leadingQuote, endingQuote); 
	}
	
	public static String quoteWith(Object obj, String leadingQuote, String endingQuote) {
		StringBuilder buffer = threadLocalBuffer.get();
		try {
			return ((obj != null)? buffer.append(leadingQuote).append(obj.toString()).append(endingQuote).toString() : "null");
		} finally {
			buffer.setLength(0);
		}
	}
	
	public static String repeat(CharSequence str, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<count; i++) {
			builder.append(str);
		}
		return builder.toString();
	}
	
	public static boolean isAnyNonBlank(String...array) {
		return org.apache.commons.lang3.StringUtils.isAllBlank(array);
	}
	
	public static boolean isAnyNonEmpty(String...array) {
		return org.apache.commons.lang3.StringUtils.isAllEmpty(array);
	}
}

package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.existsIn;
import static cathay.coindeskApi.commons.util.ArrayUtils.findAnyNotMatching;
import static cathay.coindeskApi.commons.util.TypeUtils.toBoxedArray;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringUtils {
	
	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);

	/**
	 * 高階函數都定義在此處
	 */
	public static class Hoc {
		
		public static class Collectors {
			public static Collector<Character, ?, String> joining() {
				return new Collector<Character, StringBuilder, String>() {
		            public java.util.function.Supplier<StringBuilder> supplier() { return StringBuilder::new; }
		            public java.util.function.BiConsumer<StringBuilder, Character> accumulator() { return StringBuilder::append; }
		            public java.util.function.BinaryOperator<StringBuilder> combiner() { return StringBuilder::append; }
		            public java.util.function.Function<StringBuilder, String> finisher() { return StringBuilder::toString; }
		            public Set<Characteristics> characteristics() { return Collections.emptySet(); }
		        };
			}	
		}
	}
	
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
	
	public static String quoteWithParentheses(CharSequence s) {
		return quoteWith(s, "(", ")");
	}
	
	public static String quoteWithParentheses(Object obj) {
		return quoteWith(obj, "(", ")");
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
	
	public static String removeQuote(String s, Character quote) {
		Objects.requireNonNull(s, "The argument 's' cannot be null");
		if (quote == null)
			return s;
		return Arrays.<Character>stream(toBoxedArray(s.toCharArray())).filter((ch) -> !quote.equals(ch)).collect(Hoc.Collectors.joining());
	}
	
	/**
	 * FIXME: 需檢查leading, ending quote之順序正確性??
	 */
	public static String removeQuote(String s, Character leadingQuote, Character endingQuote) {
		Objects.requireNonNull(s, "The argument 's' cannot be null");
		if (leadingQuote == null && endingQuote == null)
			return s;
		return Arrays.<Character>stream(toBoxedArray(s.toCharArray())).filter((ch) -> !existsIn(ch, leadingQuote, endingQuote)).collect(Hoc.Collectors.joining());
	}
	
	public static String repeat(CharSequence str, int count) {
		StringBuilder buffer = threadLocalBuffer.get();
		try {
			for (int i=0; i<count; i++)
				buffer.append(str);
			return buffer.toString();
		} finally {
			buffer.setLength(0);
		}
	}
	
	/**
	 * join all the element string together and append to the first argument 'str'
	 * 
	 * @param str the source string (NOT a delimiter string) which all the elements are appended to;
	 *         for a null value, str will be treated as a empty string.
	 */
	public static String join(CharSequence str, CharSequence...elements) {
		StringBuilder buffer = threadLocalBuffer.get().append((str != null)? str : "");
		try {
			for (int i=0; i<elements.length; i++) {
				buffer.append(elements[i]);
			}
			return buffer.toString();
		}
		finally {
			buffer.setLength(0);
		}
	}
	
	/**
	 * 將字串s中的所有targes, 都取代為replacement
	 */
	public static String replace(String s, CharSequence replacement, CharSequence...targets) {
		Objects.requireNonNull(s, "The argument 's' cannot be null.");
		StringBuilder buffer = threadLocalBuffer.get().append(s);
		try {
			for (CharSequence target: targets) {
				int start = -1;
				while ((start = buffer.indexOf(target.toString())) != -1) {
					int end = start + target.length();
					buffer.replace(start, end, replacement.toString());	
				}
			}
			return buffer.toString();
		}
		finally {
			buffer.setLength(0);
		}
	}
	
	/**
	 * 移除字串s中 所有target中的片段
	 */
	public static String remove(String s, CharSequence...targets) {
		Objects.requireNonNull(s, "The argument 's' cannot be null.");
		return replace(s, "", targets);
	}
	
	/**
	 * 移除字串s中 所有符合pattern的片段
	 */
	public static String removeAll(String s, String regex) {
		Objects.requireNonNull(s, "The argument 's' cannot be null.");
		if (regex == null)
			return s;
		return s.replaceAll(regex, "");
	}
	
	public static char reverseCase(char ch) {
		if (isUpperCase(ch))
			return toLowerCase(ch);
		return toUpperCase(ch);
	}
	
	public static String reverseCase(String s) {
		StringBuilder buffer = threadLocalBuffer.get().append((s != null)? s : "");
		try {
			for (char ch : s.toCharArray())
				buffer.append(isUpperCase(ch)? toLowerCase(ch) : toUpperCase(ch));
		    return buffer.toString();
		}
		finally {
			buffer.setLength(0);
		}	    
	}
	
	public static String reverseCase(String s, int[] atPositions) {
		return reverseCase(s, Arrays.stream(atPositions).boxed().toArray(Integer[]::new));
	}
	
	/**
	 * 僅有atPosition陣列中指定的位置上的字元, 才做大小寫反轉
	 */
	public static String reverseCase(String s, Integer[] atPositions) {
		// validate argument 's'
		requireNonBlank(s);
		
		// build a buffer for the string argument
		StringBuilder buffer = threadLocalBuffer.get();
		
		// 檢查indexes各元素的內容是否正確？ i>=0, i<s.length
		Integer[] notMatchings = findAnyNotMatching((index) -> index >= 0 && index < s.length(), atPositions);
		if (notMatchings.length > 0) {
			String notMatchingString = Arrays.toString((Object[]) notMatchings);
			int len = notMatchingString.length();
			throw new IllegalArgumentException("illegal out-of-bounds indexes at the positions of: " + notMatchingString.substring(1, len-1));
		}
		
		// 以高效率取出元素的容器來管理
		HashSet<Integer> atPositionsSet = new HashSet<Integer>(Arrays.asList(atPositions));
		
		// 將特定位置的字元, 逐一反轉大小寫
		char[] chars = s.toCharArray();
	    try {
	    	for (int i=0; i<chars.length; i++) {
		    	char ch = chars[i];
		    	if (atPositionsSet.contains(i))
		    		buffer.append(isUpperCase(ch)? toLowerCase(ch) : toUpperCase(ch));
		    	else
		    		buffer.append(ch);
		    }
	    	return buffer.toString();
		}
	    finally {
			buffer.setLength(0);
		}
	}
	
	public static String firstCharToUppercase(String s) {
		requireNonNull(s, "argument cannot be null");
		requireNonBlank(s, "argument cannot be blank");
		
		StringBuilder buffer = threadLocalBuffer.get();
		try {
			buffer.append(toUpperCase(s.charAt(0)));
			buffer.append(s.substring(1));
			return buffer.toString();
		}
		finally {
			buffer.setLength(0);
		}
	}
	
	public static String firstCharToLowercase(String s) {
		requireNonNull(s, "argument cannot be null");
		requireNonBlank(s, "argument cannot be blank");

		StringBuilder buffer = threadLocalBuffer.get();
		try {
			buffer.append(toLowerCase(s.charAt(0)));
			buffer.append(s.substring(1));
			return buffer.toString();	
		}
		finally {
			buffer.setLength(0);
		}
	}
	
	public static String requireNonEmpty(String s) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(s))
            throw new IllegalArgumentException("The string argument must not be empty content.");
        return s;
    }
	
	public static String requireNonEmpty(String s, String message) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(s))
            throw new IllegalArgumentException(message);
        return s;
    }
	
	public static String requireNonEmpty(String s, Supplier<String> messageSupplier) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(s))
            throw new IllegalArgumentException(messageSupplier.get());
        return s;
    }
	
	public static String requireNonBlank(String s) {
        if (org.apache.commons.lang3.StringUtils.isBlank(s))
            throw new IllegalArgumentException("The string argument must not be null, empty, or whitespaces-only content (ASCII code <= 32).");
        return s;
    }
	
	public static String requireNonBlank(String s, String message) {
        if (org.apache.commons.lang3.StringUtils.isBlank(s))
            throw new IllegalArgumentException(message);
        return s;
    }
	
	public static String requireNonBlank(String s, Supplier<String> messageSupplier) {
        if (org.apache.commons.lang3.StringUtils.isBlank(s))
            throw new IllegalArgumentException(messageSupplier.get());
        return s;
    }
	
	public static boolean isNotEmpty(CharSequence s, Consumer<? super CharSequence> then) {
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(s))
			then.accept(s);
		return true;
	}
	
	public static boolean isNotBlank(CharSequence s, Consumer<? super CharSequence> then) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(s))
			then.accept(s);
		return true;
	}
	
	public static boolean isAnyNotBlank(CharSequence...array) {
		return org.apache.commons.lang3.StringUtils.isAllBlank(array);
	}
	
	public static boolean isAnyNotEmpty(CharSequence...array) {
		return org.apache.commons.lang3.StringUtils.isAllEmpty(array);
	}
}

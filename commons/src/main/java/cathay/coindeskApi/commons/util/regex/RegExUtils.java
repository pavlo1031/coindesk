package cathay.coindeskApi.commons.util.regex;

import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cathay.coindeskApi.commons.util.function.TriConsumer;

public class RegExUtils {
	
	/////////////////////// No forEachGroup callback ///////////////////////

	public static boolean matches(String patternString, String input) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, (Function<Matcher, Boolean>) null, (Function<Matcher, ?>) null);
	}
	
	public static boolean matches(String patternString, String input, Consumer<Matcher> successThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>) null);
	}
	
	public static boolean matches(String patternString, String input, Consumer<Matcher> successThen, Consumer<Matcher> failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>)((matcher) -> {failThen.accept(matcher); return null;}));
	}
	
	public static boolean matches(String patternString, String input, Consumer<Matcher> successThen, Runnable failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>)((matcher) -> { failThen.run(); return null;}));
	}
	
	public static boolean matches(String patternString, String input, Consumer<Matcher> successThen, Function<Matcher, ?> failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), failThen);
	}
	
	public static <T> T matches(String patternString, String input, Function<Matcher, T> successThenReturn) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, successThenReturn, (Function<Matcher, ?>) null);
	}
	
	public static <T> T matches(String patternString, String input, Function<Matcher, T> successThenReturn, Consumer<Matcher> failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, successThenReturn, (matcher) -> {failThen.accept(matcher); return null;});
	}
	
	public static <T> T matches(String patternString, String input, Function<Matcher, T> successThenReturn, Runnable failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, successThenReturn, (matcher) -> { failThen.run(); return null;});
	}

	public static <T> T matches(String patternString, String input, Function<Matcher, T> successThenReturn, Function<Matcher, ?> failThen) {
		return matches0(patternString, input, (TriConsumer<Matcher, String, Integer>) null, successThenReturn, failThen);
	}
	
	/////////////////////// 2個參數的forEachGroup ///////////////////////

	public static boolean matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), (Function<Matcher, Boolean>) null, (Function<Matcher, ?>) null);
	}
	
	public static boolean matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Consumer<Matcher> successThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>) null);
	}

	public static boolean matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Consumer<Matcher> successThen, Runnable failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (matcher) -> {failedThen.run(); return null;});
	}
	
	public static boolean matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Consumer<Matcher> successThen, Consumer<Matcher> failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (matcher) -> {failedThen.accept(matcher); return null;});
	}
	
	public static boolean matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Consumer<Matcher> successThen, Function<Matcher, ?> failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), failedThen);
	}
	
	public static <T> T matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Function<Matcher, T> successThenReturn) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), successThenReturn, (Function<Matcher, ?>) null);
	}

	public static <T> T matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Runnable failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), successThenReturn, (matcher) -> {failedThen.run(); return null;});
	}
	
	public static <T> T matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Function<Matcher, ?> failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), successThenReturn, failedThen);
	}
	
	public static <T> T matches(String patternString, String input, BiConsumer<String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Consumer<Matcher> failedThen) {
		return matches0(patternString, input, (matcher, group, index) -> forEachGroup.accept(group, index), successThenReturn, (matcher) -> {failedThen.accept(matcher); return null;});
	}
	
	/////////////////////// 3個參數的forEachGroup ///////////////////////
	
	public static boolean matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup) {
		return matches0(patternString, input, forEachGroup, (Function<Matcher, Boolean>) null, (Function<Matcher, ?>) null);
	}
	
	public static boolean matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Consumer<Matcher> successThen) {
		return matches0(patternString, input, forEachGroup, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>) null);
	}

	public static boolean matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Consumer<Matcher> successThen, Runnable failedThen) {
		return matches0(patternString, input, forEachGroup, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>)(matcher) -> { failedThen.run(); return null; });
	}
	
	public static boolean matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Consumer<Matcher> successThen, Consumer<Matcher> failedThen) {
		return matches0(patternString, input, forEachGroup, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), (Function<Matcher, ?>)(matcher) -> { failedThen.accept(matcher); return null; });
	}
	
	public static boolean matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Consumer<Matcher> successThen, Function<Matcher, ?> failedThen) {
		return matches0(patternString, input, forEachGroup, (Function<Matcher, Boolean>)((matcher) -> { successThen.accept(matcher); return null; }), failedThen);
	}
	
	public static <T> T matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Function<Matcher, T> successThenReturn) {
		return matches0(patternString, input, forEachGroup, successThenReturn, (Function<Matcher, ?>) null);
	}

	public static <T> T matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Runnable failedThen) {
		return matches0(patternString, input, forEachGroup, successThenReturn, (Function<Matcher, ?>)(matcher) -> { failedThen.run(); return null;});
	}
	
	public static <T> T matches(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Function<Matcher, ?> failedThen) {
		return matches0(patternString, input, forEachGroup, successThenReturn, failedThen);
	}
	
	private static <T> T matches0(String patternString, String input, TriConsumer<Matcher, String, Integer> forEachGroup, Function<Matcher, T> successThenReturn, Function<Matcher, ?> failedThen) {
		// Java Regex operation
		final Pattern pattern = Pattern.compile(patternString);
		final Matcher matcher = pattern.matcher(input);
		
		 /* 執行matches() 才能真正獲取結果 */
		Boolean isMatching = matcher.matches();
		if (isMatching) {
			if (forEachGroup != null)
				for(int i=1; i<=matcher.groupCount(); i++)
					forEachGroup.accept(matcher, matcher.group(i), i);
			
			// Caller自訂回傳結果
			if (successThenReturn != null) {
				T returnValue = null;
				if ((returnValue = successThenReturn.apply(matcher)) != null)
					return returnValue;
			}
		}
		else {
			if (failedThen != null) {
				Object returnValue = null;
				Class<?> returnValueType = null;
				try {
					returnValue = failedThen.apply(matcher);
					returnValueType = (returnValue != null)? returnValue.getClass() : null;
					
					// No present returnValue
					if (returnValueType == null)
						return (T) Boolean.valueOf(false);
					
					if (Throwable.class.isAssignableFrom(returnValueType)) {
						if (RuntimeException.class.isAssignableFrom(returnValueType))
							throw (RuntimeException) returnValue;
						throw new RuntimeException((Throwable) returnValue);
					}
					else if (CharSequence.class.isAssignableFrom(returnValueType)) {
						final String failMessage = returnValue.toString();
						throw new RuntimeException(failMessage);
					}
					else {
						//傳回字定義物件
						return (T) returnValue;
					}
				}
				catch(Throwable e) {
					e.printStackTrace();
					if (RuntimeException.class.isAssignableFrom(e.getClass()))
						if (returnValue != null)
							throw (RuntimeException) returnValue;
					throw new RuntimeException("RegExUtils.matches() threw an exception: " + doubleQuoteString(e.getMessage()), e);
				}
			}
		}
		return (T) isMatching;
	}
}

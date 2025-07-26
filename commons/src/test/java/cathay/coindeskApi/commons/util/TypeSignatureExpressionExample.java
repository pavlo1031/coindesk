package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.regex.RegExUtils.matches;
import static cathay.coindeskApi.commons.util.TypeSignatureExpression.*;

import java.util.regex.Matcher;

import cathay.coindeskApi.commons.util.function.TriConsumer;

public class TypeSignatureExpressionExample {

	public static void main(String[] args) {
		TriConsumer<Matcher, String, Integer> forEachGroup = (matcher, group, index) -> {
			System.out.println("[forEachGroup] group " + index + ": " + group);
			if (index == matcher.groupCount())
				System.out.println();
		};
		
		// class java.lang.Integer cannot be cast to class java.lang.String (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')
		
		final String input1 = "class [L:java.lang.Integer; cannot be cast to class [Ljava.lang.String; (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')";
		
		matches(PATTERN_STRING_CLASS_CAST_EXCEPTION_MESSAGE, input1, forEachGroup,
		  // success
		  (matcher) -> {
			System.out.println("[INFO] 解析成功: " + matcher.group(1));
			
			TypeSignatureExpression expr1 = of (matcher.group("src"));
			TypeSignatureExpression expr2 = of (matcher.group("dest"));
			System.out.println("expr1: " + JsonUtils.getJsonStringPrettyFormat(expr1));
			System.out.println("expr2: " + JsonUtils.getJsonStringPrettyFormat(expr2));
		});
	}
}

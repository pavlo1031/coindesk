package cathay.coindeskApi.commons.util.regex;

import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.TypeSignatureExpression.PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION;
import static cathay.coindeskApi.commons.util.regex.Patterns.PATTERN_FQCN;

import java.util.regex.Matcher;

import cathay.coindeskApi.commons.util.function.TriConsumer;

public class PatternsExample {

	public static void main(String[] args) {
		/* dump the groups */
		TriConsumer<Matcher, String, Integer> forEachGroup = (matcher, group, index) -> {
			System.out.println("[forEachGroup] group " + index + ": " + doubleQuoteString(group));
			if (index == matcher.groupCount())
				System.out.println();
		};
		
		String input1 = "aaa.bbb.ccc.DDD";
		RegExUtils.matches(PATTERN_FQCN, input1, forEachGroup,
			// Success then:
			(matcher) -> {
				System.out.println("[INFO] 匹配成功!");
				System.out.println("group(1): " + doubleQuoteString(matcher.group(1)));
				System.out.println("group(2): " + doubleQuoteString(matcher.group(2)));
				System.out.println("---------------------------------------------------");
				System.out.println("packageName: " + doubleQuoteString(matcher.group("packageName")));
				System.out.println("className  : " + doubleQuoteString(matcher.group("className")));
				System.out.println();
			}
			// failed then:
			,(matcher) -> {
				System.out.println("[WARN] 匹配失敗" + "\n");	
			}
		);
		
		String input2 = "[[Ljava.util.stream.IntStream;@abc123";
		RegExUtils.matches(PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION, input2, forEachGroup,
			// Success then
			(matcher) -> {
				System.out.println("[INFO] 匹配成功!");
				System.out.println("group(1): " + matcher.group(1));
				System.out.println("group(2): " + matcher.group(2));
				System.out.println("group(3): " + matcher.group(3));
				System.out.println("group(7): " + matcher.group(7));
				System.out.println();
			},
			// failed then
			(matcher) -> {
				System.out.println("[WARN] 匹配失敗: " + input2 + "\n");	
			}
		);
	}
}

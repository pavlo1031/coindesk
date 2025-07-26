package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.TypeSignatureExpression.*;
import static cathay.coindeskApi.commons.util.ArrayUtils.findFirstMatch;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import lombok.Data;

@Data
public class CastClassPair {
	
	public final Class<?> fromClass;
	
	public final Class<?> toClass;
	
	private Object value;
	
	public static CastClassPair of(ClassCastException e) {
		Matcher successMatches = findFirstMatch(
			// check
			(pattern) -> compile(pattern).matcher(e.getMessage()).matches(),
			// successThen:
			(pattern) -> {
				Matcher m = compile(pattern).matcher(e.getMessage());
				m.matches();
				return m;
			},
			
			/* 依序比對: 此二patterns (不同轉型方式, 可能有多種錯誤訊息) */
			PATTERN_STRING_CLASS_CAST_EXCEPTION_MESSAGE,
			PATTERN_STRING_CANNOT_CAST_EXCEPTION_MESSAGE)
		
		// 均無匹配, 拋錯
		.orElseThrow(() -> {
			return new RuntimeException("無法解析出source, dest型別", e);
		});

		// 抽取出錯誤訊息中的: 來源型別, 和目標型別
		final TypeSignatureExpression typeExpr1 = TypeSignatureExpression.of(successMatches.group("src"));
		final TypeSignatureExpression typeExpr2 = TypeSignatureExpression.of(successMatches.group("dest"));
		return new CastClassPair(typeExpr1.getType(), typeExpr2.getType());
	}
	
	public CastClassPair(Class<?> fromClass, Class<?> toClass) {
		this.fromClass = fromClass;
		this.toClass = toClass;
	}
}

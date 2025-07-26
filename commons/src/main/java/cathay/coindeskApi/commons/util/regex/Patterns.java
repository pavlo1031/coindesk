package cathay.coindeskApi.commons.util.regex;

/**
 * 常用的pattern字串
 */
public class Patterns {

	// minimal unit
	private final static String PATTERN_FQCN_SEGMENT = "[a-zA-Z_][a-zA-Z0-9_]*";
	
	private final static String PATTERN_FQCN_SEGMENT_PackageName = "(?<packageName>(?:%s\\.)*)?".replace("%s", PATTERN_FQCN_SEGMENT);
	
	private final static String PATTERN_FQCN_SEGMENT_ClassName = String.format("(?<className>%s)", PATTERN_FQCN_SEGMENT);
	
	// ↓ PackageName                    ↓ ClassName
	// ((?:[a-zA-Z_][a-zA-Z0-9_]*\\.)*)?([a-zA-Z_][a-zA-Z0-9_]*)
	public final static String PATTERN_FQCN = String.format("(?:%s%s)", PATTERN_FQCN_SEGMENT_PackageName, PATTERN_FQCN_SEGMENT_ClassName);

	public final static String PATTERN_FQCN_INTERNAL = String.format("L?(%s)", PATTERN_FQCN.replace(".", "/"));
}

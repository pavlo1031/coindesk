package cathay.coindeskApi.commons.util.regex;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;

/**
 * matcher
 * group
 */
public interface RegexBiConsumer extends RegexConsumer, BiConsumer<Matcher, String> {

}

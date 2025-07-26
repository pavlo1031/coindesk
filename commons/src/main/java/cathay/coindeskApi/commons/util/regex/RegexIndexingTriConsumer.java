package cathay.coindeskApi.commons.util.regex;

import java.util.regex.Matcher;

import cathay.coindeskApi.commons.util.function.TriConsumer;

/**
 * matcher
 * group
 * index
 */
public interface RegexIndexingTriConsumer extends TriConsumer<Matcher, String, Long> {

}

package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface NoArgPredicate extends Predicate<Object> {

	boolean test();
	
	default boolean test(Object o) {
		return test();
	}
	 
	default Predicate<?> and(NoArgPredicate other) {
        Objects.requireNonNull(other);
        return (t) -> test() && other.test();
    }

    default NoArgPredicate negate() {
        return () -> !test();
    }
}

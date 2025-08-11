package cathay.coindeskApi.commons.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U> extends Predicate<T>, java.util.function.BiPredicate<T, U> {
    
    boolean test(T t, U u);
    
    default boolean test(T t) {
    	return test(t, null);
    }

	@Override
	default BiPredicate<T,U> negate() {
		return (T t, U u) -> !test(t, u);
	}
	
	@Override
	default BiPredicate<T, U> or(java.util.function.Predicate<? super T> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
        return (t, u) -> test(t, u) || other.test(t);
	}
	
	@Override
	default BiPredicate<T,U> and(java.util.function.Predicate<? super T> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
        return (T t, U u) -> test(t, u) && other.test(t);
	}

	default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
        return (T t, U u) -> test(t, u) || other.test(t, u);
	}
	
	default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
        return (T t, U u) -> test(t, u) && other.test(t, u);
	}
}
package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface BiFunction<T, U, R> extends Function<T, R>, java.util.function.BiFunction<T, U, R> {

	R apply(T t, U u);
	
	default R apply(T t) {
		return apply(t, null);
	}

	@Override
	default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after, "The argument 'after' cannot be null");
        return (T t, U u) -> {
        	R r = apply(t);
        	return after.apply(r); 
        };
	}
}
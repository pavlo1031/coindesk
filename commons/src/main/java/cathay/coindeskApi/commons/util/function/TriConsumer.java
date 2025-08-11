package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 3個參數的Consumer
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> extends BiConsumer<T, U> {

	void accept(T t, U u, V v);
	
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 3)
			throw new IllegalArgumentException("TriConsumer accept(Object[]) argument count must be >= 3, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null);
	}
	
    default TriConsumer<T, U, V> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> { accept(t, u, v); after.accept(t); };
    }
    
    default TriConsumer<T, U, V> andThen(java.util.function.BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> {
            accept(t, u, v);
            after.accept(t, u);
        };
    }
    
    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> {
            accept(t, u, v);
            after.accept(t, u, v);
        };
    }
}
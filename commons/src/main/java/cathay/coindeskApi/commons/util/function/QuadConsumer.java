package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 4個參數的Consumer
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> extends TriConsumer<T, U, V> {

	void accept(T t, U u, V v, W w);
	
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 4)
			throw new IllegalArgumentException("QuadConsumer accept(Object[]) argument count must be >= 4, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2], (W) args[3]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v) {
		this.accept(t, u, v, null);
	}
	
    default QuadConsumer<T, U, V, W> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> {
        	accept(t, u, v, w);
        	after.accept(t);
        };
    }
    
    default QuadConsumer<T, U, V, W> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> {
        	accept(t, u, v, w);
        	after.accept(t, u);
        };
    }
    
    default QuadConsumer<T, U, V, W> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> {
            accept(t, u, v, w);
            after.accept(t, u, v);
        };
    }
    
    default QuadConsumer<T, U, V, W> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> {
            accept(t, u, v, w);
            after.accept(t, u, v, w);
        };
    }
}
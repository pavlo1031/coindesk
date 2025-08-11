package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 5個參數的Consumer
 */
@FunctionalInterface
public interface PentaConsumer<T, U, V, W, X> extends QuadConsumer<T, U, V, W> {

	void accept(T t, U u, V v, W w, X x);

	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 5)
			throw new IllegalArgumentException("PentaConsumer accept(Object[]) argument count must be >= 5, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2], (W) args[3], (X) args[4]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null, null, null);
	}

	@Override
	default void accept(T t, U u, V v) {
		this.accept(t, u, v, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w) {
		this.accept(t, u, v, w, null);
	}
	
    default PentaConsumer<T, U, V, W, X> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x) -> {
        	accept(t, u, v, w, x);
        	after.accept(t);
        };
    }
    
    default PentaConsumer<T, U, V, W, X> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x) -> {
        	accept(t, u, v, w, x);
        	after.accept(t, u);
        };
    }
    
    default PentaConsumer<T, U, V, W, X> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x) -> {
            accept(t, u, v, w, x);
            after.accept(t, u, v);
        };
    }
    
    default PentaConsumer<T, U, V, W, X> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x) -> {
            accept(t, u, v, w, x);
            after.accept(t, u, v, w);
        };
    }
    
    default PentaConsumer<T, U, V, W, X> andThen(PentaConsumer<? super T, ? super U, ? super V, ? super W, ? super X> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x) -> {
            accept(t, u, v, w, x);
            after.accept(t, u, v, w, x);
        };
    }
}
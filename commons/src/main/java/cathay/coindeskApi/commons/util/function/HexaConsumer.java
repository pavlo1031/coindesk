package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 6個參數的Consumer
 */
@FunctionalInterface
public interface HexaConsumer<T, U, V, W, X, Y> extends PentaConsumer<T, U, V, W, X> {

	void accept(T t, U u, V v, W w, X x, Y y);
	
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 6)
			throw new IllegalArgumentException("HexaConsumer accept(Object[]) argument count must be >= 6, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2], (W) args[3], (X) args[4], (Y) args[5]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v) {
		this.accept(t, u, v, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w) {
		this.accept(t, u, v, w, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x) {
		this.accept(t, u, v, w, x, null);
	}
	
    default HexaConsumer<T, U, V, W, X, Y> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
        	accept(t, u, v, w, x, y);
        	after.accept(t);
        };
    }
    
    default HexaConsumer<T, U, V, W, X, Y> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
        	accept(t, u, v, w, x, y);
        	after.accept(t, u);
        };
    }
    
    default HexaConsumer<T, U, V, W, X, Y> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
            accept(t, u, v, w, x, y);
            after.accept(t, u, v);
        };
    }
    
    default HexaConsumer<T, U, V, W, X, Y> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
            accept(t, u, v, w, x, y);
            after.accept(t, u, v, w);
        };
    }
    
    default HexaConsumer<T, U, V, W, X, Y> andThen(PentaConsumer<? super T, ? super U, ? super V, ? super W, ? super X> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
            accept(t, u, v, w, x, y);
            after.accept(t, u, v, w, x);
        };
    }
    
    default HexaConsumer<T, U, V, W, X, Y> andThen(HexaConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y) -> {
            accept(t, u, v, w, x, y);
            after.accept(t, u, v, w, x, y);
        };
    }
}
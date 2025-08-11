package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 7個參數的Consumer
 */
@FunctionalInterface
public interface HeptaConsumer<T, U, V, W, X, Y, Z> extends HexaConsumer<T, U, V, W, X, Y> {

	void accept(T t, U u, V v, W w, X x, Y y, Z z);
	
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 7)
			throw new IllegalArgumentException("HeptaConsumer accept(Object[]) argument count must be >= 7, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2], (W) args[3], (X) args[4], (Y) args[5], (Z) args[6]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v) {
		this.accept(t, u, v, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w) {
		this.accept(t, u, v, w, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x) {
		this.accept(t, u, v, w, x, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x, Y y) {
		this.accept(t, u, v, w, x, y, null);
	}
	
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
        	accept(t, u, v, w, x, y, z);
        	after.accept(t);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
        	accept(t, u, v, w, x, y, z);
        	after.accept(t, u);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
            accept(t, u, v, w, x, y, z);
            after.accept(t, u, v);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
            accept(t, u, v, w, x, y, z);
            after.accept(t, u, v, w);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(PentaConsumer<? super T, ? super U, ? super V, ? super W, ? super X> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
            accept(t, u, v, w, x, y, z);
            after.accept(t, u, v, w, x);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(HexaConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
            accept(t, u, v, w, x, y, z);
            after.accept(t, u, v, w, x, y);
        };
    }
    
    default HeptaConsumer<T, U, V, W, X, Y, Z> andThen(HeptaConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y, ? super Z> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z) -> {
            accept(t, u, v, w, x, y, z);
            after.accept(t, u, v, w, x, y, z);
        };
    }
}
package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 8個參數的Consumer
 */
@FunctionalInterface
public interface OctoConsumer<T, U, V, W, X, Y, Z, A> extends HeptaConsumer<T, U, V, W, X, Y, Z> {

	void accept(T t, U u, V v, W w, X x, Y y, Z z, A a);
	
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 8)
			throw new IllegalArgumentException("OctoConsumer accept(Object[]) argument count must be >= 8, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1], (V) args[2], (W) args[3], (X) args[4], (Y) args[5], (Z) args[6], (A) args[7]);
	}
	
	@Override
	default void accept(T t) {
		this.accept(t, null, null, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u) {
		this.accept(t, u, null, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v) {
		this.accept(t, u, v, null, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w) {
		this.accept(t, u, v, w, null, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x) {
		this.accept(t, u, v, w, x, null, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x, Y y) {
		this.accept(t, u, v, w, x, y, null, null);
	}
	
	@Override
	default void accept(T t, U u, V v, W w, X x, Y y, Z z) {
		this.accept(t, u, v, w, x, y, z, null);
	}
	
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
        	accept(t, u, v, w, x, y, z, a);
        	after.accept(t);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
        	accept(t, u, v, w, x, y, z, a);
        	after.accept(t, u);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v, w);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(PentaConsumer<? super T, ? super U, ? super V, ? super W, ? super X> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v, w, x);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(HexaConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v, w, x, y);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(HeptaConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y, ? super Z> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v, w, x, y, z);
        };
    }
    
    default OctoConsumer<T, U, V, W, X, Y, Z, A> andThen(OctoConsumer<? super T, ? super U, ? super V, ? super W, ? super X, ? super Y, ? super Z, ? super A> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w, x, y, z, a) -> {
            accept(t, u, v, w, x, y, z, a);
            after.accept(t, u, v, w, x, y, z);
        };
    }
}
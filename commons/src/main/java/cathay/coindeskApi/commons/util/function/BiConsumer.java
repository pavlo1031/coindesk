package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 2個參數的Consumer
 */
@FunctionalInterface
public interface BiConsumer<T, U> extends Consumer<T>, java.util.function.BiConsumer<T, U> {

	void accept(T t, U u);
    
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 2)
			throw new IllegalArgumentException("BiConsumer accept(Object[]) argument count must be >= to 2, but actual length is " + args.length);
		this.accept((T) args[0], (U) args[1]);
	}
	
	default void accept(T t) {
		accept(t, null);
	}
	
	@Override
    default BiConsumer<T, U> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> { accept(t, u); after.accept(t); };
    }
    
    default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
    
    @Override
    default BiConsumer<T, U> andThen(java.util.function.BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
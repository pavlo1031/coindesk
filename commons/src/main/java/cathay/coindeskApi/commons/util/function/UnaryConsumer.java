package cathay.coindeskApi.commons.util.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 1個參數的Consumer (更明確的)
 */
@FunctionalInterface
public interface UnaryConsumer<T> extends java.util.function.Consumer<T> {

	void accept(T t);
    
	default void accept(Object... args) {
		Objects.requireNonNull(args, "The argument 'args' cannot be null");
		if (args.length >= 1)
			throw new IllegalArgumentException("UnaryConsumer accept(Object[]) argument count must be >= 1, but actual length is " + args.length);
		this.accept((T) args[0]);
	}
	
	@Override
    default UnaryConsumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
    
    default UnaryConsumer<T> andThen(UnaryConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
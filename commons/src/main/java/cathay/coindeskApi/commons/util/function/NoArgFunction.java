package cathay.coindeskApi.commons.util.function;

import java.util.function.Function;

/**
 * 仿照java的Function
 * (尚未定義 參數為NoArgFunction 的 andThen, compose)
 */
@FunctionalInterface
public interface NoArgFunction<R> extends Function<Object, R> {

	R apply();
	
	default R apply(Object v) {
		return this.apply();
	}
    
    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
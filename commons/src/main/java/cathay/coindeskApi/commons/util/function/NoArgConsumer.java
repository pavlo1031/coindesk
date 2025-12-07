package cathay.coindeskApi.commons.util.function;

import java.util.function.Consumer;

/**
 * 仿照java的Consumer
 * (尚未定義 參數為NoArgConsumer 的 andThen)
 */
@FunctionalInterface
public interface NoArgConsumer extends Consumer<Object> {

    void accept();
    
	default void accept(Object v) {
		this.accept();
	}
}
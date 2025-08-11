package cathay.coindeskApi.commons.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T> {
	
	default Predicate<T> and(java.util.function.Predicate<? super T> other) {
		return (T t) -> java.util.function.Predicate.super.and(other).test(t);
	}
	
	default Predicate<T> or(java.util.function.Predicate<? super T> other) {
		return (T t) -> java.util.function.Predicate.super.or(other).test(t);
	}
	
	/**
	 * 前提滿足時，才會判斷then條件
	 * 
	 * @param then 主要的待檢核的條件 (可是是在前提滿足時才檢查)
	 */
	default Predicate<T> then(java.util.function.Predicate<? super T> then) {
		Objects.requireNonNull(then, "The argument 'then' cannot be null");
        return (T t) -> {
        	if (!this.test(t))
        		return false;
        	return then.test(t);
        };
	}
	
	/**
	 * 前提「不」滿足時，才會判斷orElse條件
	 * 
	 * @param orElse 主要的待檢核的條件 (可是是在前提「不」滿足時才檢查)
	 */
	default Predicate<T> orElse(java.util.function.Predicate<? super T> orElse) {
		Objects.requireNonNull(orElse, "The argument 'orElse' cannot be null");
        return (T t) -> {
        	if (this.test(t))
        		return true;
        	return orElse.test(t);
        };
	}
}
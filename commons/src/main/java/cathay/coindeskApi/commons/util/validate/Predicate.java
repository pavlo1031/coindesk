package cathay.coindeskApi.commons.util.validate;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> extends cathay.coindeskApi.commons.util.function.Predicate<T> {

	@Override
	default Predicate<T> and(java.util.function.Predicate<? super T> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
        return (t) -> {
        	Boolean pass = false;
        	try {
        		pass = this.test(t);
            	if (!pass)
            		throw new ShortCircuitException(false);
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("and() 前提條件檢核失敗, 而拋出例外", false, e);
        	}
        	return other.test(t);
        };
	}

	@Override
	default Predicate<T> or(java.util.function.Predicate<? super T> other) {
		Objects.requireNonNull(other, "The argument 'other' cannot be null");
		return (t) -> {
			Boolean pass = false;
        	try {
        		pass = this.test(t);
            	if (pass)
            		throw new ShortCircuitException("or() 前提條件檢核成功, 提早離開", true);	
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("and() 前提條件檢核失敗, 而拋出例外", false, e);
        	}
        	return other.test(t);
        };
	}

	/**
	 * 前提滿足時，才會判斷then條件
	 * 
	 * @param then 主要的待檢核的條件 (可是是在前提滿足時才檢查)
	 */
	default Predicate<T> then(java.util.function.Predicate<? super T> then) {
		Objects.requireNonNull(then, "The argument 'then' cannot be null");
        return (T t) -> {
        	// 前提條件
        	Boolean pass;
        	try {
        		pass = this.test(t);
        		if (!pass)
        			// 結果判定為true, 意即直接結束, 不再判斷後續
            		throw new ShortCircuitException("then(): 在「前提條件」判斷為false ➜ 不再判斷then, 將結果設定為true, 然後結束", true);
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("then(): 執行「前提條件」判斷失敗, 拋出錯誤 ➜ 不再判斷then, 將結果設定為true, 然後結束", true, e);	
        	}
        	
        	// 主要條件then
        	try {
        		pass = then.test(t);
        		return pass;
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("then(): 執行then條件時拋出錯誤", false, e);	
        	}
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
        	// 前提條件
        	Boolean pass = null;
        	try {
        		pass = this.test(t);
        		if (pass)
        			// 結果判定為true, 意即直接結束, 不再判斷後續
            		throw new ShortCircuitException("orElse(): 在「前提條件」判斷為true ➜ 不再判斷orElse條件, 將結果設定為true, 然後結束", true);
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("orElse():  執行「前提條件」判斷失敗, 拋出錯誤 ➜ 不再判斷orElse條件, 將結果設定為true, 然後結束", true, e);	
        	}

        	// 主要條件orElse
        	try {
        		pass = orElse.test(t);
        		return pass;
        	}
        	catch (Exception e) {
        		throw new ShortCircuitException("orElse(): 執行orElse條件時拋出錯誤", false, e);	
        	}
        };
	}
}
package cathay.coindeskApi.commons.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class ValidatUtils {
	/**
	 * @param elements 待檢核的資料
	 */
	public static <T> void validateData(T data, Function<T, Boolean> validator, Consumer<T> failAndThen) {
    	if (validator == null)
    		throw new IllegalArgumentException("檢核方法validator不可為空");
    	
    	Boolean success = validator.apply(data);
		if (!success) {
			if (failAndThen != null)
				failAndThen.accept(data);
		}
	}
	
	/**
	 * @param elements 待檢核的資料集合
	 */
    public static <T> void validateData(T[] data, Function<T, Boolean> validator, Consumer<T> failAndThen) {
    	if (validator == null)
    		throw new IllegalArgumentException("檢核方法validator不可為空");
    	
    	for (T element : data) {
    		Boolean success = validator.apply(element);
    		if (!success) {
    			if (failAndThen != null)
    				failAndThen.accept(element);
    		}
    	}
    }
}

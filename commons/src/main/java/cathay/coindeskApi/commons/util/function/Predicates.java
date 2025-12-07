package cathay.coindeskApi.commons.util.function;

import static java.util.Objects.requireNonNull;


/**
 * 用多個predicate條件運算, 組合出不同邏輯的各個predicate
 */
public class Predicates {

	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	///////////////////////////////////////////////////////////////////

    public static <T> Predicate<T> and(java.util.function.Predicate<?>... predicates) {
        requireNonNull(predicates);
    	return (t) -> {
    		if (predicates.length == 0)
    			return false;
    		
    		for (int i=0; i<predicates.length; i++) {
    			java.util.function.Predicate p = predicates[i];
    			if (!p.test(t))
    				return false;
    		}
    		return true;
    	};
    }

    public static <T> Predicate<T> or(java.util.function.Predicate<? super T>... predicates) {
        requireNonNull(predicates);
    	return (t) -> {
    		if (predicates.length == 0)
    			return false;
    		
    		for (int i=0; i<predicates.length; i++) {
    			java.util.function.Predicate p = predicates[i];
    			if (p.test(t))
    				return true;
    		}
    		return false;
    	};
    }
}
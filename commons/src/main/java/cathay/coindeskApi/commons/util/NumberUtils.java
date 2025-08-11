package cathay.coindeskApi.commons.util;

public class NumberUtils {
	
	public static <T extends Number, U extends Number, R extends Number> R add(T n1, U n2) {
		if (n1 == null || n2 == null)
            throw new IllegalArgumentException("Numbers cannot be null");

        // 根據型別提升規則選擇精度最高的運算
        if (n1 instanceof Double || n2 instanceof Double) {
            Double result = n1.doubleValue() + n2.doubleValue();
            return (R) result;
        } else if (n1 instanceof Float || n2 instanceof Float) {
            Float result = n1.floatValue() + n2.floatValue();
            return (R) result;
        } else if (n1 instanceof Long || n2 instanceof Long) {
            Long result = n1.longValue() + n2.longValue();
            return (R) result;
        } else {
            Integer result = n1.intValue() + n2.intValue();
            return (R) result;
        }
	}
	
	public static <T extends Number, U extends Number, R extends Number> R substract(T n1, U n2) {
		if (n1 == null || n2 == null)
            throw new IllegalArgumentException("Numbers cannot be null");

        // 根據型別提升規則選擇精度最高的運算
        if (n1 instanceof Double || n2 instanceof Double) {
            Double result = n1.doubleValue() - n2.doubleValue();
            return (R) result;
        } else if (n1 instanceof Float || n2 instanceof Float) {
            Float result = n1.floatValue() - n2.floatValue();
            return (R) result;
        } else if (n1 instanceof Long || n2 instanceof Long) {
            Long result = n1.longValue() - n2.longValue();
            return (R) result;
        } else {
            Integer result = n1.intValue() - n2.intValue();
            return (R) result;
        }
	}
	
	// multiply
	public static <T extends Number, U extends Number, R extends Number> R multiply(T n1, U n2) {
		if (n1 == null || n2 == null)
            throw new IllegalArgumentException("Numbers cannot be null");

        // 根據型別提升規則選擇精度最高的運算
        if (n1 instanceof Double || n2 instanceof Double) {
            Double result = n1.doubleValue() * n2.doubleValue();
            return (R) result;
        } else if (n1 instanceof Float || n2 instanceof Float) {
            Float result = n1.floatValue() * n2.floatValue();
            return (R) result;
        } else if (n1 instanceof Long || n2 instanceof Long) {
            Long result = n1.longValue() * n2.longValue();
            return (R) result;
        } else {
            Integer result = n1.intValue() * n2.intValue();
            return (R) result;
        }
	}
	
	// divide
	public static <T extends Number, U extends Number, R extends Number> R divide(T dividend, U divisor) {
		if (dividend == null || divisor == null)
            throw new IllegalArgumentException("Numbers cannot be null");

        // 根據型別提升規則選擇精度最高的運算
        if (dividend instanceof Double || divisor instanceof Double) {
            Double result = dividend.doubleValue() / divisor.doubleValue();
            return (R) result;
        } else if (dividend instanceof Float || divisor instanceof Float) {
            Float result = dividend.floatValue() / divisor.floatValue();
            return (R) result;
        } else if (dividend instanceof Long || divisor instanceof Long) {
            Long result = dividend.longValue() / divisor.longValue();
            return (R) result;
        } else {
            Integer result = dividend.intValue() / divisor.intValue();
            return (R) result;
        }
	}
	
	// pow
	public static <T extends Number, U extends Number, R extends Number> R pow(T base, U exponent) {
		if (base == null || exponent == null)
            throw new IllegalArgumentException("Numbers cannot be null");

		Double result = Math.pow(base.doubleValue(), exponent.doubleValue());
		return (R) result ;
	}
	
	// modulus
	public static <T extends Number, R extends Number> R modulus(Integer dividend, Integer divisor) {
		if (dividend == null || divisor == null)
            throw new IllegalArgumentException("Numbers cannot be null");

		Integer result = dividend.intValue() % divisor.intValue();
        return (R) result;
	}

	// modulus
	public static <T extends Number, R extends Number> R modulus(Long dividend, Integer divisor) {
		if (dividend == null || divisor == null)
            throw new IllegalArgumentException("Numbers cannot be null");

		Long result = dividend.longValue() % divisor.intValue();
        return (R) result;
	} 
}

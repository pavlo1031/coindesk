package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.*;
import cathay.coindeskApi.commons.util.types.CastClassPair;

public class ArrayUtilsExample {

	public static void main(String[] args) {
		try {
			Object result = firstNonNull(new Integer[] {null, null, 5, null}, (x, index) -> "[" + index + "]: " + x);
			System.out.println("--> 1st NonNull value: " + result);
		}
		catch (ClassCastException e) {
			CastClassPair castClassInfo = CastClassPair.of(e);
			System.out.println("from: " + castClassInfo.getFromClass());
			System.out.println("  to: " + castClassInfo.getToClass());
		}
		
		Object result = findAnyMatch((x) -> x != null, (arr) -> "123", 11, 22, 33, 55);
		System.out.println("found: " + result);
	}
}

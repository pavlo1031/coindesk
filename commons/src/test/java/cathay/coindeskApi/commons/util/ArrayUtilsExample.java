package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.*;

public class ArrayUtilsExample {

	public static void main(String[] args) {
		try {
			String elem = firstNonNull(new Integer[] {null, null, 123, 456});
			System.out.println("--> 1st NonNull value: " + elem);
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

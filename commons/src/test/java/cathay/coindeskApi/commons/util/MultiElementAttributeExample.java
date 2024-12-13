package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.MultiElementAttribute.*;

public class MultiElementAttributeExample {

	public static void main(String[] args) {
		of(new int[0]).isArray((int[] o) -> {			
			System.out.println("this is an array!");
		});
	}
}

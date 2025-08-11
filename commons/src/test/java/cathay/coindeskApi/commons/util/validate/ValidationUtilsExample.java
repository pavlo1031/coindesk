package cathay.coindeskApi.commons.util.validate;

import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.*;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.*;

public class ValidationUtilsExample {
	
	public static void main(String[] args) {
		final int[] paramArray = new int[] {1, 2, 3, 4, 5};
		checkCondition(paramArray, sizeEquals(5), "長度不符: 長度必須為" + 5);
		
		final int toFindElem = 5;
		// FIXED:                                                         ↓不需宣告型別           ↓可直接對回傳值呼叫method
		Object result = checkCondition(toFindElem, existsIn(paramArray), (x) -> "未找到元素" + x).length();
		System.out.println("result: " + result);
	}
}

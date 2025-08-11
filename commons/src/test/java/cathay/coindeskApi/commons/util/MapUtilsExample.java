package cathay.coindeskApi.commons.util;

public class MapUtilsExample {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		java.util.LinkedHashMap<String, Object> map1 = new java.util.LinkedHashMap<String, Object>();
		map1.put("aa", 123);
		map1.put("bb", 456);
		map1.put("cc", 789);
		map1.put("dd", 1122);
		System.out.println("original: " + map1 + "\n");
		
		java.util.function.BiConsumer<String, Object> op = null;
		op = map1::put;
		op.accept("ee", 888);
		System.out.println("after run operator: " + map1);
		
		ProfileUtils.executionTime("在特定位置插入{K,V}", () -> {
			MapUtils.put(map1, 3, "xx", 2233);
			System.out.println("after putting an entry on specific position: " + map1);
		});
	}
}

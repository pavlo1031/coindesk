package cathay.coindeskApi.commons.enums;

public enum Order {
	
	Original(0),
	Ascending(1), Descending(-1);
	
	private Integer value;
	
	private Order(int value) {
		this.value = value;
	}
	
}

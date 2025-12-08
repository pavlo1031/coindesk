package cathay.coindeskApi.commons.util;

public class Flag {
	
	private boolean value = false;
	
	public static Flag create() {
		return new Flag();
	}
	
	public static Flag create(boolean value) {
		Flag flag = new Flag();
		flag.value = value;
		return flag;
	}
	
	public Flag setFlag(boolean value) {
		this.value = value;
		return this;
	}
	
	public Flag setFlag() {
		this.value = true;
		return this;
	}

	public Flag unsetFlag() {
		this.value = false;
		return this;
	}

	/**
	 * 取反向的值, 但不改變flag
	 */
	public boolean negate() {
		return !this.value;
	}
	
	public boolean isunSet() {
		return !value;
	}
	
	public boolean isSet() {
		return value;
	}
	
	public boolean value() {
		return value;
	}
}

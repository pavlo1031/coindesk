package cathay.coindeskApi.commons.util;

public class Flag {
	
	private boolean flag;
	
	public Flag() {
		
	}
	
	public Flag(boolean flag) {
		this.flag = flag;
	}
	
	public void setFlag() {
		this.flag = true;
	}
	
	public void unsetFlag() {
		this.flag = false;
	}
	
	public boolean isFlagSet() {
		return flag;
	}
}

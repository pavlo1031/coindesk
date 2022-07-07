package cathay.coindeskApi.api.vo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CoinResponse {
	@JsonProperty("time")
	private Time time = new Time();
	
	@JsonProperty("disclaimer")
	private String disclaimer;
	
	@JsonProperty("chartName")
	private String chartName;
	
	@JsonProperty("bpi")
	@Setter(AccessLevel.NONE)
	private HashMap<String, Coin> bpi = new HashMap<String, Coin>();
	
	public CoinResponse addBpi(String name, Coin coin) {
		bpi.put(name, coin);
		return this;
	}
	
	public CoinResponse removeBpi(String name) {
		if (this.bpi.containsKey(name))
			this.bpi.remove(name);
		return this;
	}
}

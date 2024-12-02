package cathay.coindeskApi.api.vo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class ListCoinTypeResponse extends CoinTypeResponse {
	
	@JsonProperty("disclaimer")
	private String disclaimer;
	
	@JsonProperty("chartName")
	private String chartName;
	
	@JsonProperty("bpi")
	private Map<String, Coin> bpi = new HashMap<String, Coin>();
	
	/**
	 * @param name 幣別名稱
	 * @param coin 幣別資料
	 */
	public ListCoinTypeResponse addBpi(String name, Coin coin) {
		bpi.put(name, coin);
		return this;
	}
	
	public ListCoinTypeResponse removeBpi(String name) {
		if (this.bpi.containsKey(name))
			this.bpi.remove(name);
		return this;
	}
}

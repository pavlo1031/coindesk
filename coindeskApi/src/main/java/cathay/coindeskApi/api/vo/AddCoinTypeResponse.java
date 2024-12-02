package cathay.coindeskApi.api.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class AddCoinTypeResponse extends CoinTypeResponse {
	
	@JsonProperty("bpi")
	private Map<String, Coin> bpi = new HashMap<String, Coin>();
	
	@JsonProperty("rows_affected")
	private Number rowsAffected;
	
	public AddCoinTypeResponse addBpi(Coin... coins) {
		this.addBpi(Arrays.asList(coins));
		return this;
	}
	
	public AddCoinTypeResponse addBpi(List<Coin> coins) {
		for (Coin c : coins) {
			bpi.put(c.getCoinCode(), c);
		}
		return this;
	}
}

package cathay.coindeskApi.api.vo;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class UpdateCoinTypeResponse extends CoinTypeResponse {

	@JsonProperty("updated")
	private Map<String, Coin> updated = new HashMap<String, Coin>();
	
	@JsonProperty("rows_affected")
	private Number rowsAffected;
	
	public UpdateCoinTypeResponse addBpi(Coin... coins) {
		return addBpi(Arrays.asList(coins));
	}
	
	public UpdateCoinTypeResponse addBpi(Collection<Coin> coins) {
		if (coins == null || coins.size() == 0)
			return this;
		
		if (this.updated == null)
			this.updated = new HashMap<String, Coin>();
		
		for (Coin c : coins) {
			updated.put(c.getCoinCode(), c);
		}
		return this;
	}
}

package cathay.coindeskApi.api.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCoinTypeRequest extends CoinTypeRequest {
	
	@JsonProperty("coinTypes")
	private List<Coin> coins = new ArrayList<Coin>();
	
	@JsonProperty("returningUpdated")
	private Boolean returningUpdated;
	
	public Boolean isReturningUpdated() {
		if (this.returningUpdated == null)
			return false;
		return this.returningUpdated;
	}
	
	public Boolean isReturningUpdated(boolean defaultValue) {
		if (this.returningUpdated == null)
			return defaultValue;
		return this.returningUpdated;
	}
}

package cathay.coindeskApi.api.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 新增幣別的reqeust資料
 */
@Data
@Accessors(chain = true)
public class AddCoinTypeRequest extends CoinTypeRequest {
	/**
	 * 待更新的幣別資料
	 */
	@JsonProperty("coinTypes")
	private List<Coin> coins = new ArrayList<Coin>();
	
	@JsonProperty("returningAdded")
	private Boolean returningAdded;

	public Boolean isReturningAdded() {
		if (this.returningAdded == null)
			return false;
		return this.returningAdded;
	}

	public Boolean isReturningAdded(boolean defaultValue) {
		if (this.returningAdded == null)
			return defaultValue;
		return this.returningAdded;
	}
}

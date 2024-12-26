package cathay.coindeskApi.api.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteCoinTypeRequest extends CoinTypeRequest {
	
	private List<String> coinCodes = new ArrayList<String>();
	
	@JsonProperty("returningDeleted")
	private Boolean returningDeleted;
	
	public Boolean isReturningDeleted() {
		if (this.returningDeleted == null)
			return false;
		return this.returningDeleted;
	}
	
	public Boolean isReturningDeleted(boolean defaultValue) {
		if (this.returningDeleted == null)
			return defaultValue;
		return this.returningDeleted;
	}
}

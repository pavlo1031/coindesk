package cathay.coindeskApi.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteCoinTypeRequest extends CoinTypeRequest {
	
	private List<String> coinCodes = new ArrayList<String>();
	
	@JsonProperty("returningDeleted")
	@Value("${cathay.interview.coindeskApi.api.returning_result_affected:false}")
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

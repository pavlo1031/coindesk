package cathay.coindeskApi.api.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class DeleteCoinTypeResponse extends CoinTypeResponse {
	
	@JsonProperty
	private List<String> deleted;
	
	@JsonProperty("rows_affected")
	private Number rowsAffected;
	
	public DeleteCoinTypeResponse addCoinCode(String coinCode) {
		if (this.deleted == null)
			this.deleted = new ArrayList<String>();
		deleted.add(coinCode);
		return this;
	}
	
	public DeleteCoinTypeResponse addCoinCodes(String... codes) {
		return addCoinCodes(Arrays.asList(codes));
	}
	
	public DeleteCoinTypeResponse addCoinCodes(Collection<String> codes) {
		if (this.deleted == null)
			this.deleted = new ArrayList<String>();
		deleted.addAll(codes);
		return this;
	}
}

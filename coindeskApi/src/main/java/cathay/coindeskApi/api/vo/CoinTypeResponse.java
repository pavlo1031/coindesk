package cathay.coindeskApi.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class CoinTypeResponse {
	
	@JsonProperty("time")
	private Time time = new Time();
	
	@JsonProperty("data")
	private Object data;
	
	@JsonProperty("msg")
	private String msg;
}

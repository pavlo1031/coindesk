package cathay.coindeskApi.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Coin implements Serializable {

	@JsonProperty("code")
	private String code;
	
	@JsonProperty("symbol")
	private String symbol;
	
	@JsonProperty("rate")
	private String rate;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("description_chinese")
	private String descriptionChinese;
	
	@JsonProperty("rate_float")
	private BigDecimal rateFloat;
	
	public Coin(String code, String symbol, String rate, String description, Double rateFloat) {
		this.code = code;
		this.symbol = symbol;
		this.rate = rate;
		this.description = description;
		this.rateFloat = new BigDecimal(rateFloat);
	}
	
	public Coin setRateFloat(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		return this;
	}
	
	public Coin setRateFloat(Double rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat);
		return this;
	}

}

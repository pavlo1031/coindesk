package cathay.coindeskApi.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import cathay.coindeskApi.commons.util.CurrencyUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class Coin implements Serializable {
	
	@JsonProperty("code")
	private String coinCode;
	
	@JsonProperty("symbol")
	private String symbol;
	
	@Setter(AccessLevel.NONE)
	@JsonProperty("rate")
	private String rate;
	
	@JsonProperty("rate_float")
	private BigDecimal rateFloat;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("description_zh")
	private String descriptionChinese;

	
	public Coin() {
		
	}
	
	public Coin(String code) {
		this.coinCode = code;
	}
	
	public Coin(String code, String symbol, Number rateFloat) {
		this(code, symbol, rateFloat, null, null);
	}
	
	public Coin(String code, String symbol, Number rateFloat, String description) {
		this(code, symbol, rateFloat, description, null);
	}
	
	public Coin(String code, String symbol, Number rateFloat, String description, String descriptionCh) {
		this.coinCode = code;
		this.symbol = symbol;
		this.rateFloat = new BigDecimal(rateFloat.doubleValue());
		this.rate = CurrencyUtils.format(rateFloat);
		this.description = description;
		this.descriptionChinese = descriptionCh;
	}
	
	public Coin setRateFloat(Number rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat.doubleValue());
		this.rate = CurrencyUtils.format(rateFloat);
		return this;
	}

	public int hashCode() {
		return this.coinCode.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (Coin.class != o.getClass())
			return false;
		return this.coinCode.equals(((Coin) o).getCoinCode());
	}
}

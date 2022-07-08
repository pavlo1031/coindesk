package cathay.coindeskApi.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Coin implements Serializable {

	private static DecimalFormat currencyFormat;
	
	@Autowired
	@JsonIgnore
	private InjectionHelper injectHelper;
	
	@Component
	private static class InjectionHelper {
		@Autowired
		public void setCurrencyFormat(DecimalFormat currencyFormat) {
			Coin.currencyFormat = currencyFormat;
		}
	}
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("symbol")
	private String symbol;
	
	@JsonProperty("rate")
	@Setter(AccessLevel.NONE)
	private String rate;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("description_chinese")
	private String descriptionChinese;
	
	@JsonProperty("rate_float")
	private BigDecimal rateFloat;
	
	public Coin(String code, String symbol, Double rateFloat) {
		this(code, symbol, new BigDecimal(rateFloat), null, null);
	}
	
	public Coin(String code, String symbol, BigDecimal rateFloat) {
		this(code, symbol, rateFloat, null, null);
	}
	
	public Coin(String code, String symbol, Double rate, String description) {
		this(code, symbol, new BigDecimal(rate), description, null);
	}
	
	public Coin(String code, String symbol, BigDecimal rate, String description) {
		this(code, symbol, rate, description, null);
	}
	
	public Coin(String code, String symbol, BigDecimal rateFloat, String description, String descriptionCh) {
		this.code = code;
		this.symbol = symbol;
		this.rateFloat = rateFloat;
		this.rate = currencyFormat.format(rateFloat);
		this.description = description;
		this.descriptionChinese = descriptionCh;
	}
	
	public Coin setRateFloat(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		this.rate = currencyFormat.format(rateFloat);
		return this;
	}
	
	public Coin setRateFloat(Double rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat);
		this.rate = currencyFormat.format(rateFloat);
		return this;
	}

}

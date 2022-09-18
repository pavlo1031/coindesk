package cathay.coindeskApi.api.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class CoinRequest {
	
	private String code;
	
	private String symbol;
	
	private String description;
	
	private String description_chinese;
	
	private String rate;
	
	private BigDecimal rate_float;
	
	public <T extends CoinRequest> T setCoinCode(String coinCode) {
		this.code = coinCode;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setCode(String coinCode) {
		this.code = coinCode;
		return (T) this;
	}
	
	public String getCode() {
		return this.code;
	}

	@JsonIgnore
	public String getCoinCode() {
		return this.code;
	}

	public <T extends CoinRequest> T setSymbol(String symbol) {
		this.symbol = symbol;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setDescription(String description) {
		this.description = description;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setDescriptionChinese(String descriptionCh) {
		this.description_chinese = descriptionCh;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setDescription_chinese(String descriptionCh) {
		this.description_chinese = descriptionCh;
		return (T) this;
	}
	
	public String getDescription_chinese() {
		return this.description_chinese;
	}
	
	@JsonIgnore
	public String getDescriptionChinese() {
		return this.description;
	}
	
	public <T extends CoinRequest> T setRate_float(BigDecimal rateFloat) {
		this.rate_float = rateFloat;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setRateFloat(Double rateFloat) {
		this.rate_float = new BigDecimal(rateFloat);
		return (T) this;
	}

	public <T extends CoinRequest> T setRateFloat(Float rateFloat) {
		this.rate_float = new BigDecimal(rateFloat);
		return (T) this;
	}
	
	public BigDecimal getRate_float() {
		return this.rate_float;
	}

	@JsonIgnore
	public BigDecimal getRateFloat() {
		return rate_float;
	}
	
	public <T extends CoinRequest> T setRate(String rate) {
		this.rate = rate;
		return (T) this;
	}
}

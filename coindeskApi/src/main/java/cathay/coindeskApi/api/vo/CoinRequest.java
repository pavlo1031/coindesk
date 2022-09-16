package cathay.coindeskApi.api.vo;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class CoinRequest {
	
	private String coinCode;
	
	private String symbol;
	
	private String description;
	
	private String descriptionChinese;
	
	private String rate;
	
	private BigDecimal rateFloat;
	
	public <T extends CoinRequest> T setCoinCode(String coinCode) {
		this.coinCode = coinCode;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setCode(String coinCode) {
		this.coinCode = coinCode;
		return (T) this;
	}
	
	public String getCode() {
		return this.coinCode;
	}
	
	public <T extends CoinRequest> T setSymbol(String symbol) {
		this.symbol = symbol;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setDescriptionChinese(String descriptionCh) {
		this.descriptionChinese = descriptionCh;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setDescription_chinese(String descriptionCh) {
		this.descriptionChinese = descriptionCh;
		return (T) this;
	}
	
	public String getDescription_chinese() {
		return this.descriptionChinese;
	}
	
	public <T extends CoinRequest> T setRate_float(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		return (T) this;
	}
	
	public <T extends CoinRequest> T setRateFloat(Double rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat);
		return (T) this;
	}

	public <T extends CoinRequest> T setRateFloat(Float rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat);
		return (T) this;
	}
	
	public BigDecimal getRate_float() {
		return this.rateFloat;
	}
	
	public <T extends CoinRequest> T setRate(String rate) {
		this.rate = rate;
		return (T) this;
	}
}

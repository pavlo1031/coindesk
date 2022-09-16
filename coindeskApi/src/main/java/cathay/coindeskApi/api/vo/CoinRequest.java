package cathay.coindeskApi.api.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CoinRequest {
	
	private String coinCode;
	
	private String symbol;
	
	private String description;
	
	private String descriptionChinese;
	
	private String rate;
	
	private BigDecimal rateFloat;
	
	public CoinRequest setCode(String coinCode) {
		this.coinCode = coinCode;
		return this;
	}
	
	public String getCode() {
		return this.coinCode;
	}
	
	public CoinRequest setDescription_chinese(String descriptionCh) {
		this.descriptionChinese = descriptionCh;
		return this;
	}
	
	public String getDescription_chinese() {
		return this.descriptionChinese;
	}
	
	public CoinRequest setRate_float(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		return this;
	}
	
	public BigDecimal getRate_float() {
		return this.rateFloat;
	}
}

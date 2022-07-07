package cathay.coindeskApi.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "coin_type")
public class CoinType implements Serializable {
	@Id
	@PrimaryKeyJoinColumn
	@Column(name = "code")
	private String code;

	@Column(name = "symbol")
	private String symbol;

	@Column(name = "rate")
	private String rate;

	@Column(name = "description")
	private String description;

	@Column(name = "description_chinese")
	private String descriptionChinese;

	@Column(name = "rate_float")
	private BigDecimal rateFloat;

	@Column(name = "update_datetime")
	private Date updated;

	public CoinType setRateFloat(Double rateFloat) {
		this.rateFloat = new BigDecimal(rateFloat);
		return this;
	}
	
	public CoinType setRateFloat(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		return this;
	}
	
	public CoinType setDescription(String description, String chineseName) {
		this.description = description;
		this.descriptionChinese = chineseName;
		return this;
	}
}

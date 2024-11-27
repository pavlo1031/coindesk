package cathay.coindeskApi.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cathay.coindeskApi.commons.util.CurrencyUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
@DynamicUpdate
@Table(name = "coin_type")
public class CoinType implements Serializable {
	
	@Id
	@Column(name = "code", unique = true)
	private String coinCode;

	@Column(name = "symbol", nullable = false)
	private String symbol;
	
	@Transient
	@Setter(AccessLevel.NONE)
	private String rate;
	
	@Column(name = "rate_float", nullable = false)
	private BigDecimal rateFloat;

	@Column(name = "description")
	private String description;

	@Column(name = "description_zh")
	private String descriptionChinese;

	@JsonIgnore
	@UpdateTimestamp
	@Column(name = "update_time")
	private Date updated;

	@JsonIgnore
	@CreationTimestamp
	@Column(name = "create_time")
	private Date created;
	
	public CoinType() {}
	
	// 可能僅有內部測試會用到
	public CoinType(String code) {
		this(code, null, null, null, null);
	}
	
	public CoinType(String code, String symbol, Number rateFloat) {
		this(code, symbol, rateFloat, null, null);
	}

	public CoinType(String code, String symbol, Number rateFloat, String description) {
		this(code, symbol, rateFloat, description, null);
	}
	
	public CoinType(String code, String symbol, Number rateFloat, String description, String descriptionChinese) {
		this.coinCode = code;
		this.symbol = symbol;
		this.rate = CurrencyUtils.format(rateFloat);
		this.rateFloat = new BigDecimal(rateFloat.doubleValue());
		this.description = description;
		this.descriptionChinese = descriptionChinese;
	}
	
	public CoinType setRateFloat(Double rateFloat) {
		return this.setRateFloat(new BigDecimal(rateFloat.doubleValue()));
	}
	
	public CoinType setRateFloat(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
		this.rate = CurrencyUtils.format(rateFloat);
		return this;
	}
	
	public String getRate() {
		if (this.rate == null)
			this.rate = CurrencyUtils.format(rateFloat);
		return this.rate;
	}
	
	public CoinType setDescription(String description, String descriptionChinese) {
		this.description = description;
		this.descriptionChinese = descriptionChinese;
		return this;
	}
	
	public int hashCode() {
		return this.coinCode.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (CoinType.class != o.getClass())
			return false;
		return this.coinCode.equals(((CoinType) o).getCoinCode());
	}
}

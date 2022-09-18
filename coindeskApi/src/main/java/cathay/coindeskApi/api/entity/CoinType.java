package cathay.coindeskApi.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import cathay.coindeskApi.util.Model;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "coin_type")
@DynamicUpdate
public class CoinType implements Model<CoinType, CoinType.Field>, Serializable {
	
	public enum Field {
		Code("code"),
		Symbol("symbol"),
		Rate("rate"),
		RateFloat("rate_float"),
		Description("description"),
		DescriptionChinese("description_chinese"),
		Updated("update_time");
		
		private String dbColumn;
		
		public String getDatabaseColumn() {
			return dbColumn;
		}
		
		private Field(String dbColumn) {
			this.dbColumn = dbColumn;
		}
	}
	
	@Id
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

	@Column(name = "update_time")
	private Date updated;

	public Object get(Field field) {
		switch(field) {
		case Code:
			return code;
		case Symbol:
			return symbol;
		case Rate:
			return rate;
		case RateFloat:
			return rateFloat;
		case Description:
			return description;
		case DescriptionChinese:
			return descriptionChinese;
		case Updated:
			return updated;
		}
		return null;
	}
	
	public CoinType set(Field field, Object val) {
		switch(field) {
		case Code:
			this.code = (String) val;
			break;
		case Symbol:
			this.symbol = (String) val;
			break;
		case Rate:
			this.rate = (String) val;
			break;
		case RateFloat: {
			BigDecimal val_ = null;
			if (val != null) {
				val_ = new BigDecimal(val.toString());
			}
			this.rateFloat = (BigDecimal) val_;
			break;
		}
		case Description:
			this.description = (String) val;
			break;
		case DescriptionChinese:
			this.descriptionChinese = (String) val;
			break;
		case Updated:
			this.updated = (Date) val;
			break;
		}
		return this;
	}
	
	public static Field[] fields() {
		return Field.values();
	}
	
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

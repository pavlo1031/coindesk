package cathay.coindeskApi.api.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddCoinTypeRequest {
	
	private String code;
	
	private String symbol;
	
	private String description;
	
	private String description_chinese;
	
	private BigDecimal rate_float;
}

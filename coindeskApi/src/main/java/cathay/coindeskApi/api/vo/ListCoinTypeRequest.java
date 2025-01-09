package cathay.coindeskApi.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ListCoinTypeRequest extends CoinTypeRequest {
	/**
	 * 分頁索引, 從0開始計數
	 */
	private Integer pageNumber;
	
	/**
	 * 分頁大小
	 */
	private Integer pageSize;
}

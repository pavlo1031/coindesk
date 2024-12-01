package cathay.coindeskApi.commons.util;

/**
 * 此類別僅為了包裝現有的beans相關的功能 (可能是別的library的實現)
 * 讓它們有額外特性, 例如: bean資料處理完, 會傳回對象
 */
public class BeanUtils {

	public static <TargetType> TargetType copyProperties(Object source, TargetType target) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
		return target;
	}
}

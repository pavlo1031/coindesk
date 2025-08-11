package cathay.coindeskApi.commons.util;

import java.text.DecimalFormat;

/**
 * 貨幣數值/單位的操作、輸出元件
 *  (目前僅有一輸出方法)
 */
public class CurrencyUtils {

	private static final DecimalFormat DefaultCurrencyFormatter = new DecimalFormat("#,##0.000");

	public static String format(Number value) {
		return DefaultCurrencyFormatter.format(value);
	}
}

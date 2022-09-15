package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.util.BatchUpdate.updateFieldValues;
import static cathay.coindeskApi.util.StringUtil.doubleQuoteString;
import static cathay.coindeskApi.api.entity.CoinType.Field.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.entity.CoinType.Field;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.util.BatchUpdate;
import cathay.coindeskApi.util.JsonUtil;

@Service
public class CoinService extends UpdateService<String, CoinType, Field, CoinTypeRepository> {

	private static DecimalFormat CurrencyFormat;
	
	@Autowired
	private InjectionHelper injectionHelper;
	
	@Component
	private static class InjectionHelper {
		@Autowired
		public void setCurrencyFormat(DecimalFormat currencyFormat) {
			CoinService.CurrencyFormat = currencyFormat;
		}
	}
	
	@Autowired
	private CoinTypeRepository coinTypeRepository;
	

	public CoinService(CoinTypeRepository repository) {
		super(repository);
	}
	
	public List<CoinType> getAllCoinTypes() {
		List<CoinType> resultList = null;
		try {
			resultList = coinTypeRepository.findAll();
		}
		finally {
			String s = null;
			if (resultList != null)
				s = JsonUtil.getJsonString(resultList, true);
			System.out.println("--> all coin types: " + s);
		}
		return coinTypeRepository.findAll();
	}
	
	public CoinType getCoinType(String code) {
		CoinType coinType = null;
		try {
			coinType = coinTypeRepository.findByCode(code).orElse(null);
		}
		finally {
			String s = null;
			if (coinType != null)
				s = JsonUtil.getJsonString(coinType, true);
			System.out.println("--> get coin type: " + s);
		}
		return coinType;
	}

	public CoinType addCoinType(String coinCode, String symbol, BigDecimal rateFloat) {
		return addCoinType(coinCode, symbol, rateFloat, null, null);
	}
	
	public CoinType addCoinType(String coinCode, String symbol, BigDecimal rateFloat, String description) {
		return addCoinType(coinCode, symbol, rateFloat, description, null);
	}
	
	public CoinType addCoinType(String coinCode, String symbol, Double rateFloat) {
		return addCoinType(coinCode, symbol, new BigDecimal(rateFloat), null, null);
	}
	
	public CoinType addCoinType(String coinCode, String symbol, Double rateFloat, String description) {
		return addCoinType(coinCode, symbol, new BigDecimal(rateFloat), description, null);
	}
	
	public CoinType addCoinType(String coinCode, String symbol, Double rateFloat, String description, String descriptionChinese) {
		return addCoinType(coinCode, symbol, new BigDecimal(rateFloat), description, descriptionChinese);
	}
	
	public CoinType addCoinType(String coinCode, String symbol, BigDecimal rateFloat, String description, String descriptionChinese) {
		CoinType coinType = new CoinType()
		.setCode(coinCode)
		.setSymbol(symbol)
		.setRate(CurrencyFormat.format(rateFloat))
		.setRateFloat(rateFloat)
		.setDescription(description)
		.setDescriptionChinese(descriptionChinese);
		return addCoinType(coinType);
	}
	
	public CoinType addCoinType(CoinType coinType) {
		CoinType coinType_ = null;
		try {
			if (coinTypeRepository.existsById(coinType.getCode())) {
				throw new IllegalStateException("該coinType已存在: " + doubleQuoteString(coinType.getCode()));
			}
			
			if (coinType.getUpdated() == null)
				coinType.setUpdated(new Date());
			
			coinType_ = coinTypeRepository.save(coinType);
		}
		finally {
			String s = null;
			if (coinType_ != null)
				s = JsonUtil.getJsonString(coinType_, true);
			System.out.println("--> added coin type: " + s);
		}
		return coinType_;
	}
	
	public Integer updateRate(String coinCode, CoinType coinType, BigDecimal rateFloat) {
		BatchUpdate<Field> batchUpdate
			= updateFieldValues(RateFloat, rateFloat)
			  .set(Rate, CurrencyFormat.format(rateFloat));
		return (Integer) update(coinCode, batchUpdate);
	}
	
	public CoinType updateRateAndGet(String coinCode, CoinType coinType, BigDecimal rateFloat) {
		BatchUpdate<Field> batchUpdate
			= updateFieldValues(RateFloat, rateFloat)
			  .set(Rate, CurrencyFormat.format(rateFloat));
		return (CoinType) updateAndGet(coinCode, batchUpdate);
	}
	
	public CoinType updateSymbolAndRate(String coinCode, String symbol, Double rateFloat) {
		return updateSymbolAndRate(coinCode, symbol, new BigDecimal(rateFloat));
	}
	
	public CoinType updateSymbolAndRate(String coinCode, String symbol, BigDecimal rateFloat) {
		CoinType coinType = null;
		try {
			coinType = updateAndGet(coinCode, Symbol, symbol);			
			System.out.println("--> try find again: " + getCoinType(coinCode));
			//if (true) {
			//	throw new RuntimeException("Error: 測試rollback");
			//}
			coinType = updateRateAndGet(coinCode, coinType, rateFloat);
			return coinType;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			System.out.println("[before leaving]: coinType = " + coinType);
		}
	}
	
	protected Object update(String coinCode, CoinType coinType, BatchUpdate<Field> batchUpdate, boolean get, Consumer<CoinType> beforeWriteAction) {
		Object obj = super.update(coinCode, coinType, batchUpdate, get, (coinTypeToFlush) -> {
			coinTypeToFlush.setUpdated(new Date());
		});
		return obj;
	}
	}
}

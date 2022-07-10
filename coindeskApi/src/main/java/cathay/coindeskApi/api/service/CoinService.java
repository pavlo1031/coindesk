package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.util.StringUtil.doubleQuoteString;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.util.JsonUtil;

@Service
public class CoinService {

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
		.setSymbol(coinCode)
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
	
	private Object updateSymbol(String coinCode, String symbol, boolean get) {
		Object returnVal = null;
		CoinType coinType = null;
		
		// 1. find
		coinType = coinTypeRepository.findById(coinCode).orElse(null);
		if (coinType == null)
			throw new IllegalStateException("無法更新symbol欄位, 該筆資料不存在, coinCode = " + coinCode);

		// 2. update
		Throwable error = null;
		try {
			if (coinType.getSymbol().equals(symbol))
				return returnVal = (get)? coinType : 0;

			if (!get) { 
				returnVal = coinTypeRepository.updateSymbol(coinCode, symbol);
			}
			else {
				coinType.setSymbol(symbol);
				coinType.setUpdated(new Date());
				returnVal = coinTypeRepository.save(coinType);
			}
			return returnVal;
		}
		catch (Throwable t) {
			error = t;
			// 記錄錯誤
			return returnVal = coinType;
		}
		finally {
			if (!get) {
				Integer rowsAffected = (Integer) returnVal;
				System.out.println("--> rowsAffected: " + rowsAffected + "\n");
			} else {
				coinType = (CoinType) returnVal;
				String s = null;
				if (coinType != null)
					s = JsonUtil.getJsonString(coinType, true);
				System.out.println("--> updated coin type: " + s + "\n");
			}
		}
	}
	
	@Transactional
	public Integer updateSymbol(String coinCode, String symbol) {
		return (Integer) updateSymbol(coinCode, symbol, false);
	}
	
	@Transactional
	public CoinType updateSymbolAndGet(String coinCode, String symbol) {
		return (CoinType) updateSymbol(coinCode, symbol, true);
	}
	
	@Transactional
	public int updateRate(String coinCode, Double rate) {
		return updateRate(coinCode, new BigDecimal(rate));
	}
	
	@Transactional
	public Integer updateRate(String coinCode, BigDecimal rate) {
		int rowsAffected = 0;
		try {
			rowsAffected = coinTypeRepository.updateRate(coinCode, CurrencyFormat.format(rate), rate);
		} finally {
			System.out.println("--> rowsAffected: " + rowsAffected + "\n");
		}
		return rowsAffected;
	}
	
	@Transactional
	public CoinType updateRateAndGet(String coinCode, Double rate) {
		return updateRateAndGet(coinCode, new BigDecimal(rate));
	}
	
	@Transactional
	public CoinType updateRateAndGet(String coinCode, BigDecimal rate) {
		CoinType coinType = null;
		try {
			if (updateRate(coinCode, rate) <= 0) {
				return null;
			}
			coinType = coinTypeRepository.findById(coinCode).orElse(null);
		}
		finally {
			String s = null;
			if (coinType != null)
				s = JsonUtil.getJsonString(coinType, true);
			System.out.println("--> updated coin type: " + s + "\n");
		}
		return coinType;
	}

	
	@Transactional
	public int updateDescription(String coinCode, String description) {
		int rowsAffected = 0;
		try {
			rowsAffected = coinTypeRepository.updateDescription(coinCode, description);
		} finally {
			System.out.println("--> rowsAffected: " + rowsAffected + "\n");
		}
		return rowsAffected;
	}
	
	@Transactional
	public int updateDescription(String coinCode, String description, String descriptionCh) {
		int rowsAffected = 0;
		try {
			rowsAffected = coinTypeRepository.updateDescription(coinCode, description, descriptionCh);
		} finally {
			System.out.println("--> rowsAffected: " + rowsAffected + "\n");
		}
		return rowsAffected;
	}
	
	@Transactional
	public CoinType updateDescriptionAndGet(String coinCode, String description) {
		CoinType coinType = null;
		try {
			if (updateDescription(coinCode, description) <= 0) {
				return null;
			}
			coinType = coinTypeRepository.findById(coinCode).orElse(null);
		}
		finally {
			String s = null;
			if (coinType != null)
				s = JsonUtil.getJsonString(coinType, true);
			System.out.println("--> updated coin type: " + s + "\n");
		}
		return coinType;
	}
	
	@Transactional
	public CoinType updateDescriptionAndGet(String coinCode, String description, String descriptionCh) {
		CoinType coinType = null;
		try {
			if (updateDescription(coinCode, description, descriptionCh) <= 0) {
				return null;
			}
			coinType = coinTypeRepository.findById(coinCode).orElse(null);
		}
		finally {
			String s = null;
			if (coinType != null)
				s = JsonUtil.getJsonString(coinType, true);
			System.out.println("--> updated coin type: " + s + "\n");
		}
		return coinType;
	}	
	
	@Transactional
	public CoinType updateCoinType(CoinType coinType) {
		CoinType coinType_ = null;
		try {			
			if (coinTypeRepository.existsById(coinType.getCode())) {
				if (coinType.getUpdated() == null)
					coinType.setUpdated(new Date());
				coinType_ = coinTypeRepository.save(coinType);
			}
		}
		finally {
			String s = null;
			if (coinType_ != null)
				s = JsonUtil.getJsonString(coinType_, true);
			System.out.println("--> updated coin type: " + s + "\n");
		}
		return coinTypeRepository.save(coinType);
	}
}

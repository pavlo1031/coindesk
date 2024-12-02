package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.commons.util.BeanUtils.copyProperties;
import static cathay.coindeskApi.commons.util.CollectionUtils.filter;
import static cathay.coindeskApi.commons.util.StringUtils.quoteString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.api.vo.Coin;

@Service
public class CoinService {
	
    @Autowired
    private CoinTypeRepository coinTypeRepository;
    
    public CoinType addCoinType(Coin coin) {
    	if (coin.getCoinCode() == null)
    		throw new IllegalArgumentException("幣別代號coinCode不可為null");
    	
    	// TODO: 檢查幣別是否已存在
    	Optional<CoinType> coinType = coinTypeRepository.findById(coin.getCoinCode());
    	if (coinType.isPresent())
    		 throw new IllegalArgumentException("新增失敗: 無法新增已存在的幣別 " + quoteString(coin.getCoinCode()));

        return coinTypeRepository.save(copyProperties(coin, new CoinType()));
    }
    
    public List<CoinType> addCoinTypes(Coin... coins) {
        return addCoinTypes(Arrays.asList(coins));
    }
    
    public List<CoinType> addCoinTypes(List<Coin> coins) {
    	if (coins == null || coins.size() == 0)
    		return Collections.emptyList();
    	
		// 已存在的幣別
    	//Set<String> existingCoinCodes = checkCoinCodeExistence(coins);
    	HashSet<String> existingCoinCodes = new HashSet<String>(coinTypeRepository.findCoinCodes());
    	
    	// keep the coinTyps to insert
    	List<CoinType> toAddCoinTypes = new ArrayList<CoinType>();
    	
    	// TODO: 處理「不允許新增」的狀況, 例: 必要欄位未填
    	for (Coin coin : filter(coins, (c) -> c.getCoinCode() != null)) {
    		// 略過已存在的幣別
    		if (existingCoinCodes.contains(coin.getCoinCode())) {
    			continue;
    		}
    		
    		// 必填欄位未填
    		// ➜ 是否應該再拋例外時再處理??
    		if (coin.getSymbol() == null || coin.getRateFloat() == null) {
    			continue;
    		}
    		
    		// 建立, 賦值
    		CoinType coinType = copyProperties(coin, new CoinType());
			
    		// 蒐集
    		toAddCoinTypes.add(coinType);
    	}
    	
    	try {
	        return coinTypeRepository.saveAllAndFlush(toAddCoinTypes);
    	}
    	catch (org.springframework.dao.DataIntegrityViolationException e) {
    		
    	}
    	return toAddCoinTypes;
    }
}

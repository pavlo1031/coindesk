package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.commons.util.CollectionUtils.filter;
import static cathay.coindeskApi.commons.util.CollectionUtils.map;
import static cathay.coindeskApi.commons.util.CollectionUtils.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.api.vo.Coin;

@Service
public class CoinService {
	
    @Autowired
    private CoinTypeRepository coinTypeRepository;
    
    public List<CoinType> update(Coin... coins) {
    	return update(Arrays.asList(coins), true);
    }
    
    public List<CoinType> update(List<Coin> coins) {
    	return update(coins, true);
    }
    
    public List<CoinType> update(List<Coin> coins, boolean returningUpdated) {
        if (coins == null || coins.size() == 0)
            return Collections.emptyList();
        
        // 取出: 「有帶coinCode的」幣別更新資料
        List<Coin> updateData = filter(coins, (c) -> c.getCoinCode() != null);

        // 更新參數中有設定的新值
        Map<String, CoinType> beforeUpdated = toMap(
        	// values
        	coinTypeRepository.findAllById(
	        		// 取用: coin的幣別代號來查詢
		    		map(coins, (c) -> c.getCoinCode())),
	        // keys
        	(c) -> c.getCoinCode()
    	);

        for (Coin c : updateData) {
        	boolean dirty = false;
        	CoinType entity = beforeUpdated.get(c.getCoinCode());
        	if (entity == null) {
        		// 略過: 參數coins中包含的無效的幣別資料
        		//      (原本不存在的幣別)
        		continue;
        	}
        	
        	if (c.getSymbol() != null && !c.getSymbol().equals(entity.getSymbol())) {
        		entity.setSymbol(c.getSymbol());
        		dirty = true;
        	}
        	
			if (c.getRateFloat() != null && c.getRateFloat().compareTo(entity.getRateFloat()) != 0) {
				entity.setRateFloat(c.getRateFloat());
				dirty = true;
        	}
			
			if (c.getDescription() != null && !c.getDescription().equals(entity.getDescription())) {
				entity.setDescription(c.getDescription());
				dirty = true;
			}
			
			if (c.getDescriptionChinese() != null && !c.getDescriptionChinese().equals(entity.getDescriptionChinese())) {
				entity.setDescriptionChinese(c.getDescriptionChinese());
				dirty = true;
			}
        	
        	if (!dirty) {
        		beforeUpdated.remove(c.getCoinCode());
        	}
        }
        
        // Perform updates
        List<CoinType> afterUpdated = null;
        try {
        	afterUpdated = coinTypeRepository.saveAll(beforeUpdated.values());
        }
        catch (Throwable t) {
        	// TODO: 未來再處理
        	t.printStackTrace();
        }
        return afterUpdated;
    }
}

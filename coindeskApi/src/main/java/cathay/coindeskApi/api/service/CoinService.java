package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.commons.util.CollectionUtils.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.repository.CoinTypeRepository;

@Service
public class CoinService {
	
    @Autowired
    private CoinTypeRepository coinTypeRepository;

    // 傳入幣別代碼
    public synchronized int delete(String... coinCodes) {
        return delete(Arrays.asList(coinCodes));
    }

    public synchronized int delete(List<String> coinCodes) {
    	if (coinCodes == null || coinCodes.size() == 0)
            return 0;
    	
    	// Perform delete
        int rowsAffected = coinTypeRepository.deleteAllByIds(coinCodes);
        
        return rowsAffected;
    }
    
    public synchronized List<String> deleteAndReturn(String... coinCodes) {
    	return deleteAndReturn(Arrays.asList(coinCodes));
    }
    
    public synchronized List<String> deleteAndReturn(List<String> coinCodes) {
        if (coinCodes == null || coinCodes.size() == 0)
            return Collections.emptyList();
        
        // 改以hash特性容器來裝載, 加速搜尋
        final HashSet<String> coinCodeSetFromRequest = new HashSet<String>(coinCodes);
        
        // 額外開銷1
    	// ➜ 先取得 「本來就存在的」 而且是 「在呼叫端指定要刪除的範圍」
        HashSet<String> beforeDeleted = new HashSet<String>(
    			filter(coinTypeRepository.findCoinCodes(), (code) -> coinCodeSetFromRequest.contains(code)));
        
        // Perform deletion
        coinTypeRepository.deleteAllByIds(coinCodes);
        
        
    	// 額外開銷2
    	// ➜ 再查詢一次: 刪除後的現有幣別集合
    	HashSet<String> afterDeleted = new HashSet<String>(coinTypeRepository.findCoinCodes());
    	
		// 蒐集: 原本存在, 刪完後不存在的幣別
    	final List<String> deleted = new ArrayList<String>();
    	beforeDeleted.forEach((coinCode) -> {
    		if (!afterDeleted.contains(coinCode)) {
    			deleted.add(coinCode);
    		}
		});        
        return deleted;
    }
}

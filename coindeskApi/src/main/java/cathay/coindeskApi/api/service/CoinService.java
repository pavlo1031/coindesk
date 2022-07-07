package cathay.coindeskApi.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;

@Service
public class CoinService {

	@Autowired
	private CoinTypeRepository coinTypeRepository;
	
	public List<CoinType> getAllCoinTypes() {
		return coinTypeRepository.findAll();
	}
	
	public CoinType getCoin(String code) {
		return coinTypeRepository.findByCode(code).orElse(null);
	}
	
	public CoinType addCoinType(CoinType coinType) {
		if (coinType.getUpdated() == null)
			coinType.setUpdated(new Date());
		return coinTypeRepository.save(coinType);
	}
	
	public CoinType updateCoinType(CoinType coinType) {
		if (coinType.getUpdated() == null)
			coinType.setUpdated(new Date());
		return coinTypeRepository.save(coinType);
	}
}

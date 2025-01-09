package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.commons.util.BeanUtils.copyProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.api.vo.Coin;

@Service
public class CoinService {
	
    @Autowired
    private CoinTypeRepository coinTypeRepository;
    
    public List<CoinType> getAllCoinTypes() {
        return coinTypeRepository.findAll();
    }
    
    public List<CoinType> getAllCoinTypes(int pageNumber, int pageSize) {
        return coinTypeRepository.findAll(PageRequest.of(pageNumber, pageSize)).toList();
    }
    
    public Coin findCoinType(String code) {
        Optional<CoinType> coinType = coinTypeRepository.findById(code);
        if (!coinType.isPresent()) {
            return null;
        }
        return copyProperties(coinType.get(), new Coin());
    }
    
    public List<CoinType> findCoinTypes(String... codes) {
        return this.findCoinTypes(Arrays.asList(codes));
    }
    
    public List<CoinType> findCoinTypes(List<String> codes) {
        if (codes == null || codes.size() == 0) {
            return Collections.emptyList();
        }
        return coinTypeRepository.findAllById(codes);
    }

}

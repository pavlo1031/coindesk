package cathay.coindeskApi.api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.repository.CoinTypeRepository;
import cathay.coindeskApi.api.vo.Coin;

@Service
public class CoinService {
	
    @Autowired
    private CoinTypeRepository coinTypeRepository;
}

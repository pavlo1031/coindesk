package cathay.coindeskApi.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.service.CoinService;
import cathay.coindeskApi.api.vo.Coin;
import cathay.coindeskApi.api.vo.CoinResponse;

@RestController
@RequestMapping("/api/v1.0.0/coin")
public class CoindeskApiController {

	@Autowired
	private CoinService coinService;
	
	@RequestMapping("test")
	public ResponseEntity<?> testApi() {
		final List<CoinType> coinTypes = coinService.getAllCoinTypes();
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
		
		if (coinTypes != null) {
			for (CoinType c : coinTypes) {
				Coin coin = new Coin(c.getCode(), c.getSymbol(), c.getRateFloat(), c.getDescription(), c.getDescriptionChinese());
				response.addBpi(c.getCode(), coin);
			}
		}
		return ResponseEntity.ok(response);
	}
}

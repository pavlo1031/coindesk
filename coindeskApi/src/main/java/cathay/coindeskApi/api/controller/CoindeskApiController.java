package cathay.coindeskApi.api.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static cathay.coindeskApi.api.entity.CoinType.Field.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.service.CoinService;
import cathay.coindeskApi.api.vo.Coin;
import cathay.coindeskApi.api.vo.CoinResponse;
import cathay.coindeskApi.util.BatchUpdate;

@RestController
@RequestMapping("/api/v1.0.0/coin")
public class CoindeskApiController {

	@Autowired
	private CoinService coinService;
	
	@GetMapping(path = "list")
	public ResponseEntity<?> list() {
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
	
	@PostMapping(path = "add")
	public ResponseEntity<?> add(@RequestParam(name = "code") String coinCode,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "description_chinese") String descriptionCh,
			@RequestParam(name = "rate_float", required = false) BigDecimal rateFloat) {
		
		System.out.println("add(), POST");
		System.out.println("- code: " + coinCode);
		System.out.println("- symbol: " + symbol);
		System.out.println("- description: " + description);
		System.out.println("- description_chinese: " + descriptionCh);
		System.out.println("- rate_float: " + rateFloat);
		
		// TODO: 需要參數檢核 ??
		
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
				
		CoinType coinType = null;
		try {
			coinType = coinService.addCoinType(coinCode, symbol, rateFloat, description, descriptionCh);
			response.addBpi(coinType.getCode(), new Coin(coinType.getCode(),
					coinType.getSymbol(), coinType.getRateFloat(), coinType.getDescription(),
					coinType.getDescriptionChinese()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok("ok");
	}
	
	@PutMapping(path = "update")
	public ResponseEntity<?> update(
			@RequestParam(name = "code") String coinCode,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "description_chinese", required = false) String descriptionCh,
			@RequestParam(name = "rate_float", required = false) BigDecimal rateFloat) {
		
		System.out.println("update(), PUT");
		System.out.println("- code: " + coinCode);
		System.out.println("- symbol: " + symbol);
		System.out.println("- description: " + description);
		System.out.println("- description_chinese: " + descriptionCh);
		System.out.println("- rate_float: " + rateFloat);

		BatchUpdate<CoinType.Field> batchUpdate = new BatchUpdate<CoinType.Field>();
		if (isNotBlank(symbol))
			batchUpdate.set(Symbol, symbol);
		if (isNotBlank(description))
			batchUpdate.set(Description, description);
		if (isNotBlank(descriptionCh))
			batchUpdate.set(DescriptionChinese, descriptionCh);
		if (rateFloat != null)
			batchUpdate.set(RateFloat, rateFloat);
		
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
		
		CoinType coinType = null;
		try {
			coinType = coinService.updateAndGet(coinCode, batchUpdate);
			response.addBpi(coinType.getCode(), new Coin(coinType.getCode(),
				coinType.getSymbol(), coinType.getRateFloat(), coinType.getDescription(),
				coinType.getDescriptionChinese()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally {
			batchUpdate.reset();
		}
		return ResponseEntity.ok(response);
	}
}

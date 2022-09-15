package cathay.coindeskApi.api.controller;

import static cathay.coindeskApi.api.entity.CoinType.Field.*;
import static cathay.coindeskApi.util.BatchUpdate.updateFieldValues;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static cathay.coindeskApi.util.BeanUtils.copyProperties;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.api.service.CoinService;
import cathay.coindeskApi.api.vo.AddCoinRequest;
import cathay.coindeskApi.api.vo.AddCoinRequestJson;
import cathay.coindeskApi.api.vo.Coin;
import cathay.coindeskApi.api.vo.CoinResponse;
import cathay.coindeskApi.api.vo.UpdateCoinRequest;
import cathay.coindeskApi.api.vo.UpdateCoinRequestJson;
import cathay.coindeskApi.util.BatchUpdate;

@RestController
@RequestMapping("/api/v1.0.0/coin")
public class CoindeskApiController {

	@Autowired
	private CoinService coinService;
	
	@GetMapping(path = "list")
	public ResponseEntity<?> list() {
		System.out.println("list(), GET");
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
		
		List<CoinType> coinTypes = null;
		try {
			coinTypes = coinService.getAllCoinTypes();
			if (coinTypes != null) {
				for (CoinType c : coinTypes)
					response.addBpi(c.getCode(), copyProperties(c, new Coin()));
				
			}
			return ResponseEntity.ok(response);
		}
		catch (Throwable t) {
			return new ResponseEntity<String>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 參數形式
	 * application/json
	 */
	@PostMapping(path = "add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> add(@RequestBody AddCoinRequestJson requestJson) {
		System.out.println("add() for JSON parameters, POST");
		System.out.println("reuqest: " + requestJson);
		
		AddCoinRequest request = copyProperties(requestJson, new AddCoinRequest());
		return add(request);
	}
	
	/**
	 * 參數形式
	 * multipart form data
	 * x-www-form-urlencoded
	 */
	@PostMapping(path = "add")
	public ResponseEntity<?> add(AddCoinRequest request) {
		
		System.out.println("add() for 'multipart form-data' and 'x-www-form-urlencoded', POST");
		System.out.println("- request: " + request);
		
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
				
		CoinType coinType = null;
		try {
			coinType = coinService.addCoinType(request.getCoinCode(), request.getSymbol(), request.getRateFloat(),
					request.getDescription(), request.getDescriptionChinese());
			response.addBpi(coinType.getCode(), new Coin(coinType.getCode(),
					coinType.getSymbol(), coinType.getRateFloat(), coinType.getDescription(),
					coinType.getDescriptionChinese()));
		}
		catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok("ok");
	}
	
	/**
	 * 參數形式
	 * application/json
	 */
	@PostMapping(path = "update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> update(@RequestBody UpdateCoinRequestJson requestJson) {
		System.out.println("add() for JSON parameters, POST");
		System.out.println("request: " + requestJson);
		
		UpdateCoinRequest request = copyProperties(requestJson, new UpdateCoinRequest());
		return update(request);
	}
	
	@PutMapping(path = "update")
	public ResponseEntity<?> update(UpdateCoinRequest request) {
		
		System.out.println("update(), PUT");
		System.out.println("- request: " + request);
		BatchUpdate<CoinType.Field> batchUpdate = updateFieldValues();
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
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally {
			batchUpdate.reset();
		}
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(path = "delete")
	public ResponseEntity<?> delete(@RequestParam(name = "code") String coinCode) {
		

		System.out.println("delete(), DELETE");
		System.out.println("- coin code: " + coinCode);
		
		final CoinResponse response = new CoinResponse()
		.setDisclaimer(
			"This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
			"Non-USD currency data converted using hourly conversion rate from openexchangerates.org")
		.setChartName("Bitcoin");
		
		if (isBlank(coinCode)) {
			throw new IllegalArgumentException("Coin code not present. Please give a non-empty value;");
		}
		
		try {
			coinService.delete(coinCode);
			return ResponseEntity.ok(response);	
		}
		catch (Throwable t) {
			return new ResponseEntity<String>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

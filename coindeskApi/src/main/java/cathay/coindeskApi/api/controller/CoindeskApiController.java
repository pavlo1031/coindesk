package cathay.coindeskApi.api.controller;

import static cathay.coindeskApi.commons.util.CollectionUtils.map;
import static cathay.coindeskApi.commons.util.CollectionUtils.toList;
import static cathay.coindeskApi.commons.util.JsonUtils.getJsonStringPrettyFormat;
import static cathay.coindeskApi.commons.util.StringUtils.quoteString;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cathay.coindeskApi.api.service.CoinService;
import cathay.coindeskApi.api.vo.CoinTypeRequest;
import cathay.coindeskApi.api.vo.CoinTypeResponse;
import cathay.coindeskApi.api.vo.AddCoinTypeRequest;
import cathay.coindeskApi.api.vo.AddCoinTypeResponse;
import cathay.coindeskApi.api.vo.Coin;

@RestController
@RequestMapping("/api/v1.0.0/coin")
public class CoindeskApiController {

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Autowired
	private CoinService coinService;
	
	/**
	 * list 取得所有幣別
	 */
	@GetMapping(path = "list", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> list() {
		System.out.println("list(), GET");
		CoinTypeResponse response = new CoinTypeResponse();
		response.setMsg("後端建構中...");
		return ResponseEntity.ok(response);
	}
	
	/**
	 * add 新增幣種
	 * 
	 * @param coinTypes 欲新增之幣別的資料集合
	 * @param returningAdded 是否要傳回成功新增的幣別; 先預設為true(演示之用), 若是實務上, 會預設為false
	 */
	@PostMapping(path = "add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> add(@RequestBody AddCoinTypeRequest request) {
		System.out.println("add() POST, 接受JSON資料");
		System.out.println("request: " + getJsonStringPrettyFormat(request));
		
		// perform add
		List<Coin> addedCoinTypes = toList(coinService.addCoinTypes(request.getCoins()), Coin.class);
		
		// 篩出未加入的幣別
		List<Coin> coinCodesNotAdded = new ArrayList<Coin>(request.getCoins());
		coinCodesNotAdded.removeAll(addedCoinTypes);
				
		AddCoinTypeResponse response = new AddCoinTypeResponse();		
		// 決定是否回傳新增的幣別資料
		if (request.isReturningAdded()) {
			response.addBpi(addedCoinTypes);	
		} else {
			response.setBpi(null);
		}
		
		// 實際新增的幣別筆數
		response.setRowsAffected(addedCoinTypes.size());
		
		// 未新增的幣別代號, 列在msg欄位中
		if (coinCodesNotAdded.size() > 0) { 
			response.setMsg("幣別代號已存在, 無法新增: " + join(map(coinCodesNotAdded, (c) -> quoteString(c.getCoinCode())), ", "));
		}
		return ResponseEntity.ok(response);
	}

	
	/**
	 * update: 更新幣種資料
	 * @param request 接收JSON資料
	 */
	@PutMapping(path = "update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> update(@RequestBody CoinTypeRequest request) {
		System.out.println("update() PUT, 接受JSON資料");
		CoinTypeResponse response = new CoinTypeResponse()
			.setData(request)
			.setMsg("後端建構中, 已收到JSON資料");
		return ResponseEntity.ok(response);
	}
	
	
	/**
	 * 刪除幣種資料
	 * 
	 * @param coinCode 幣別代號
	 */
	@DeleteMapping(path = "delete", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> delete(@RequestBody CoinTypeRequest request) {	
		System.out.println("delete(), DELETE");
		CoinTypeResponse response = new CoinTypeResponse()
			.setData(request)
			.setMsg("後端建構中, 已收到待刪除的幣別代號");
		return ResponseEntity.ok(response);
	}
}

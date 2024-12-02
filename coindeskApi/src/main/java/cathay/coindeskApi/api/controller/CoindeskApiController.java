package cathay.coindeskApi.api.controller;


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
	 * @param request 接收JSON資料
	 */
	@PostMapping(path = "add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> add(@RequestBody CoinTypeRequest request) {
		System.out.println("add() POST, 接受JSON資料");
		CoinTypeResponse response = new CoinTypeResponse()
			.setData(request)
			.setMsg("後端建構中, 已收到JSON資料");
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

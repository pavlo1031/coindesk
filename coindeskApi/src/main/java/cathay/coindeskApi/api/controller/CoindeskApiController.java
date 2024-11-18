package cathay.coindeskApi.api.controller;

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

@RestController
@RequestMapping("/api/v1.0.0/coin")
public class CoindeskApiController {

	/**
	 * list 取得所有幣別
	 */
	@GetMapping(path = "list")
	public ResponseEntity<?> list() {
		System.out.println("list(), GET");
		return ResponseEntity.ok("後端建構中...");
	}
	
	/**
	 * add 新增幣種
	 * @param request 接收JSON資料
	 */
	@PostMapping(path = "add", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> add(@RequestBody Object request) {
		System.out.println("add() POST, 接受JSON資料");
		return ResponseEntity.ok("後端建構中, 已收到JSON資料: " + request);
	}

	
	/**
	 * update: 更新幣種資料
	 * @param request 接收JSON資料
	 */
	@PutMapping(path = "update", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> update(@RequestBody Object request) {
		System.out.println("update() PUT, 接受JSON資料");
		return ResponseEntity.ok("後端建構中, 已收到JSON資料: " + request);
	}
	
	
	/**
	 * 刪除幣種資料
	 * 
	 * @param coinCode 幣別代號
	 */
	@DeleteMapping(path = "delete")
	public ResponseEntity<?> delete(@RequestParam(name = "code") String coinCode) {	
		System.out.println("delete(), DELETE");
		System.out.println("- coin code: " + coinCode);
		return ResponseEntity.ok("後端建構中, 待刪除的幣別代號為: " + coinCode);
	}
}

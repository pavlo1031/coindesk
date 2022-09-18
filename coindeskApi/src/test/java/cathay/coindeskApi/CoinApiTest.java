package cathay.coindeskApi;

import static cathay.coindeskApi.util.JsonUtil.getJsonStringPrettyFormat;
import static cathay.coindeskApi.util.JsonUtil.getJsonString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cathay.coindeskApi.api.vo.AddCoinRequest;
import cathay.coindeskApi.api.vo.UpdateCoinRequest;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CoinApiTest {
	
	@Autowired
    private MockMvc mvc;
	
	@Test
	@DisplayName("[List] Get all existing coin types")
	public void test_list() throws Exception {
		mvc.perform(
    		MockMvcRequestBuilders
    			.get("/api/v1.0.0/coin/list"))
		 	.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@DisplayName("[Add] New coinType")
	public void test_add_new_coinType() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		
	    AddCoinRequest request
	    	= new AddCoinRequest()
	            .setCoinCode("NTD")
	            .setSymbol("NT$")
	            .setRateFloat(1.0)
	            .setDescription("New Taiwan Dollar")
	            .setDescriptionChinese("新台幣");
	    
		mvc.perform(
    		MockMvcRequestBuilders
	    		.post("/api/v1.0.0/coin/add")
	    		.headers(httpHeaders)
	    		.content(getJsonString(request)))
		 	.andExpect(MockMvcResultMatchers.status().isOk())
		 	.andDo((MvcResult result) -> {
		 		System.out.println("result: " + getJsonStringPrettyFormat(result.getResponse().getContentAsString()));
		 	});
	}
	
	@Test
	public void test_add_new_coinType() {
		
	}
	
	@Test
	public void test_add_duplicated_coinType() {
		
	}	
	
	@Test
	public void test_update_single_field() {
		
	}
	
	@Test
	public void test_update_multiple_fields() {
		
	}
	
	@Test
	public void test_update_nonExisting_on_coinType() {

	}
	
	@Test
	@DisplayName("[Delete] Existing coinType")
	public void test_delete_existing_coinTypes() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		
		mvc.perform(
    		MockMvcRequestBuilders
	    		.delete("/api/v1.0.0/coin/delete")
	    		.headers(httpHeaders)
	    		.param("code", "USD"))
		 	.andExpect(MockMvcResultMatchers.status().isOk())
		 	.andDo((MvcResult result) -> {
		 		System.out.println("result: " + getJsonStringPrettyFormat(result.getResponse().getContentAsString()));
		 	});	
	}
}

package cathay.coindeskApi;

import static cathay.coindeskApi.util.JsonUtil.getJsonStringPrettyFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CoinApiTest {
	
	@Autowired
    private MockMvc mvc;
	
	@Test
	public void test_list() throws Exception {
		mvc.perform(
    		MockMvcRequestBuilders
    		.get("/api/v1.0.0/coin/list"))
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
	public void test_update_nonChanged_coinType_columns() {
		
	}
}

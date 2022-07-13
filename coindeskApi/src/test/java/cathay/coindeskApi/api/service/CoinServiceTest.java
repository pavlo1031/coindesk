package cathay.coindeskApi.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@SpringBootTest()
@DisplayName("Test CoinService")
@ExtendWith(SpringExtension.class)
@RunWith(HierarchicalContextRunner.class)
public class CoinServiceTest {

	// 待查詢test中的注入方式
	private CoinService coinService;
	
	@Nested
	public static class List {
		@Test
		@DisplayName("list all coinTypes")
		public void test_list() {
			
		}
	}
	
	@Nested
	@DisplayName("Add CoinType")
	public static class Add {
		@Test
		@DisplayName("add new coinType")
		public void test_add_new_coinType() {
			
		}
		
		@Test
		@DisplayName("add duplicate coinType, 預期拋出exception")
		public void test_add_duplicated_coinType() {
			
		}
	}

	@Nested
	@DisplayName("Update CoinType")
	public static class Update {
		
		@Test
		@DisplayName("update single field")
		public void test_update_single_field() {
			
		}
		
		@Test
		@DisplayName("update multiple columns")
		public void test_update_multiple_fields() {
			
		}
		
		@Test
		@DisplayName("update columns on non-existing coinType, 預期拋出exception")
		public void test_update_nonExisting_on_coinType() {

		}
		
		@Test
		@DisplayName("update but no column are written (value not changed), 預期得到rowsAffected = 0")
		public void test_update_nonChanged_coinType_columns() {
			// 預期得到rowsAffected = 0
			// 若是updateAndGet, 如何判斷未寫入新值??
		}
	}
}

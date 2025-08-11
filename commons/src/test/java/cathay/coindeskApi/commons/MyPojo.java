package cathay.coindeskApi.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MyPojo {
	String id;
	String name;
	Integer age;
	public MyPojo(String id) {
		this.id = id;
	}
}
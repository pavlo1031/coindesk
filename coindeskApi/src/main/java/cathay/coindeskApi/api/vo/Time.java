package cathay.coindeskApi.api.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Time implements Serializable {

	private String updated = "aaa";
	
	private String updatedISO = "bbb";
	
	private String updateduk = "ccc";
	
	public Time() {
		Date date = new Date();
		updated = date.toString();
		updatedISO = date.toGMTString();
		updateduk = updated;
	}
}

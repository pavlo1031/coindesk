package cathay.coindeskApi.api.vo;

import java.io.Serializable;
import java.util.Date;

import cathay.coindeskApi.commons.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Time implements Serializable {

	private String updated;
	
	private String updatedISO;
	
	private String updateduk;
	
	public Time() {
		Date date = new Date();
		updated = date.toString();
		updatedISO = date.toGMTString();
		updateduk = updated;
	}
	
	public static void main(String[] args) {
		System.out.println(JsonUtils.getJsonStringPrettyFormat("new time", new Time()));
	}
}

package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.JsonUtils.getJsonStringPrettyFormat;
import static cathay.coindeskApi.commons.util.JsonUtils.getObjectMapper;
import static cathay.coindeskApi.commons.util.MapUtils.of;

import java.beans.PropertyDescriptor;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import cathay.coindeskApi.commons.MyType;

public class BeanUtilsExample {

	public static void main(String[] args) {
		PropertyDescriptor descriptor;
		
		
		// Set including null
		getObjectMapper()
		 .setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
		
		Map<String, String> getters = BeanUtils.getGetterNameByProperty(MyType.class,
				of("x", boolean.class,
				   "virgin1", Boolean.class,
				   "virgin2", boolean.class,
				   "single1", Boolean.class,
				   "single2", boolean.class,
				   "isRich", Boolean.class,
				   "isRich", boolean.class,
				   "fromMiddleClass", Boolean.class,
				   "fromMiddleClass", boolean.class,
				   "fromHighSocialClass", Boolean.class,
				   "fromHighSocialClass", boolean.class,
				   "id", String.class,
				   "nAme", String.class,
				   "age", int.class,
				   "xY", String.class,
				   "xYz", int.class,
				   "xIndex", int.class)
		);
		
		System.out.println("➜ Getters: "
			+ getJsonStringPrettyFormat(getters) + "\n\n");
	}
}

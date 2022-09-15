package cathay.coindeskApi.util;

public class BeanUtils {

	public static <TargetType> TargetType copyProperties(Object source, TargetType target) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
		return target;
	}
}

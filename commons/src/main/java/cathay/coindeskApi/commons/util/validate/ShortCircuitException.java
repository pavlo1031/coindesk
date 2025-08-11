package cathay.coindeskApi.commons.util.validate;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 「Predicate條件判斷時, 由於短路特性提早傳回結果」的狀況
 */
public class ShortCircuitException extends RuntimeException {

	private Object result;
	
    private String shortCircuitCondition;   // 哪個條件導致短路
    
    private List<String> skippedConditions; // 哪些條件被跳過（可為空）
	
	public ShortCircuitException() {
		super();
	}
	
	public ShortCircuitException(String msg) {
		super(msg);
	}

	public ShortCircuitException(Object result) {
		super((Throwable) null);
		this.result = result;
	}
	
	public ShortCircuitException(Throwable cause) {
		super(cause);
	}
	
	public ShortCircuitException(String msg, Object result) {
		super(msg, (Throwable) null);
		this.result = result;
	}
	
	public ShortCircuitException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ShortCircuitException(Object result, Throwable cause) {
		super(cause);
		this.result = result;
	}
	
	public ShortCircuitException(String msg, Object result, Throwable cause) {
		super(msg, cause);
		this.result = result;
	}
	
	public ShortCircuitException(String shortCircuitCondition, String... skippedConditions) {
		this((Object) null, shortCircuitCondition, skippedConditions);
	}
	
	public ShortCircuitException(Object result, String shortCircuitCondition, String... skippedConditions) {
		super(buildMessage(shortCircuitCondition, skippedConditions).toString(), (Throwable) null);
		this.result = result;
		this.shortCircuitCondition = shortCircuitCondition;
		this.skippedConditions = Arrays.asList(skippedConditions);
	}
	
	public ShortCircuitException(Throwable cause, String shortCircuitCondition, String... skippedConditions) {
		super(buildMessage(shortCircuitCondition, skippedConditions).toString(), cause);
		this.shortCircuitCondition = shortCircuitCondition;
		this.skippedConditions = Arrays.asList(skippedConditions).stream().filter((s) -> StringUtils.isBlank(s) != true).toList();
	}
	
	public <R> R getResult() {
		return (R) this.result;
	}
	
	public String getShortCircuitCondition() {
		return this.shortCircuitCondition;
	}
	
	public List<String> getSkippedConditions() {
		return this.skippedConditions;
	}
	
	public String getSkippedConditions(int index) {
		return this.skippedConditions.get(index);
	}
	
	private static StringBuilder buildMessage(String shortCircuitCondition, String... skippedConditions) {
		StringBuilder buffer = new StringBuilder();
		if (!StringUtils.isEmpty(shortCircuitCondition)) {
			buffer.append("提前離開的條件: ").append(shortCircuitCondition);
		}
		if (skippedConditions != null && skippedConditions.length > 0) {
			if (!buffer.isEmpty())
				buffer.append(", ");
			buffer.append("略過未判斷離開的條件: ").append(String.join(", ", skippedConditions));
		}
		return buffer;
	}
}

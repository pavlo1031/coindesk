package cathay.coindeskApi.commons.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.function.Consumer;
import java.util.function.Function;

import cathay.coindeskApi.commons.util.function.NoArgConsumer;
import cathay.coindeskApi.commons.util.function.NoArgFunction;
import lombok.Builder;

/**
 * 效能分析工具
 */
public class ProfileUtils {
	
	/**
	 * 未來可能提供更多統計結果、或格式化輸出方法
	 */
	@Builder
	public static class ProfilingResult {
		
		public CharSequence label;
		
		public Number timeCost;
		
		public Object returnValue;
		
		public final boolean hasReturnValue;
		
		public static class ProfilingResultBuilder {
			// customize setter
			public ProfilingResultBuilder returnValue(Object returnValue) {
				// 只要有呼叫builder().returnValue
				// 一律設定為有傳回值, 只是值為null
				this.returnValue = returnValue;
				this.hasReturnValue = true;
				return this;
			}
		}
		
		////  提供: 鏈式呼叫的用法
		////  (未來其他操作方式, 也會參考此寫法)
		//public ProfilingResult xxx(Consumer<?> callback) {
		//	if (callback != null)
		//		((Consumer<Object>) callback).accept((Object) this.returnValue);
		//	return this;
		//}
	}
	
	public static Number executionTime(NoArgConsumer task) {
		return executionTime(null, 1, null, (arg) -> task.accept());
	}
	
	public static Number executionTime(CharSequence label, NoArgConsumer task) {
		return executionTime(label, 1, null, (arg) -> task.accept());
	}
	
	public static Number executionTime(CharSequence label, Number times, NoArgConsumer task) {
		return executionTime(label, times, null, (arg) -> task.accept());
	}
	
	public static <T> Number executionTime(T argument, Consumer<T> task) {
		return executionTime(null, 1, argument, task);
	}
	
	public static <T> Number executionTime(CharSequence label, T argument, Consumer<T> task) {
		return executionTime(label, 1, argument, task);
	}
	
	public static <T> Number executionTime(CharSequence label, Number times, T argument, Consumer<T> task) {
		if (task == null)
			throw new NullPointerException("The task to execute cannot be null");
			
		System.out.println("\n\n#### Calculate Execution Time: " + ((isNotBlank(label))? "\"" + label + "\"":"") + " ####");
		System.out.println("--------------------------------------------------");
		
		Long totalCost = 0L;
		for (Long i=0L; i < times.longValue(); i++) {
			long startTime = System.nanoTime();
			task.accept(argument);
			long endTime = System.nanoTime();
			
			long duration = endTime - startTime;
			if (times.intValue() <= 1)
				System.out.printf("➜ ");
			else
				System.out.printf("[%d] ", i);
			System.out.println("time cost: " + duration + " nanoSec\n");
			totalCost += duration;
		}
		System.out.println();
		return (Number) totalCost;
	}
	
	public static <T> ProfilingResult executionTime(CharSequence label, NoArgFunction<?> task) {
		return executionTime(label, null, (arg) -> { Object ret = task.apply(null); return ret; });
	}
	
	public static <T> ProfilingResult executionTime(NoArgFunction<?> task) {
		return executionTime(null, null, (arg) -> { Object ret = task.apply(null); return ret; });
	}
	
	public static <T> ProfilingResult executionTime(T argument, Function<T, ?> task) {
		return executionTime(null, argument, task);
	}
	
	public static <T> ProfilingResult executionTime(CharSequence label, T argument, Function<T, ?> task) {
		if (task == null)
			throw new NullPointerException("The task to execute cannot be null");
			
		System.out.println("\n\n#### Calculate Execution Time: " + ((isNotBlank(label))? "\"" + label + "\"":"") + " ####");
		System.out.println("--------------------------------------------------");
		
		long startTime = System.nanoTime();
		Object ret = task.apply(argument);
		long endTime = System.nanoTime();
		long duration = endTime - startTime;		
		System.out.println("➜ time cost: " + duration + " nanoSec\n");
		
		return ProfilingResult.builder().label(label).timeCost(duration).returnValue(ret).build();
	}
}

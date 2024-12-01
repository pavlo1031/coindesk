package cathay.coindeskApi.commons.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.function.Consumer;

import cathay.coindeskApi.commons.util.function.NoArgConsumer;

/**
 * 效能分析工具
 */
public class ProfileUtils {
	
	public static Number executionTime(NoArgConsumer task) {
		return executionTime(null, 1, null, (arg) -> task.accept());
	}
	
	public static Number executionTime(CharSequence label, NoArgConsumer task) {
		return executionTime(label, 1, null, (arg) -> task.accept());
	}
	
	// TODO: Number times --> 可能解讀成 T argument
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
			
		if (isNotBlank(label))
			System.out.println("\n\n#### Calculate Execution Time: " + "\"" + label + "\"");
		System.out.println("--------------------------------------------------");
		
		Long totalCost = 0L;
		for (Long i=0L; i < times.longValue(); i++) {
			long startTime = System.currentTimeMillis();
			task.accept(argument);
			long endTime = System.currentTimeMillis();
			
			long duration = endTime - startTime;
			if (times.intValue() <= 1)
				System.out.printf("➜ ");
			else
				System.out.printf("[%d] ", i);
			System.out.println("time cost: " + duration + " ms");
			totalCost += duration;
		}
		System.out.println();
		return (Number) totalCost;
	}
}

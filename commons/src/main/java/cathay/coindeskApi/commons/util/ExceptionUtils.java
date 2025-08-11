package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.isEmpty;
import static java.util.stream.StreamSupport.stream;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import cathay.coindeskApi.commons.enums.Direction;

public class ExceptionUtils {
	
	public static <ExceptionType extends Throwable> ExceptionType setCause(ExceptionType exception, Throwable cause) {
		if (cause != null) {
			try {
				Method setCause = Throwable.class.getDeclaredMethod("setCause", Throwable.class);
				boolean accessible = setCause.canAccess(exception); 
				setCause.setAccessible(true);
				// call the setter
				setCause.invoke(exception, cause);
				if (!accessible) setCause.setAccessible(false);
			}
			// 使用reflection設定cause失敗
			catch (Exception reflectionError) {
				// NoSuchFieldException | NoSuchMethodException | SecurityException
				// InaccessibleObjectException: VM參數: "--add-opens java.base/java.lang=ALL-UNNAMED"
				// IllegalAccessException | InvocationTargetException
				throw new IllegalStateException("Reflection failed: 無法將cause設定給參數exception", reflectionError);
			}
		}
		return exception;
	}
	
	///////////////////
	
	public static <ExceptionType extends Throwable> ExceptionType filterStackTraceElements(ExceptionType exception, Predicate<StackTraceElement> filter) {
		return setStackTraceElementsThen(exception, exception.getStackTrace(), Direction.Forward, filter, (Consumer<StackTraceElement[]>) null);
	}
	
	public static <ExceptionType extends Throwable> ExceptionType filterStackTraceElementsThen(ExceptionType exception, Predicate<StackTraceElement> filter, Consumer<StackTraceElement[]> completedThen) {
		return setStackTraceElementsThen(exception, exception.getStackTrace(), Direction.Forward, filter, completedThen);
	}
	
	///////////////////
	
	public static <ExceptionType extends Throwable> ExceptionType setStackTraceElements(ExceptionType exception, StackTraceElement[] stackTraceElements, Predicate<StackTraceElement> filter) {
		return setStackTraceElementsThen(exception, Direction.Forward.<StackTraceElement>iterable(stackTraceElements), filter, (Consumer<StackTraceElement[]>) null);
	}
	
	public static <ExceptionType extends Throwable> ExceptionType setStackTraceElementsThen(ExceptionType exception, StackTraceElement[] stackTraceElements, Direction travserOrder, Predicate<StackTraceElement> filter, Consumer<StackTraceElement[]> completedThen) {
		return setStackTraceElementsThen(exception, travserOrder.<StackTraceElement>iterable(stackTraceElements), filter, completedThen);
	}
	
	public static <ExceptionType extends Throwable> ExceptionType setStackTraceElements(ExceptionType exception, Iterable<StackTraceElement> stackTraceElements, Predicate<StackTraceElement> filter) {
		return setStackTraceElementsThen(exception, stackTraceElements, filter, (Consumer<StackTraceElement[]>) null);
	}
	
	public static <ExceptionType extends Throwable> ExceptionType setStackTraceElementsThen(ExceptionType exception, Iterable<StackTraceElement> stackTraceElements, Predicate<StackTraceElement> filter, Consumer<StackTraceElement[]> completedThen) {
		if (isEmpty(stackTraceElements)) {
			exception.setStackTrace(null);	
			return null;
		}
		
		// 產生: 過濾後的trace stack elements
		Stream<StackTraceElement> stream = stream(stackTraceElements.spliterator(), false);
		StackTraceElement[] newStackTraceElements = stream.filter(filter).toArray(StackTraceElement[]::new);
		// set
		exception.setStackTrace(newStackTraceElements);
		
		if (completedThen != null)
			completedThen.accept(newStackTraceElements);
		return exception;
	}
}

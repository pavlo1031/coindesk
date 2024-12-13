package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ReflectionUtils.getConstructor;
import static cathay.coindeskApi.commons.util.ReflectionUtils.getInnerType;
import static cathay.coindeskApi.commons.util.ReflectionUtils.getMethod;

import java.lang.reflect.InvocationTargetException;

import cathay.coindeskApi.commons.MyPojo;
import cathay.coindeskApi.commons.MyType;

public class CallFlowCompletionStageExample {

	public static void main(String[] args) {
		// instantiation
		MyType instance = null;
		try {
			instance = getConstructor(MyType.class).newInstance();
			System.out.println("[INFO]: instance created by constructor: " + instance + "\n");
		}
		catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Inner Type 1 (static)
		System.out.println("innerType 1:");
		MyType.MyInnerType1 myInnerType1 = getInnerType(MyType.class, "MyInnerType1")
		.newInstance()
		.getInstance();
		System.out.println("instance of myInnerType1: " + myInnerType1 + "\n");
		
		////////////////////////////////////////////////////////////////////////
		
		if (instance == null) {
			System.out.println("[Warn] instance建立失敗, 無法執行之後的instance method, 離開main()");
			return;
		}
		
		// Inner Type 2 (non-static)
		System.out.println("innerType 2:");
		MyType.MyInnerType2 myInnerType2 = getInnerType(MyType.class, "MyInnerType2")
		 .bindEnclosingInstance(instance)
		 .newInstance("xyz", 12345)
		 .getInstance();
		System.out.println("instance of myInnerType2: " + myInnerType2 + "\n");
		
		
		// method1
		getMethod(MyType.class, "method1").bind(instance)
		.call()
		.then((result) -> {
			System.out.println("[then] method1 result: " + result + "\n");
		})
		.onError((e) -> {
			System.out.println("[WARN]: " + e.getClass().getSimpleName() + ": " + e.getMessage() + "\n");
		});
		
		
		// method 2
		int returnedInt = getMethod(MyType.class, "method2", int.class).bind(instance)
		.call(12345, (result) -> {
			System.out.println("[Call] result = " + result);
		})
		.then((result) -> {
			System.out.println("[Then] result = " + result);
		})
		.get();
		System.out.println("➜ returnedInt = " + returnedInt + "\n");
		
		
		// method 3
		Object returnedObject = getMethod(MyType.class, "method3", String.class).bind(instance)
		.call("hey jude", (result) -> {
			System.out.println("[Call] result = " + "\"" + result + "\"");
		})
		.then((MyPojo result) -> {
			System.out.println("[Then] result = " + "\"" + result + "\"");
		})
		.get();
		System.out.println("➜ returnedString = " + "\"" + returnedObject + "\"" + "\n");
	}
}

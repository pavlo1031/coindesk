package cathay.coindeskApi.commons;

import static cathay.coindeskApi.commons.util.TypeUtils.isNumericType;
import static cathay.coindeskApi.commons.util.TypeUtils.isPrimitiveNumericType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import cathay.coindeskApi.commons.util.JsonUtils;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MyType {
	
	private String id;
	
	private String nAme;
	
	private int Age;
	
	private int xIndex;
	
	// Access will failed
	// (好一個他媽的撒旦宣言啊)
	private boolean virgin = false;
	
	private boolean single = false;
	
	private Boolean isRich = true;
	
	private Boolean fromMiddleClass = true;
	
	private Boolean fromHighSocialClass = true;
	
	
	public static void main(String[] args) {
		MyType mytype = new MyType();
		
		BeanInfo mytypeBeanInfo = null;
		PropertyDescriptor[] descripters = null;
		try {
			mytypeBeanInfo = Introspector.getBeanInfo(MyType.class, Introspector.USE_ALL_BEANINFO);
			descripters = mytypeBeanInfo.getPropertyDescriptors();
			System.out.println(JsonUtils.getJsonStringPrettyFormat(descripters));
		}
		catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 覆蓋: 預設命名 "getXIndex"
	public int getxIndex() {
		System.out.println("[Warn] 覆蓋lombok預設命名 'getXIndex'");
		return this.xIndex;
	}
	
	public boolean isFromHighSocialClass() {
		return this.fromHighSocialClass;
	}
	
	
	@ToString
	public static class MyInnerType1 {
		private String name = "It is an instance of MyInnerType1";
		public MyInnerType1() { System.out.println("*** 執行 MyInnerType1::constructor()"); }
	}
	
	@ToString
	public class MyInnerType2 {
		private String name = "It is an instance of MyInnerType2";
		public MyInnerType2() { System.out.println("*** 執行 MyInnerType1::constructor()"); }
		public MyInnerType2(String s, Integer val) { System.out.println("*** 執行 MyInnerType1::constructor(String, val), s=" + s + ", val=" + val); }
	}
	
	MyType() { System.out.println("*** 執行 MyType::constructor()"); }
	MyType (int val) { System.out.println("*** 執行 private MyType::constructor(int)"); }
	
	MyInnerType2 getMyInnerType2Instance() {
		return new MyInnerType2();
	}
	
	public MyType set(Property property, Object value) {
		property.set(this, value);
		return this;
	}
	
	public <T> T get(Property property) {
		return (T) property.get(this);
	}
	
	void method1() { System.out.println("call: method1()"); }
	
	int method2(int val) { System.out.println("call: method2(int), 參數val = " + val); return val / 2; }
	
	static MyPojo method3(String id) {
		System.out.println("call: method3(String), 參數id = " + id);
		return new MyPojo(id);
	}
	
	public enum Property {
		Id(String.class) {
			public MyType set(MyType mytype, Object value) {
				if (value == null) {
					mytype.id = null;
					return mytype;
				}
				if (String.class == value.getClass()) {
					mytype.id = (String) value;
					return mytype;
				}
				else if (CharSequence.class.isAssignableFrom(value.getClass())) {
					mytype.id = ((CharSequence) value).toString();
					return mytype;
				}
				throw new IllegalArgumentException("argument `id` must be a String or CharSequence.");
			}

			public String get(MyType mytype) { return mytype.id; }
		},
		
		Name(String.class) {
			public MyType set(MyType mytype, Object value) {
				if (value == null) {
					mytype.nAme = null;
					return mytype;
				}
				
				if (String.class == value.getClass()) {
					mytype.nAme = (String) value;
					return mytype;
				}
				else if (CharSequence.class.isAssignableFrom(value.getClass())) {
					mytype.nAme = ((CharSequence) value).toString();
					return mytype;
				}				
				throw new IllegalArgumentException("argument `name` must be a String or CharSequence.");
			}

			public String get(MyType mytype) { return mytype.nAme; }
		},
		
		Age(int.class) {
			public MyType set(MyType mytype, Object value) {
				if (value == null) {
					mytype.Age = 0;
					return mytype;
				}
				
				if (isPrimitiveNumericType(value.getClass())) {
					mytype.Age = ((Number) value).intValue();
					return mytype;
				}
				else if (isNumericType(value.getClass())) {
					mytype.Age = ((Number) value).intValue();
					return mytype;
				}
				throw new IllegalArgumentException("argument `age` must be a numeric type.");
			}

			public Integer get(MyType mytype) { return mytype.Age; }
		},
		
		Virgin(boolean.class) {
			public MyType set(MyType mytype, Object isVirgin) {
				if (isVirgin == null) {
					mytype.virgin = false;
					return mytype;
				}
				
				if (boolean.class == isVirgin.getClass() || Boolean.class == isVirgin.getClass()) {
					mytype.virgin = ((Boolean) isVirgin).booleanValue();
					return mytype;
				}
				else if (Number.class.isAssignableFrom(isVirgin.getClass())) {
					mytype.virgin = ((Number) isVirgin).intValue() == 0;
					return mytype;
				}
				throw new IllegalArgumentException("argument `virgin` must be a boolean or numeric type (nonzero value will be treated as true).");
			}

			public Boolean get(MyType mytype) { return mytype.virgin; }
		};
		
		private Class<?> type;
		
		private Property(Class<?> type) { this.type = type; }
		
		public abstract MyType set(MyType mytype, Object value);
		
		public abstract Object get(MyType mytype);
	}
}

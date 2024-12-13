package cathay.coindeskApi.commons.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class PropertyDescriptorExample {

	public static void main(String[] args) throws IntrospectionException {
		Person person = new Person();
        person.setName("huaijin");
        person.setAge(18);
        person.addAddress("hangzhou", "anhui");
        
        
        BeanInfo personBeanInfo = Introspector.getBeanInfo(Person.class, Introspector.USE_ALL_BEANINFO);
        
        for (PropertyDescriptor descriptor : personBeanInfo.getPropertyDescriptors()) {
        	System.out.println("name: " + "\'" + descriptor.getName() + "\'");
        	System.out.println("display name: " + "\'" + descriptor.getDisplayName() + "\'");
        	System.out.println("short description: " + "\'" + descriptor.getShortDescription() + "\'");
        	System.out.println("read method: " + "\'" + descriptor.getReadMethod() + "\'");
        	System.out.println("write method: " + "\'" + descriptor.getWriteMethod() + "\'");
        	
        	System.out.println();
        }
        
        /*
        BeanInfo personBeanInfo = null;
        PropertyDescriptor[] propertyDescriptors = null;
        try {
			personBeanInfo = Introspector.getBeanInfo(Person.class, Introspector.USE_ALL_BEANINFO);
			propertyDescriptors = personBeanInfo.getPropertyDescriptors();
		
			// 獲取PropertyDescriptor，讀/寫屬性
	        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
	            System.out.println("property name: " + propertyDescriptor.getName());
	            System.out.println("read before writing: " + propertyDescriptor.getReadMethod().invoke(person, null));
	            Class<?> propertyClass = propertyDescriptor.getPropertyType();
	            if (propertyClass == String.class) {
	                propertyDescriptor.getWriteMethod().invoke(person, "lxy");
	            } else if (propertyClass == int.class) {
	                propertyDescriptor.getWriteMethod().invoke(person, 28);
	            }
	            if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
	                IndexedPropertyDescriptor indexedPropertyDescriptor =
	                        (IndexedPropertyDescriptor) propertyDescriptor;
	                indexedPropertyDescriptor.getIndexedWriteMethod().invoke(person, 0, "2dfire");
	            }
	            System.out.println("read after writing: " + propertyDescriptor.getReadMethod().invoke(person, null) + "\n");
	        }
	        
	        // 獲取MethodDescriptor，可以操作JavaBean
	        MethodDescriptor[] methodDescriptors = personBeanInfo.getMethodDescriptors();
	        for (MethodDescriptor methodDescriptor : methodDescriptors) {
	            System.out.println("methodName: " + methodDescriptor.getName());
	        }
	        */
        }
//        catch (IntrospectionException e) {
//			e.printStackTrace();
//		}
//        catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

@Data
//@Accessors(chain = true)
class Person {
    private String name;
    private int age;
    private List<String> addresses = new ArrayList<>();
    
    public Person addAddress(String... addresses) {
    	for (int i=0; i<addresses.length; i++)
    		this.addresses.add(addresses[i]);
    	return this;
    }
    
    public void setAddresses(String... addresses) {
    	this.addresses.clear();
    	for (int i=0; i<addresses.length; i++)
    		this.addresses.add(addresses[i]);
//    	return this;
    }
    
    public void setAddresses(List<String> addresses) {
    	this.addresses = addresses;
//    	return this;
    }
}

package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ObjectUtils.allSamePresenceStatus;
import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.StringUtils.remove;
import static cathay.coindeskApi.commons.util.regex.Patterns.PATTERN_FQCN;
import static lombok.AccessLevel.NONE;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cathay.coindeskApi.commons.enums.ClassNameFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TypeSignatureExpression<T> {
	
	public final static String PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION = String.format("(\\[+)?([BSIJFDCZL])?(?:(%s)(;)?)?(@[a-zA-Z0-9]+)?", remove(PATTERN_FQCN, "?<packageName>", "?<className>"));
	
	// 一般轉型失敗的訊息: (T) value
	public final static String PATTERN_STRING_CLASS_CAST_EXCEPTION_MESSAGE = "class (?<src>%s) cannot be cast to class (?<dest>%s) .*".replace("%s", PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION);

	// Class.cast轉型失敗的訊息: Class.cast(value)
	public final static String PATTERN_STRING_CANNOT_CAST_EXCEPTION_MESSAGE = "Cannot cast (?<src>%s) to (?<dest>%s)".replace("%s", PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION);
	
	
	private final static Pattern PATTERN_TYPE_SIGNATURE_EXPRESSION = Pattern.compile(PATTERN_STRING_TYPE_SIGNATURE_EXPRESSION);
	
	private final static Pattern PATTERN_CLASS_CAST_EXCEPTION_MESSAGE = Pattern.compile(PATTERN_STRING_CLASS_CAST_EXCEPTION_MESSAGE);
	
	private final static Pattern PATTERN_CANNOT_CAST_EXCEPTION_MESSAGE = Pattern.compile(PATTERN_STRING_CANNOT_CAST_EXCEPTION_MESSAGE);
	
	private final static Map<String, Class<?>> TypeDescriptorMappings = MapUtils.of(
		"B", byte.class,
		"S", short.class,
		"I", int.class,
		"J", long.class,
		"F", float.class,
		"D", double.class,
		"C", char.class,
		"Z", boolean.class,
		"V", void.class
	);
	
	@Setter(NONE) @Getter(NONE)
	private final String expression;
	
	private final String arrayTypePrefix;
	private final String typeDescriptor;
	private final String classname;
	//private final String semicolon;
	private final String hashCode;
	
	private Class<?> componentType;
	private Class<T> type;
	
	private ClassNameFormat classnameFormat;
	
	public TypeSignatureExpression(CharSequence s) {
		Matcher matcher = PATTERN_TYPE_SIGNATURE_EXPRESSION.matcher(s);
		if (matcher.matches() /* 實際執行匹配, 獲取結果 */) {
			this.expression = s.toString();
			this.arrayTypePrefix = matcher.group(1);
			this.typeDescriptor = (matcher.group(2) != null)? matcher.group(2) : "L";
			this.classname = matcher.group(3);
			// packageName = matcher.group(4);
			// className   = matcher.group(5);
			//this.semicolon   = matcher.group(6);
			this.hashCode = matcher.group(7);
			
			// 檢查: L和分號';'必須成對出現
			if (!allSamePresenceStatus(matcher.group(2), matcher.group(4)))
				if ("L".equals(matcher.group(2)))
					throw new IllegalArgumentException("Type Signature表示式中: 有'L'時, 也必須有結尾的分號: " + s);	
			
			// Classname format
			if (this.classname != null) {
				if (this.classname.contains("/"))
					this.classnameFormat = ClassNameFormat.INTERNAL;
				else if (this.classname.contains("."))
					this.classnameFormat = ClassNameFormat.BINARY;
				else {
					throw new IllegalArgumentException("Illegal classname part " + doubleQuoteString(this.classname));
				}
			}
			
			// 識別type signature中的型別
			Class<?> typeOfClassname = null;
			if (TypeDescriptorMappings.containsKey(this.typeDescriptor))
				typeOfClassname = TypeDescriptorMappings.get(this.typeDescriptor);
			else {
				try {
					typeOfClassname = Class.forName(this.classname.replace('/', '.'));
				} catch (ClassNotFoundException e) {
					System.out.println("[WARN] class not found: " + doubleQuoteString(this.classname));
					throw new RuntimeException("Failed to find class " + this.classname, e);
				}
			}
			
			// 決定最終的type, componentType
			if (isArray()) {
				this.componentType = typeOfClassname;
				this.type = (Class<T>) java.lang.reflect.Array.newInstance(typeOfClassname, 0).getClass();
			} else {
				this.componentType = null;
				this.type = (Class<T>) typeOfClassname;
			}
		}
		else {
			this.arrayTypePrefix = null;
			this.typeDescriptor = null;
			this.classname = null;
			this.hashCode = null;
			this.componentType = null;
			this.type = null;
			throw new IllegalArgumentException(doubleQuoteString(s) + "is not a correct type signature expression");
		}
	}
	
	public static TypeSignatureExpression of(CharSequence expr) { return new TypeSignatureExpression(expr); }
	
	/**
	 * 判斷是否為array, collection, map
	 */
	public boolean isMultiElementType() {
		return this.type.isArray()
				|| Collection.class.isAssignableFrom(this.type)
				|| Map.class.isAssignableFrom(this.type)
				;
	}
	
	public boolean isCollection() { return isCollection(null, null); }
	
	public boolean isCollection(Class<? extends Collection> collectionType) { return isCollection(collectionType, null); }
	
	public boolean isCollection(Class<? extends Collection> collectionType, Consumer<Class<? extends Collection>> then) {
		if (Collection.class.isAssignableFrom(this.type)) {
			// 不深究具體的型別
			if (collectionType == null)
				return true;
			
			if (!collectionType.isAssignableFrom(this.type))
				return false;
			
			if (then != null)
				try {
					then.accept((Class<? extends Collection>) this.type);
				} catch (ClassCastException e) { /* skip */ }
			
			return true;
		}
		return false;
	}
	
	public boolean isArray() { return isArray(null); }
	
	public boolean isArray(Class<?> arrayType) {
		if (this.arrayTypePrefix != null && this.arrayTypePrefix.startsWith("[")) {
			if (arrayType != null)
				return arrayType.isAssignableFrom(this.type);
			return true;
		}
		return false;
	}

	public boolean isMap() { return Map.class.isAssignableFrom(this.type); }
	
	public boolean isMap(Class<?> mapClass) { return isMap(mapClass, null); }
	
	public boolean isMap(Class<?> mapClass, Consumer<Class<? extends Map>> then) {
		// 不深究具體的型別
		if (Map.class.isAssignableFrom(this.type)) {
			if (mapClass == null)
				return true;
			
			if (!mapClass.isAssignableFrom(this.type))
				return false;
			
			if (then != null)
				try {
					then.accept((Class<? extends Map>) this.type);
				} catch (ClassCastException e) { /* skip */ }
			
			return true;
		}
		return false;
	}
	
	public int getDimension() {
		if (this.arrayTypePrefix != null)
			return this.arrayTypePrefix.length();
		return -1;
	}
	
	public boolean isPrimitive() { return this.type.isPrimitive(); }

	public boolean isReferenceType() { return "L".equals(this.typeDescriptor); }
	
	public String getTypeName() {
		if (this.type != null)
			return this.type.getName();
		throw new IllegalStateException("this.type is not supposed to be null");
	}
	
	public Class<?> getType() { return this.type; }
	
	public Class<?> getComponentType() { return this.componentType; }
	
	public String toString() { return toString(null); }
	
	public String toString(Character packageSeparator) {
		return this.expression; 
	}
}

package cathay.coindeskApi.commons.enums;

import static cathay.coindeskApi.commons.util.StringUtils.doubleQuoteString;
import static cathay.coindeskApi.commons.util.regex.Patterns.*;
import static cathay.coindeskApi.commons.util.regex.RegExUtils.matches;

import java.util.regex.Pattern;

public enum ClassNameFormat {
	
	INTERNAL {
		public String format(String classname) {
            return classname.replace('.', '/');
        }
		
		public String getClassname(String typeSignatureExpr) {
			return null;
		}
		
		public Class<?> getType(String expr) {
			return null;
		}
	},
	
	BINARY {
        public String format(String classname) {
            return classname.replace('/', '.');
        }
        
        public String getClassname(String typeSignatureExpr) {
			return null;
		}
		
		public Class<?> getType(String expr) {
			return null;
		}
	};
	
	public abstract String format(String classname);
	
	public abstract String getClassname(String expr);
	
	/**
	 * @param expr 支援: classname , type signature expression
	 */
	public abstract Class<?> getType(String expr);
	
	public static void main(String[] args) {
		ClassNameFormat x = parse("[I");
		System.out.println("x = " + x);
	}
	
	public static ClassNameFormat parse(String expr) {
		/*
		 * Type Signature expression
		 * FQCN binary name
		 * FQCN Internal name
		 */
		return matches(PATTERN_FQCN, expr,
		  // success
		  (matcher) -> BINARY,
		  // else
		  (matcher) -> {
			  if (Pattern.compile(PATTERN_FQCN_INTERNAL).matcher(expr).matches())
				  return INTERNAL;
			  throw new IllegalArgumentException("Illegal INTERNAL NAME format of a classname: " + doubleQuoteString(expr));
		});
	}
}

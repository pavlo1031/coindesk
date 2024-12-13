package cathay.coindeskApi.commons.logic;

import static cathay.coindeskApi.commons.util.TypeUtils.isLamdaType;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.Getter;

@Getter
public enum LogicOp {
	// 不做運算, 信號直接通過
	PASS(true),
	
	NOT(true) {
		
	},
	
	AND {
	
	},
	
	OR {
		
	},
	
	XOR {
		
	},
	
	NAND {
		
	},

	NOR {
		
	};
	
	public static void main(String[] args) {
		// Predefined constraints
		final Predicate<Object> checkNonNull = (data) -> data != null;
		final Predicate<String> checkNonEmpty = (s) -> s != null && s.length() > 0;
		
		/*
		Predicate<Integer> opNot = NOT.<Integer>operate((Object x) -> x+1 == 2);
		{
			System.out.println(opNot.test(5));        // !false --> true
			System.out.println(opNot.test(0));        // !false --> true
			System.out.println(opNot.test(1) + "\n"); // !true --> false
		}
		
		Predicate<Integer> opPass = PASS.<Integer>operate((x) -> x+1 == 2);
		{
			System.out.println(opPass.test(5));
			System.out.println(opPass.test(0));
		}
		*/

//		NOT.operate(checkNonNull, checkNonEmpty);
//		
//		AND.operate(checkNonNull);
	}
	
	private final boolean acceptingSingleInput;
	
	private LogicOp() { this.acceptingSingleInput = false; }
	
	private LogicOp(boolean acceptsSingleInput) { this.acceptingSingleInput = acceptsSingleInput;}

	
	// (Pass, Not)
	public <T> Predicate<? super T> operate(Predicate<? super T> predicate) {
		if (!this.acceptingSingleInput) {
			// TODO:
			throw new UnsupportedOperationException("It's an operator that takes multiple inputs, please call operate(Predicate[] condition).");
		}
		
		// TODO:
		return predicate;
	}
	
	public <T> Predicate<? super T> operate(Predicate<? super T>... predicates) {
		if (this.acceptingSingleInput) {
			// TODO:
			throw new UnsupportedOperationException("It's an unary-operator, please call operate(Predicate<T> condition). Only the operations that accept 2 (or more) inputs can call this method. ");
		}
		
		// TODO:
		return null;
	}
	
	<T> Predicate<?> compute(Predicate<?> input) {
		return null;
	}
	
	<T> Predicate<?>[] compute(Predicate<?>[] inputs) {
		return null;
	}
	
	private <T, InputType, ReturnType> ReturnType compute(Class<InputType> InputType, Predicate<T>[] inputs, Function<Predicate<T>[], ReturnType> implementor) {
		//if (this.acceptingSingleInput) {
		//	return (ReturnType) implementor.apply(inputs);
		//}
		
		if (Predicate[].class.isAssignableFrom(InputType)) {
			
		}
		else if (Predicate.class.isAssignableFrom(InputType)) {
			
		}
		
		return (ReturnType) implementor.<Predicate[], Predicate>apply(inputs);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private void checkParamters(Predicate<?>... predicates) {
		if (this.acceptingSingleInput) {
			if (predicates.length > 1)
				throw new IllegalArgumentException("Too many predicates: This operator accepts only 1 input");
		} else {
			if (predicates.length < 2)
				throw new IllegalArgumentException("Too few predicates: This operator accepts at least 2 inputs.");
		}
	}
	
	private void checkLamdaType(Object lamda, String argName) {
		if (!isLamdaType(lamda.getClass()))
			throw new IllegalArgumentException("The argument '" + argName + "' must be a lamda expression (whose type is annotated with @FunctionalInterface)");
	}
}

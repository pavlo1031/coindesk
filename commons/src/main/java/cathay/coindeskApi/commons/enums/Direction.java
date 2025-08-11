package cathay.coindeskApi.commons.enums;

import java.util.Iterator;

import cathay.coindeskApi.commons.util.ArrayUtils;

public enum Direction {
	
	Forward {
		public <T> Iterable<T> iterable(Object array) { return ArrayUtils.Iteration.forwardOrder(array); }
		public <T> Iterator<T> iterator(Object array) { return ArrayUtils.Iteration.forwardOrderIterator(array); }
	},
	
	Reverse {
		public <T> Iterable<T> iterable(Object array) { return ArrayUtils.Iteration.reverseOrder(array); }
		public <T> Iterator<T> iterator(Object array) { return ArrayUtils.Iteration.reverseOrderIterator(array); }
	};
	
	public abstract <T> Iterator<T> iterator(Object array);
	
	public abstract <T> Iterable<T> iterable(Object array);
}

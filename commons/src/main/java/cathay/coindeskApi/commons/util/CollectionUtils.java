package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.enums.Direction.*;
import static cathay.coindeskApi.commons.util.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cathay.coindeskApi.commons.enums.Direction;
import cathay.coindeskApi.commons.util.function.NoArgConsumer;

public class CollectionUtils {

	public static class Iteration {
		
		public static <T> Iterable<T> forwardOrder(List<T> list) {
			Objects.requireNonNull(list, "The argument 'list' cannot be null");
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return createListIterator(list, Forward);
				}
			};
		}
		
		public static <T> Iterable<T> reverseOrder(List<T> list) {
			Objects.requireNonNull(list, "The argument 'list' cannot be null");
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return createListIterator(list, Reverse);
				}
			};
		}
		
		public static <T> Iterator<T> forwardOrderIterator(List<T> list) {
			Objects.requireNonNull(list, "The argument 'array' cannot be null");
			return createListIterator(list, Forward);
		}
		 
		public static <T> Iterator<T> reverseOrderIterator(List<T> list) {
			Objects.requireNonNull(list, "The argument 'array' cannot be null");
			return createListIterator(list, Reverse);
		}
		
		private static <T> Iterator<T> createListIterator(List<T> list, Direction direction) {
			Objects.requireNonNull(list, "The argument 'list' cannot be null");
			final int length = list.size();
			return new Iterator<T>() {
				int position = (Forward == direction)? 0 : length;
				public boolean hasNext() {
					return (Forward == direction)
							? position < length
							: position > 0;
				}
				public T next() {
					return (Forward == direction)
							? list.get(position++)
							: list.get(--position);
				}
			};
		}
	}
	
	public static <T, U> List<U> map(T[] source, Function<T, U> mapper) {
		return map(Arrays.asList(source), null, mapper);
	}
	
	public static <T, U> List<U> map(Collection<T> source, Function<T, U> mapper) {
		return map(source, null, mapper);
	}
	
	public static <T, U> List<U> map(T[] source, Predicate<T> filter, Function<T, U> mapper) {
		return map(Arrays.asList(source), filter, mapper);
	}
	
	public static <C extends Collection<U>, T, U> C map(Collection<T> source, Predicate<T> filter, Function<T, U> mapper) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		if (mapper == null)
			throw new NullPointerException("參數mapper不可為空");
		
		Collection<U> newCollectionInst = null;		
		try {
			newCollectionInst = source.getClass().newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			if (source instanceof List) {
				newCollectionInst = new ArrayList<U>();
			}
			else if (source instanceof Set) {
				newCollectionInst = new HashSet<U>();
			}
			else if (source instanceof Queue) {
				newCollectionInst = (C) new ArrayBlockingQueue<U>(source.size());
			}
		}
		
		for (T inElem : source) {
			if (filter != null && !filter.test(inElem))
				continue;
			
			U outElem = null;
			outElem = mapper.apply(inElem);
			newCollectionInst.add(outElem);
		}
		return (C) newCollectionInst;
	}
	
	public static <C extends Collection<T>, T> C filter(T[] source, Predicate<T> predicate) {
		return (C) filter(Arrays.asList(source), predicate);
	}
	
	public static <C extends Collection<T>, T> C filter(C source, Predicate<T> predicate) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		
		C newCollectionInst = null;
		try {
			newCollectionInst = ((Class<C>) source.getClass()).newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			if (source instanceof List) {
				newCollectionInst = (C) new ArrayList<T>();
			}
			else if (source instanceof Set) {
				newCollectionInst = (C) new HashSet<T>();
			}
			else if (source instanceof Queue) {
				newCollectionInst = (C) new ArrayBlockingQueue<T>(source.size());
			}
		}
		
		for (T element : source) {
			if (predicate != null && predicate.test(element)) {
				 newCollectionInst.add(element);
			}
		}
		return newCollectionInst;
	}
	
	/**
	 * 支援collection對陣列的addAll
	 * 
	 * @param values 待加入的元素
	 */
	public static <C extends Collection<E>, E> boolean addAll(C collection, E... values) {
		return collection.addAll(Arrays.asList(values));
	}
	
	/**
	 * 支援collection對陣列的addAll
	 * 
	 * @param values 待加入的元素
	 */
	public static <C extends Collection<E>, T, E> boolean addAll(C collection, Function<T, E> mapper, T... values) {
		List<E> list = Arrays.stream(values).map(mapper).collect(Collectors.toList());
		return collection.addAll(list);
	}
	
	/**
	 * 支援collection對陣列的containsAll
	 * 
	 * @param values 待檢查「是否存在於集合」的元素
	 */
	public static <C extends Collection<?>, E> boolean containsAll(C c, E... values) {
		return c.containsAll(Arrays.asList(values));
	}
	
	/**
	 * 支援collection對陣列的removeAll
	 * 
	 * @param values 待刪除的元素
	 */
	public static <C extends Collection<E>, E> boolean removeAll(C collection, E... values) {
		return collection.removeAll(Arrays.asList(values));
	}
	
	/**
	 * 支援collection對陣列的retainAll
	 * 
	 * @param values 刪除全部的元素, 僅留下values
	 */
	public static <C extends Collection<E>, E> boolean retainAll(C collection, E... values) {
		return collection.retainAll(Arrays.asList(values));
	}
	
	/**
	 * 判斷是否包含元素, 提供onComplete callback
	 */
	public static <C extends Collection<T>, T> boolean contains(C collection, T elem) {
		return contains(collection, elem, null, null);
	}
	
	/**
	 * @param hitCallback 有找到元素時 會呼叫的callback
	 */
	public static <C extends Collection<T>, T> boolean contains(C collection, T elem, Consumer<? extends T> hitCallback) {
		return contains(collection, elem, hitCallback, null);
	}
	
	/**
	 * @param missCallback "未"找到元素時 會呼叫的callback
	 */
	public static <C extends Collection<T>, T> boolean contains(C collection, T elem, NoArgConsumer missCallback) {
		return contains(collection, elem, null, missCallback);
	}
	
	/**
	 * @param hitCallback 有找到元素時 會呼叫的callback
	 * @param missCallback "未"找到元素時 會呼叫的callback
	 */
	public static <C extends Collection<T>, T> boolean contains(C collection, T elem, Consumer<? extends T> hitCallback, NoArgConsumer missCallback) {
		Objects.requireNonNull(collection, "The argument 'collection' cannot be null");
		if (collection.isEmpty()) {
			if (missCallback != null)
				missCallback.accept();
			return false;
		}
		
		boolean found = false;
		if (found = collection.contains(elem)) {
			if (hitCallback != null) ((Consumer<T>) hitCallback).accept(elem);
		} else {
			if (missCallback != null) missCallback.accept();
		}
		return found;
	}
	
	
	/*
	 * 這版本可能不需要:
	 * 當初會有這method, 就是為了支援stream ➜ 讓stream操作最後不用自己呼叫collect()
	 */
	public static <T> List<T> toList(Stream<? extends T> stream) {
		return stream.collect(Collectors.toList());
	}

	/*
	 * 這版本可能不需要:
	 * 當初會有這method, 就是為了支援stream ➜ 並讓stream操作最後不用自己呼叫collect()
	 */
	public static <T, U> List<T> toList(Stream<? extends T> stream, Predicate<T> predicate) {
		return stream.filter(predicate).collect(Collectors.toList());
	}
	
	
	public static <T, U> List<U> toList(T[] source) {
		return toList(source, (Class<U>) null, (Predicate<T>) null, (Consumer<U>) null);
	}
	
	public static <T, U> List<U> toList(T[] source, Predicate<T> filter) {
		return toList(source, (Class<U>) null, filter, (Consumer<U>) null);
	}
	
	public static <T, U> List<U> toList(T[] source, Consumer<U> forEachNewElement) {
		return toList(source, (Class<U>) null, (Predicate<T>) null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(T[] source, Predicate<T> filter, Consumer<U> forEachNewElement) {
		return toList(source, (Class<U>) null, filter, forEachNewElement);
	}
	
	
	public static <T, U> List<U> toList(Collection<T> source) {
		return toList(source, (Class<U>) null, (Predicate<T>) null, (Consumer<U>) null);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Consumer<U> forEachNewElement) {
		return toList(source, (Class<U>) null, (Predicate<T>) null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Predicate<T> filter) {
		return toList(source, (Class<U>) null, filter, (Consumer<U>) null);
	}
	
	
	
	public static <T, U> List<U> toList(T[] source, Class<U> elementClass) {
		return toList(source, elementClass, null, null);
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> elementClass, Predicate<T> filter) {
		return toList(source, elementClass, filter, null);
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> elementClass, Consumer<U> forEachNewElement) {
		return toList(source, elementClass, (Predicate<T>) null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> elementClass, Predicate<T> filter, Consumer<U> forEachNewElement) {
		return toList(Arrays.asList(source), elementClass, filter, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> elementClass) {
		return toList(source, elementClass, null, null);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> elementClass, Consumer<U> forEachNewElement) {
		return toList(source, elementClass, null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> elementClass, Predicate<T> filter) {
		return toList(source, elementClass, filter, null);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> elementClass, Predicate<T> filter, Consumer<U> forEachNewElement) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		
		ArrayList<U> targetList = new ArrayList<U>();
		for (T element : source) {
			if (filter != null && !filter.test(element))
				continue;
			
			U newInst = null;
			try {
				if (element == null) {
					targetList.add(null);
				}
				else {
					// 預設作法: 建立一空的target物件, 以"直接複製"的方式映射值
					newInst = elementClass.newInstance();
					// TODO: 注意「同名, 不同型別」
					copyProperties(element, newInst);
					
					// add to the result list
					targetList.add(newInst);
					
					if (forEachNewElement != null)
						forEachNewElement.accept(newInst);
				}
			}
			catch (Exception e) {}
		}
		
		return (List<U>) targetList;
	}
}

package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {

	public static <K, V, U> Map<K, V> toMap(Collection<V> source, Function<V, K> keySupplier) {
		return toMap(source, keySupplier, (Function<V, V>) null, (Predicate<V>) null);
	}
	
	public static <K, V, U> Map<K, U> toMap(Collection<V> source, Function<V, K> keySupplier, Function<V, U> valueSupplier) {
		return toMap(source, keySupplier, valueSupplier, null);
	}
	
	public static <K, V, U> Map<K, U> toMap(Collection<V> source, Function<V, K> keySupplier, Function<V, U> valueSupplier, Predicate<V> filter) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		
		if (keySupplier == null)
			throw new NullPointerException("參數keySupplier不可為空");
		
		// help index the value in the 'source' collection
		ArrayList<V> sourceList = new ArrayList<V>(source);
		
		HashMap<K, U> map = new HashMap<K, U>();
		for (int i=0; i<sourceList.size(); i++) {
			V element = sourceList.get(i);
			if (filter != null && filter.test(element)) {
				continue;
			}
			
			K key = keySupplier.apply(element);
			
			U value = null;
			if (valueSupplier != null)
				value = valueSupplier.apply(element);
			else {
				value = (U)element;
			}
			map.put(key, value);
		}
		return map;
	}
	
	public static <T, U> List<U> map(Collection<T> source, Function<T, U> mapper) {
		return map(source, null, mapper);
	}
	
	public static <T, U> List<U> map(Collection<T> source, Predicate<T> filter, Function<T, U> mapper) {
		if (mapper == null)
			throw new NullPointerException("參數mapper不可為空");
		
		// help index the value in the 'source' collection
		ArrayList<T> sourceList = new ArrayList<T>(source);
		
		ArrayList<U> list = new ArrayList<U>();
		for (int i=0; i < sourceList.size(); i++) {
			T inElem = sourceList.get(i);
			if (filter != null && !filter.test(inElem))
				continue;
			
			U outElem = null;
			outElem = mapper.apply(inElem);
			list.add(outElem);
		}
		return list;
	}
	
	public static <T> List<T> filter(T[] source, Predicate<T> predicate) {
		return filter(Arrays.asList(source), predicate);
	}
	
	@SuppressWarnings("unchecked")
	public static <C extends Collection<T>, T> C filter(C source, Predicate<T> predicate) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		
		// 預設使用的容器類型
		Class<C> collectionType = (Class<C>) source.getClass();
		C newCollectionInst = null;
		try {
			newCollectionInst = collectionType.newInstance();
		}
		catch (Exception e) {
			// 若預設使用的容器類型為"無法實體化"類型
			// --> 改用ArrayList裝載結果
			newCollectionInst = (C) new ArrayList<T>();
		}
		finally {
			for (T element : source) {
				if (predicate.test(element)) {
					 newCollectionInst.add(element);
				}
			}
		}
		return newCollectionInst;
	}
	
	public static <T> List<T> toList(Stream<? extends T> stream) {
		return stream.collect(Collectors.toList());
	}
	
	public static <T, U> List<T> toList(Stream<? extends T> stream, Predicate<T> predicate) {
		return stream.filter(predicate).collect(Collectors.toList());
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> targetClass) {
		return toList(Arrays.asList(source), targetClass, null, null);
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> targetClass, Predicate<T> filter) {
		return toList(Arrays.asList(source), targetClass, filter);
	}
	
	public static <T, U> List<U> toList(T[] source, Class<U> targetClass, Consumer<U> forEachNewElement) {
		return toList(Arrays.asList(source), targetClass, null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> targetClass) {
		return toList(source, targetClass, null, null);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> targetClass, Consumer<U> forEachNewElement) {
		return toList(source, targetClass, null, forEachNewElement);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> targetClass, Predicate<T> filter) {
		return toList(source, targetClass, filter, null);
	}
	
	public static <T, U> List<U> toList(Collection<T> source, Class<U> targetClass, Predicate<T> filter, Consumer<U> forEachNewElement) {
		if (source == null)
			throw new NullPointerException("來源集合物件source不可為空");
		
		// help index the value in the 'source' collection
		ArrayList<T> sourceList = new ArrayList<T>(source);
		
		ArrayList<U> targetList = new ArrayList<U>();
		for (int i=0; i<sourceList.size(); i++) {
			T element = sourceList.get(i);
			if (filter != null && !filter.test(element))
				continue;
			
			U newInst = null;
			try {
				if (element == null) {
					targetList.add(null);
				}
				else {
					// 預設作法: 建立一空的target物件, 以"直接複製"的方式映射值
					newInst = targetClass.newInstance();
					// TODO: 注意「同名, 不同型別」
					copyProperties(element, newInst);
					
					if (forEachNewElement != null)
						forEachNewElement.accept(newInst);
					targetList.add(newInst);
				}
			}
			catch (Exception e) {}
		}
		
		return (List<U>) targetList;
	}	
}

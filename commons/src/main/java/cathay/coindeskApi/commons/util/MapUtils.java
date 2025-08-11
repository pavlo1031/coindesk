package cathay.coindeskApi.commons.util;

import static cathay.coindeskApi.commons.util.ArrayUtils.existsIn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapUtils {

	private static Class<?> ClassUnmodifiableMap;

	private static final Class<?>[] MapTypesNotAllowingNullValue = new Class<?>[] {
		java.util.Hashtable.class, java.util.concurrent.ConcurrentHashMap.class
	};
	
	/**
	 * 高階函數都定義在此處
	 */
	public static class Hoc {
		/**
		 * primitive array
		 */
		public static <T> IntFunction<Entry<Integer, T>> createEntry(IntFunction<T> valueSupplier) {
			Objects.requireNonNull(valueSupplier, "The argument 'valueSupplier' cannot be null");
			return (int index) -> {
				return Map.<Integer, T>entry(index, valueSupplier.apply(index));
			};
		}
	}
	
	/**
	 * 兩兩一組視作key-value pair的一個entry
	 *  (key和value各自的型別, 是由第一個entry決定)
	 *  
	 * @throws IllegalArgumentException elements元素個數小於2, 或不為偶數時
	 */
	public static <K, V> Map<K, V> of(Object...elements) {
		return of(LinkedHashMap<K,V>::new, elements);
	}
	
	public static <K, V> Map<K, V> of(Collection<?> elements) {
		return of(LinkedHashMap<K,V>::new, elements.toArray());
	}
	
	/**
	 * 兩兩一組視作key-value pair的一個entry
	 *  (key和value各自的型別, 是由第一個entry決定)
	 *  
	 * @param keyType 明確指定key型別
	 * @param valueType 明確指定value型別
	 * @param elements
	 * @throws IllegalArgumentException elements元素個數小於2, 或不為偶數時
	 */
	public static <K, V> Map<K, V> of(Class<K> keyType, Class<V> valueType, Object...elements) {
		return of(LinkedHashMap<K,V>::new, keyType, valueType, elements);
	}
	
	public static <K, V> Map<K, V> of(Class<K> keyType, Class<V> valueType, Collection<?> elements) {
		return of(LinkedHashMap<K,V>::new, keyType, valueType, elements.toArray());
	}
	
	/**
	 * 兩兩一組視作key-value pair的一個entry
	 *  (key和value各自的型別, 是由第一個entry決定)
	 *  
	 * @param supplier 決定map容器的型別
	 * @param keyType 明確指定key型別
	 * @param valueType 明確指定value型別
	 * @param elements
	 * @throws IllegalArgumentException elements元素個數小於2, 或不為偶數時
	 */
	public static <K, V> Map<K, V> of(Supplier<? extends Map<K, V>> supplier, Object...elements) {
		return of(LinkedHashMap<K,V>::new, (Class<K>) null, (Class<V>) null, elements);
	}
	
	/**
	 * 兩兩一組視作key-value pair的一個entry
	 *  (key和value各自的型別, 是由第一個entry決定)
	 * 
	 * @param supplier 決定map容器的型別
	 * @param keyType 明確指定key型別
	 * @param valueType 明確指定value型別
	 * @param elements
	 * @throws IllegalArgumentException elements元素個數小於2, 或不為偶數時
	 */
	public static <K, V> Map<K, V> of(Supplier<? extends Map<? extends K, ? extends V>> supplier, Class<K> keyType, Class<V> valueType, Object...elements) {
		if (elements.length < 2)
			throw new IllegalArgumentException("元素個數至少有2個.");
		if (elements.length % 2 != 0)
			throw new IllegalArgumentException("元素個數必須為偶數, 兩個元素為一組, 分別為key和value.");
		
		// determine key and value types
		keyType = (keyType != null)
				  ? keyType : (Class<K>) elements[0].getClass();
		valueType = (valueType != null)
					? valueType : (Class<V>) elements[1].getClass();
		
		// Create map container
		Map<K, V> map = ((Supplier<? extends Map<K, V>>) supplier).get();
		
		for (int i=0; i<elements.length; i+=2) {
			// key
			K key = (K) elements[i];
			if (key != null) {
				if (!keyType.isAssignableFrom(key.getClass())) {
					System.out.println("[WARN] elements[" + i + "]: " + "key型別必須為" + keyType.getSimpleName() + "，或其子類別");
					continue;
				}
			}
			
			// value
			V value = (V) elements[i+1];
			if (value != null) {
				if (!valueType.isAssignableFrom(value.getClass())) {
					System.out.println("[WARN] elements[" + (i+1) + "]: " + "value型別必須為" + valueType.getSimpleName() + "，或其子類別");
					continue;
				}
			}
			
			map.put(key, value);
		}
		return (Map<K, V>) map;
	}
	
	public static <K,V> boolean isUnmodifiableMap(Map<K,V> map) {
		// argument null check
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		
		// first-time loading
		if (ClassUnmodifiableMap == null) {
			try {
				ClassUnmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Internal Error: cannot check unmodifiability, failed to load the class 'java.util.Collections$UnmodifiableMap'");
			}	
		}
		
		boolean isUnmodifiable = false;
		if (ClassUnmodifiableMap.isAssignableFrom(map.getClass())) {
			isUnmodifiable = true;
		}
		/*
		else {
			// 測試: 使用待操作資料
			Object key = "testKey", value = "testData";
			
			// protect resource
			synchronized (map) {
				try {
					// 資料插入測試: 容器不拋出錯誤 "unsupported operation":
					// put, putAll, putIfAbsent
					// replace, replaceAll
					// remove
					// clear
					// compute, computeIfPresent, computeIfAbsent
					// merge(K, V, callback)
					((Map<Object, Object>) map).put(key, value);
				}
				catch (UnsupportedOperationException e) {
					isUnmodifiable = true;
				}
				catch (Exception e) {
					// 其他錯誤, 測試中斷: 視為此物件無法執行容器操作
				}
				finally {
					// 還原: 移除測試資料
					map.remove(key);
				}
			}
		}
		*/
		return isUnmodifiable;
	}
	
	/**
	 * 透過key取得Entry (而不是value)
	 * 
	 * @return 鍵值key對映的entry<k,v>
	 */
	public static <K, V> Entry<K,V> getEntry(Map<K,V> map, K key) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		Map<K, Entry<K, V>> entryMap = map.entrySet().stream().collect(
		  Collectors.<Entry<K, V>, K, Entry<K, V>>
			toMap((entry) -> entry.getKey(),
			      (entry) -> entry));
		return entryMap.get(key);
	}
	
	/**
	 * 透過index取得Entry
	 * 
	 * @return 位置index上的entry<k,v>
	 * @throws IndexOutOfBoundsException index < 0 或 index >= map.size()時
	 */
	public static <K, V> Entry<K,V> getEntryAt(Map<K,V> map, int index) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		return map.entrySet().stream().collect(Collectors.toList()).get(index);
	}
	
	
	/**
	 * TODO: 可傳參數給建構函數
	 */
	public static <K, V> V get(Map<K, V> map, K key, Class<? extends V> valueClass, Class<?>... constructorParamTypes) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		V value = null;
		if (!map.containsKey(key)) {
			value = NewInstanceUtils.newInstance(valueClass);
			map.put(key, value);
		}
		return value;
	}
	
	/**
	 * 如果key不存在, 會將預設的值加入為: entry<key, defaultValue>
	 */
	public static <K, V> V get(Map<K, V> map, K key, V defaultValue) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		if (!map.containsKey(key)) {
			if (defaultValue == null) {
				try {
					return getOrPutKeyIfAbsent(map, key);
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("The argument 'defaultValue' cannot be null when the map is 'Hashtable' or 'ConcurrentHashMap'");
				}
			}
			map.put(key, defaultValue);
		}
		return defaultValue;
	}
	
	/**
	 * 如果key不存在, 會加入null值: entry<key, null>
	 */
	public static <K, V> V getOrPutKeyIfAbsent(Map<K, V> map, K key) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		Objects.requireNonNull(key, "The argument 'key' cannot be null");
		// 如果存在, 直接傳回
		if (map.containsKey(key))
			return map.get(key);
		
		if (existsIn(map.getClass(), MapTypesNotAllowingNullValue))
			throw new IllegalArgumentException("Null value is not allowed when the map is 'Hashtable' or 'ConcurrentHashMap'");
		
		map.put(key, null);
		return null;
	}
	
	/**
	 * 將candidateValues的第一個非空值, 插入map
	 */
	public static <K,V> V putNonNull(Map<K,V> map, K key, V...candidateValues) {
		// null check
		Objects.requireNonNull(map);
		
		// 需要改以firstNonNull完成? (取第一個非空元素)
		V lastValue = null;
		for (V value : candidateValues) {
			if (value != null) {
				synchronized (map) {
					lastValue = map.put(key, value);
				}
				break;
			}
		}
		return lastValue;
	}
	
	/**
	 * putFirst 將candidateValues的第一個非空值, 插入map的第一個位置
	 */
	// FIXME: SequencedMap是jdk高版本元件
	//public static <K,V> V putNonNullAtFirst(SequencedMap<K,V> map, K key, V...candidateValues) {
	//	// null check
	//	Objects.requireNonNull(map, "The argument 'map' cannot be null");
	//			
	//	// 需要改以firstNonNull完成? (取第一個非空元素)
	//	V oldValue = null;
	//	for (V value : candidateValues) {
	//		if (value != null) {
	//			synchronized (map) {
	//				oldValue = map.putFirst(key, value);
	//			}
	//			break;
	//		}
	//	}
	//	return oldValue;
	//}
	
	/**
	 * putLast 將candidateValues的第一個非空值, 插入map的最後一個位置
	 */
	//public static <K,V> V putNonNullAtLast(SequencedMap<K,V> map, K key, V...candidateValues) {
	//	// null check
	//	Objects.requireNonNull(map, "The argument 'map' cannot be null");
	//			
	//	// 需要改以firstNonNull完成? (取第一個非空元素)
	//	V oldValue = null;
	//	for (V value : candidateValues) {
	//		if (value != null) {
	//			synchronized (map) {
	//				oldValue = map.putLast(key, value);	
	//			}
	//			break;
	//		}
	//	}
	//	return oldValue;
	//}
	
	/**
	 * 在指定位置插入key, value
	 *  (不主動維護map插入順序性, 請使用者謹慎考慮)
	 */
	public static <K,V> Map<K,V> put(Map<K,V> map, int index, K key, V value) {
		// null check
		Objects.requireNonNull(map);
		
		LinkedList<Entry<K, V>> entryList = new LinkedList<Map.Entry<K,V>>(map.entrySet());
		entryList.add(
			index, // at the given position
			Map.entry(key, value)
		);
		
		if (isUnmodifiableMap(map))
			throw new UnsupportedOperationException("Cannot add entry to an unmodifiable map.");
		
		synchronized (map) {
			// 先清空, 重新加入新的entryList
			map.clear();
			return putAll(map, entryList);	
		}
	}
	
	public static <K,V> Map<K,V> putAll(Map<K,V> map, Collection<Entry<K, V>> entrySet) {
		// null check
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		
		// The map could be 'unmodifiable'
		if (isUnmodifiableMap(map))
			throw new UnsupportedOperationException("Cannot add to an unmodifiable map.");
		
		synchronized (map) {
			for (Entry<K, V> entry : entrySet)
				map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	/**
	 * <pre>
	 * 以特定方式排序Map元素。
	 * 若map本身是LinkedHashMap, 此操作會影響參數map; 否則會建立一新map裝載排序好的元素
	 * </pre>
	 */
	public static <K, V> Map<K, V> sort(Map<K, V> map, Comparator<? super Entry<? extends K, ? extends V>> comparator) {
		Objects.requireNonNull(map, "The argument 'map' cannot be null");
		Objects.requireNonNull(comparator, "The argument 'comparator' cannot be null");
		
		// keep the entries in advance 
		ArrayList<Entry<K, V>> entries = new ArrayList<>(map.entrySet());
		
		// perform sort
		entries.sort(comparator);
		
		// reset
		if (map instanceof LinkedHashMap)
			map.clear();
		else
			map = new LinkedHashMap<>();
		
		// re-populate the entries
		for (Entry<K, V> entry : entries)
			map.put(entry.getKey(), entry.getValue());
		return map;
	}
	
	public static <K, V, U> Map<K, V> toMap(V[] source, Function<V, K> keySupplier) {
		return toMap(Arrays.asList(source), keySupplier, (Function<V, V>) null, (Predicate<V>) null);
	}
	
	public static <K, V, U> Map<K, U> toMap(V[] source, Function<V, K> keySupplier, Function<V, U> valueSupplier) {
		return toMap(Arrays.asList(source), keySupplier, valueSupplier, null);
	}
	
	public static <K, V, U> Map<K, U> toMap(V[] source, Function<V, K> keySupplier, Function<V, U> valueSupplier, Predicate<V> filter) {
		return toMap(Arrays.asList(source), keySupplier, valueSupplier, filter);
	}
	
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
		
		HashMap<K, U> map = new HashMap<K, U>();
		for (V element : source) {
			if (filter != null && filter.test(element))
				continue;
			
			K key = keySupplier.apply(element);
			U value = null;
			if (valueSupplier != null)
				value = valueSupplier.apply(element);
			else {
				value = (U) element;
			}
			map.put(key, value);
		}
		return map;
	}
}

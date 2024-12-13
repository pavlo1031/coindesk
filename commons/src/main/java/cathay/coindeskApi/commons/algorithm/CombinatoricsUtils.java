package cathay.coindeskApi.commons.algorithm;

import static cathay.coindeskApi.commons.util.validate.ValidationUtils.and;
import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.isNotNull;
import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.numberGreaterThan;
import static cathay.coindeskApi.commons.util.validate.Hoc.Validator.sizeGreaterThan;
import static cathay.coindeskApi.commons.util.validate.ValidationUtils.checkCondition;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import cathay.coindeskApi.commons.util.function.HeptaConsumer;
import cathay.coindeskApi.commons.util.function.HexaConsumer;
import cathay.coindeskApi.commons.util.function.OctoConsumer;
import cathay.coindeskApi.commons.util.function.PentaConsumer;
import cathay.coindeskApi.commons.util.function.QuadConsumer;
import cathay.coindeskApi.commons.util.function.TriConsumer;
import cathay.coindeskApi.commons.util.function.UnaryConsumer;

public class CombinatoricsUtils {
	/**
     * 產生所有排列
     */
	public static <T> Result<T> permutations(int pickCount, T... poolElemens) {
		return permutations(pickCount, Arrays.asList(poolElemens));
	}
	
	/**
     * 產生所有排列
     */
    public static <T> Result<T> permutations(int pickCount, List<T> pool) {
    	// check arguments
    	checkCondition(pickCount, numberGreaterThan(0), "參數pickCount必須為大於0");
    	checkCondition(pool, and(isNotNull("poolElemens"), sizeGreaterThan(0)), "參數poolElemens不可為空");
    	
        List<List<T>> results = new ArrayList<>();
        backtrack(pickCount,
        		  pool,
        		  new ArrayList<>(),        // current
            	  0,                        // start index
            	  new boolean[pool.size()],
            	  results,
            	  Mode.PERMUTATION);
        return new Result<T>(pickCount, results);
    }
    
    /**
     * 產生所有組合
     */
    public static <T> Result<T> combinations(int pickCount, T... poolElemens) {
		return combinations(pickCount, Arrays.asList(poolElemens));
	}
    
    /**
     * 產生所有組合
     */
    public static <T> Result<T> combinations(int pickCount, List<T> pool) {
    	// check arguments
    	checkCondition(pickCount, numberGreaterThan(0), "參數pickCount必須為大於0");
    	checkCondition(pool, and(isNotNull("pool"), sizeGreaterThan(0)), "參數pool不可為空");
    	
        List<List<T>> results = new ArrayList<>();
        backtrack(pickCount,
        		  pool,
        		  new ArrayList<>(), // current
	        	  0,                 // start index
	        	  null,              // "不需"用到used
	        	  results,
	        	  Mode.COMBINATION);
        return new Result<T>(pickCount, results);
    }

	
	private enum Mode {
		PERMUTATION, COMBINATION
	}

    /**
     * 通用回溯演算法 (Backtracking)，用來產生排列 (Permutation) 或組合 (Combination)。
     *
     * @param pool       待排列之樣本空間 (來源集合)
     * @param current    當前已選取的元素序列 (逐步構建中)
     * @param used       標記哪些元素已經被使用過 (僅在排列模式下需要；組合模式下為 null)
     * @param pickCount  最終要選取的元素數量 (目標長度)
     * @param results    收集所有最終結果的容器
     * @param mode       模式：PERMUTATION 或 COMBINATION
     * @param startIndex 組合模式下，下一次迭代從哪個位置開始 (避免重複)；
     *                   排列模式下可忽略 (因為會嘗試所有位置)
     *
     * 演算法邏輯：
     * 1. 如果 current 已經達到 pickCount，表示找到一個完整解 → 加入 results。
     * 2. 如果是 PERMUTATION：
     *      - 從 pool 的每個元素嘗試加入 current。
     *      - 跳過已經使用過的元素 (靠 used[] 判斷)。
     *      - 遞迴下去繼續選取，直到長度達標。
     *      - 回溯 (移除最後一個元素、還原 used 狀態)。
     * 3. 如果是 COMBINATION：
     *      - 從 startIndex 開始往後挑選元素 (確保不重複、也避免順序不同的重複組合)。
     *      - 每選一個元素就往下遞迴，並把起始點更新為下一個位置。
     *      - 回溯 (移除最後一個元素)。
     */
    private static <T> void backtrack(int pickCount, List<T> pool, List<T> current, int startIndex, boolean[] used, List<List<T>> results, Mode mode) {
    	// 基底條件：已經選到指定數量 → 保存結果
    	if (current.size() == pickCount) {
            results.add(new ArrayList<>(current));
            return;
        }

    	if (Mode.PERMUTATION == mode) {
            // 排列：要考慮元素順序，因此每個位置都能嘗試
            for (int i = 0; i < pool.size(); i++) {
                if (used[i]) continue; // 已經用過就跳過
                used[i] = true;        // 標記為已使用
                current.add(pool.get(i));

                backtrack(pickCount, pool, current,
	              		  0, // start index
	              		  used,
	              		  results,
	              		  Mode.PERMUTATION);
                
                // 回溯：撤銷選擇
                current.remove(current.size() - 1);
                used[i] = false;
            }
        }
    	else if (Mode.COMBINATION == mode) {
            // 組合：不考慮順序，只能往後挑選，避免重複
            for (int i = startIndex; i < pool.size(); i++) {
                current.add(pool.get(i));

                backtrack(pickCount, pool, current,
                		  i+1,  // start index
                		  null, // 組合不需用used
                		  results,
                		  Mode.COMBINATION);

                // 回溯：撤銷選擇
                current.remove(current.size() - 1);
            }
        }
    }
    
    public static class Result<T> {
    	
    	private int pickSize;
    	
    	private List<List<T>> results;

    	private Map<Object, List<T>> resultsMappings = new HashMap<Object, List<T>>();
    	
    	private Class<?> solutionIdType;
    	
    	private Function<List<T>, Object> solutionIdSupplier;
    	
    	private Result(List<List<T>> results) {
    		this(-1, results, (solution) -> true, null);
		}
    	
    	private Result(int pickSize, List<List<T>> results) {
    		this(-1, results, (solution) -> true, null);
    	}
    	
    	private <K> Result(int pickSize, List<List<T>> results, Function<List<T>, K> solutionIdSupplier) {
    		this(-1, results, (solution) -> true, solutionIdSupplier);
    	}
    	
    	private <K> Result(int pickSize, List<List<T>> results, Predicate<List<T>> includingSolution, Function<List<T>, K> solutionIdSupplier) {
    		for (List<T> solution : results) {
    			if (pickSize == -1)
    				pickSize = solution.size();
    			else {
    				if (solution.size() != pickSize)
        				throw new IllegalStateException("一組解的元素數不一致: 僅能為" + pickSize + ", 但實際一組解的長度為" + solution.size());
    			}
    		}
    		this.pickSize = pickSize;
    		this.results = results.stream().filter(includingSolution).toList();
    		this.resultsMappings = results.stream().filter(includingSolution)
    									   .collect(toMap(
    											// key
	    										(solution) -> {
	    											K id = solutionIdSupplier.apply(solution);
	    											if (this.solutionIdType == null)
	    												this.solutionIdType = id.getClass();
	    											return id;
	    										},
	    										// value
	    										(solution) -> solution)
    									   );
    	}
    	
    	public Stream<List<T>> stream() {
    		return this.results.stream();
    	}
    	
    	public List<List<T>> get(){
    		return (List<List<T>>) this.results;
    	}
    	
    	public Result<T> include(Predicate<List<T>> includingSolution) {
    		this.results = this.results.stream().filter(includingSolution).toList();
    		this.resultsMappings = this.results.stream().filter(includingSolution)
								   .collect(toMap(
										// key
										(solution) -> {
											Object id = solutionIdSupplier.apply(solution);
											if (this.solutionIdType == null)
												this.solutionIdType = id.getClass();
											return id;
										},
										// value
										(solution) -> solution)
								   );
    		return this;
    	}
    	
    	public Result<T> exclude(Predicate<List<T>> excludingSolution) {
    		return include(excludingSolution.negate());
    	}
    	
    	public Result<T> forEach(Consumer<List<T>> each) {
    		for (List<T> solution : this.results)
    			each.accept(solution);
    		return this;
    	}
    	
    	public Result<T> forEachSolution(UnaryConsumer<T> each) {
    		if (pickSize != 1)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(BiConsumer<T, T> each) {
    		if (pickSize != 2)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(TriConsumer<T, T, T> each) {
    		if (pickSize != 3)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(QuadConsumer<T, T, T, T> each) {
    		if (pickSize != 4)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2), solution.get(3));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(PentaConsumer<T, T, T, T, T> each) {
    		if (pickSize != 5)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2), solution.get(3));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(HexaConsumer<T, T, T, T, T, T> each) {
    		if (pickSize != 6)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2), solution.get(3), solution.get(4), solution.get(5));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(HeptaConsumer<T, T, T, T, T, T, T> each) {
    		if (pickSize != 7)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數的callback");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2), solution.get(3), solution.get(4), solution.get(5), solution.get(6));
    		return this;
    	}
    	
    	public Result<T> forEachSolution(OctoConsumer<T, T, T, T, T, T, T, T> each) {
    		if (pickSize != 8)
    			throw new IllegalArgumentException("forEachSolution(): callback參數量不符, 僅能為" + pickSize + "個參數的callback");
    		
    		for (List<T> solution : this.results)
    			each.accept(solution.get(0), solution.get(1), solution.get(2), solution.get(3), solution.get(4), solution.get(5), solution.get(6), solution.get(7));
    		return this;
    	}
    }
}

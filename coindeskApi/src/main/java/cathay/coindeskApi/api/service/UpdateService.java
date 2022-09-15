package cathay.coindeskApi.api.service;

import static cathay.coindeskApi.util.BatchUpdate.updateFieldValues;
import static cathay.coindeskApi.util.StringUtil.doubleQuoteString;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;

import cathay.coindeskApi.api.entity.CoinType;
import cathay.coindeskApi.util.BatchUpdate;
import cathay.coindeskApi.util.ModelFieldSupport;

/**
 * 未來未提供 "多種" entity type的資料更新功能
 */
public class UpdateService<IdType, EntityType extends ModelFieldSupport<EntityType, FieldType>,
	FieldType extends Enum<FieldType>, RepositoryType extends JpaRepository<EntityType, IdType>> {

	private RepositoryType repository;
	
	public UpdateService(RepositoryType repository) {
		this.repository = repository;
	}
	
	/**
	 * 單一欄位
	 */
	public Integer update(IdType id, FieldType field, Object value) {
		return (Integer) update(id, (EntityType) null, updateFieldValues(field, value), false);
	}
	
	/**
	 * 單一欄位
	 */
	public CoinType updateAndGet(IdType id, FieldType field, Object value) {
		return (CoinType) update(id, (EntityType) null, updateFieldValues(field, value), true);
	}
	
	/**
	 * 單一欄位 (接收現有entity)
	 */
	public Integer update(IdType id, EntityType entity, FieldType field, Object value) {
		return (Integer) update(id, entity, updateFieldValues(field, value), false);
	}
	
	/**
	 * 單一欄位 (接收現有entity)
	 */
	public CoinType updateAndGet(IdType id, EntityType entity, FieldType field, Object value) {
		return (CoinType) update(id, entity, updateFieldValues(field, value), true);
	}
	
	/**
	 * 更多欄位
	 */
	public Integer update(IdType id, BatchUpdate<FieldType> batchUpdate) {
		return (Integer) update(id, (EntityType) null, batchUpdate, false);
	}
	
	/**
	 * 多欄位
	 */
	public CoinType updateAndGet(IdType id, BatchUpdate<FieldType> batchUpdate) {
		return (CoinType) update(id, (EntityType) null, batchUpdate, true);
	}
	
	/**
	 * 更多欄位
	 */
	public Integer update(IdType id, EntityType entity, BatchUpdate<FieldType> batchUpdate) {
		return (Integer) update(id, entity, batchUpdate, false);
	}
	
	/**
	 * 多欄位 (接收現有entity)
	 */
	public CoinType updateAndGet(IdType id, EntityType entity, BatchUpdate<FieldType> batchUpdate) {
		return (CoinType) update(id, entity, batchUpdate, true);
	}
	
	/**
	 * 多欄位 (接收現有entity)
	 */
	protected Object update(IdType id, EntityType entity, BatchUpdate<FieldType> batchUpdate, boolean get) {
		return update(id, entity, batchUpdate, get, null);
	}
	
	/**
	 * 更新多欄位, 並提供 "寫入資料之前" 一個可以處理資料的執行點
	 */
	protected Object update(IdType id, EntityType entity, BatchUpdate<FieldType> batchUpdate, boolean get,
			Consumer<EntityType> beforeWriteAction) {
		
		Object returnVal = null;
		EntityType entity_ = null;
		
		// 1. find
		if (entity == null) {
			// 未指定現成entity, 從資料庫find
			entity_ = repository.findById(id).orElse(null);
			if (entity_== null) {
				List<String> fields = batchUpdate.keySet().stream().map((FieldType f) -> f.name()).collect(Collectors.toList());
				throw new IllegalStateException("該筆資料不存在 (coinCode = " + doubleQuoteString(id) + "), " +
					"無法更新" + String.join(", ", fields) + "欄位");
			}
		}
		else {
			entity_ = entity;
		}
		
		Throwable error = null;
		try {
			// set value
			int writeFieldsCount = batchUpdate.size();
			for (Map.Entry<FieldType, Object> entry : batchUpdate.entrySet()) {
				final FieldType key = entry.getKey();
				final Object val = entry.getValue();
				// check data change
				if (entity_.get(key).equals(val)) {
					batchUpdate.setUpdated(key, false);
					writeFieldsCount--;
					continue;
				}
				batchUpdate.setUpdated(key, true);
				entity_.set(entry.getKey(), entry.getValue());
			}
			
			if (writeFieldsCount == 0)
				return returnVal = (get)? entity_ : 0;
			
			if (beforeWriteAction != null)
				beforeWriteAction.accept(entity_);
			
			// write
			entity_ = repository.save(entity_);
			returnVal = (get)? entity_ : 1;
			
			return returnVal;
		}
		catch (Throwable t) {
			error = t;
			// 正在構想如何處理錯誤?
			// (以及是否要在此處處理)
			throw t;
		}
	}
}

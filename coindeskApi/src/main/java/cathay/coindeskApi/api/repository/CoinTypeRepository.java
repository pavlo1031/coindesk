package cathay.coindeskApi.api.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String>{

	Optional<CoinType> findByCode(String code);
	
	@Modifying
	@Query("update CoinType ct set ct.symbol = ?2, ct.updated = now() where ct.code = ?1")
	int updateSymbol(String coinCode, String description);
	
	@Modifying
	@Query("update CoinType ct set ct.description = ?2 where ct.code = ?1")
	int updateDescription(String coinCode, String description);
	
	@Modifying
	@Query("update CoinType ct set ct.description = ?2, ct.descriptionChinese = ?3 where ct.code = ?1")
	int updateDescription(String coinCode, String description, String description_ch);
	
	@Modifying
	@Query("update CoinType ct set ct.rate = ?2, ct.rateFloat = ?3 where ct.code = ?1")
	int updateRate(String coinCode, String rate, BigDecimal rateFloat);
}
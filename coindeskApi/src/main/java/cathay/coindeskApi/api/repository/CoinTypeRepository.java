package cathay.coindeskApi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String> {
	
	@Query("SELECT coinCode FROM CoinType c")
	List<String> findCoinCodes();
	
	@Modifying
	@Transactional
	@Query("delete from CoinType c where c.coinCode in :ids")
	int deleteAllByIds(@Param("ids") Iterable<String> ids);
}
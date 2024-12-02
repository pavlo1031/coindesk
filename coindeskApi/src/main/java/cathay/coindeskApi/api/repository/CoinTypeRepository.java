package cathay.coindeskApi.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String> {
	
	@Query("SELECT coinCode FROM CoinType c")
	List<String> findCoinCodes();
}
package cathay.coindeskApi.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String> {

	List<CoinType> findAll();
	
	Page<CoinType> findAll(Pageable page);
}
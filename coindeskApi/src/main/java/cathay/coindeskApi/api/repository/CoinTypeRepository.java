package cathay.coindeskApi.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String>{

	Optional<CoinType> findByCode(String code);
}
package cathay.coindeskApi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cathay.coindeskApi.api.entity.CoinType;

@Repository
public interface CoinTypeRepository extends JpaRepository<CoinType, String> {

}
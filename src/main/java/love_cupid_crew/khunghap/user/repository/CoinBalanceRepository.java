package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.CoinBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinBalanceRepository extends JpaRepository<CoinBalance, Long> {
}
package semigg.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import semigg.semi.domain.Stats;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT s FROM Stats s ORDER BY " +
            "CASE s.tier " +
            "  WHEN 'CHALLENGER' THEN 1 " +
            "  WHEN 'GRANDMASTER' THEN 2 " +
            "  WHEN 'MASTER' THEN 3 " +
            "  WHEN 'DIAMOND' THEN 4 " +
            "  WHEN 'PLATINUM' THEN 5 " +
            "  WHEN 'GOLD' THEN 6 " +
            "  WHEN 'SILVER' THEN 7 " +
            "  WHEN 'BRONZE' THEN 8 " +
            "  WHEN 'IRON' THEN 9 " +
            "  ELSE 10 END, " +
            "s.leaguePoints DESC")

    List<Stats> findAllOrderedByRank();
}

package semigg.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import semigg.semi.domain.RiotAccount;
import semigg.semi.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiotAccountRepository extends JpaRepository<RiotAccount, Long> {

    // ✅ 특정 유저의 모든 계정 조회 (본계정 + 부계정)
    List<RiotAccount> findByUserId(Long userId);


    List<RiotAccount> findAllByUser(User user);

    // ✅ 특정 유저의 본계정 1개만 조회
    Optional<RiotAccount> findByUserIdAndIsMainAccountTrue(Long userId);

    @Query("SELECT r FROM RiotAccount r JOIN FETCH r.user WHERE r.isMainAccount = true")
    List<RiotAccount> findAllByMainAccountTrueWithUser();
    // ✅ 리더보드용: 모든 유저의 본계정만 정렬해서 조회
    @Query("""
        SELECT r FROM RiotAccount r
        JOIN FETCH r.user
        WHERE r.isMainAccount = true
        ORDER BY 
          CASE r.tier
            WHEN 'CHALLENGER' THEN 1
            WHEN 'GRANDMASTER' THEN 2
            WHEN 'MASTER' THEN 3
            WHEN 'DIAMOND' THEN 4
            WHEN 'EMERALD' THEN 5
            WHEN 'PLATINUM' THEN 6
            WHEN 'GOLD' THEN 7
            WHEN 'SILVER' THEN 8
            WHEN 'BRONZE' THEN 9
            WHEN 'IRON' THEN 10
            ELSE 11 END,
          r.lp DESC
    """)
    List<RiotAccount> findAllMainAccountsOrdered();

    boolean existsByUserIdAndIsMainAccountTrue(Long userId);

    // ✅ 소환사명+태그로 계정 찾기 (중복 방지용 등)
    Optional<RiotAccount> findBySummonerNameAndTagLine(String summonerName, String tagLine);
}

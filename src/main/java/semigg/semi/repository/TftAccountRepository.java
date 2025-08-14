package semigg.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import semigg.semi.domain.tft.TftAccount;

import java.util.List;
import java.util.Optional;

public interface TftAccountRepository extends JpaRepository<TftAccount, Long> {

    // 유저별 전체 TFT 계정
    List<TftAccount> findByUserId(Long userId);

    // 본계정 존재 여부
    boolean existsByUserIdAndIsMainAccountTrue(Long userId);

    // 유저의 본계정 1개
    List<TftAccount> findTopByUserIdAndIsMainAccountTrue();

    List<TftAccount> findAllByIsMainAccountTrueOrderByLpDesc();

    // 소환사 이름 + 태그로 조회
    Optional<TftAccount> findBySummonerNameAndTagLine(String summonerName, String tagLine);

    Optional<TftAccount>findTopByUserIdAndIsMainAccountTrueOrderByIdDesc(Long userId);
    // (만약 필드가 있다면) puuid로 조회
    Optional<TftAccount> findByPuuid(String puuid);

    // 유저의 특정 계정 삭제
    void deleteByUserIdAndId(Long userId, Long accountId);

    Optional<TftAccount> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("update TftAccount a set a.isMainAccount = false where a.user.id = :userId")
    void clearMainByUserId(Long userId);
}
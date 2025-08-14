package semigg.semi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semigg.semi.domain.lol.League;

import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findByUserId(Long userId);

    // 연관 객체 User의 필드명을 반영해서 수정
    Optional<League> findByUserMainSummonerNameAndUserTagLine(String mainSummonerName, String tagLine);
}
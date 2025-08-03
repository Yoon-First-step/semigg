package semigg.semi.repository;


import org.springframework.stereotype.Repository;
import semigg.semi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인 (중복 체크 등)
    boolean existsByEmail(String email);

    Optional<User> findByMainSummonerNameAndTagLine(String mainSummonerName, String tagLine);
}

package semigg.semi.repository;


import semigg.semi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 학번으로 사용자 조회 (필요 시)
    Optional<User> findByStudentId(String studentId);

    // 이메일 존재 여부 확인 (중복 체크 등)
    boolean existsByEmail(String email);
}

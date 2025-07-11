package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.League;
import semigg.semi.domain.User;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.LeagueResponseDto;
import semigg.semi.repository.LeagueRepository;
import semigg.semi.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final UserRepository userRepository;

    /**
     * 특정 유저의 리그 정보 조회
     */
    public LeagueResponseDto findByUserId(Long userId) {
        League league = leagueRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("리그 정보를 찾을 수 없습니다."));
        return LeagueResponseDto.fromEntity(league);
    }

    /**
     * 리그 정보 저장 또는 업데이트
     */
    @Transactional
    public LeagueResponseDto saveOrUpdateLeague(Long userId, LeagueDto leagueDto) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 기존 리그 데이터가 있으면 업데이트, 없으면 새로 생성
        League league = leagueRepository.findByUserId(userId)
                .orElseGet(() -> League.builder().user(user).build());

        // 3. 정보 갱신
        league.updateInfo(leagueDto); // ← dto 기반 도메인 로직 호출

        // 4. 저장 (save는 update + insert 둘 다 처리)
        League savedLeague = leagueRepository.save(league);

        // 5. 응답 DTO 반환
        return LeagueResponseDto.fromEntity(savedLeague);
    }

}

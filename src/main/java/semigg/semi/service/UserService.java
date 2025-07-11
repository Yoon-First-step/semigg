package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.League;
import semigg.semi.domain.User;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.UserRequestDto;
import semigg.semi.dto.UserResponseDto;
import semigg.semi.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RiotApiService riotApiService;

    /**
     * 회원가입
     */
    @Transactional
    public Long register(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // riotId에서 소환사이름과 태그라인 분리
        String[] parts = dto.getRiotId().split("#");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Riot ID 형식이 잘못되었습니다. 예: 'Hide on bush#KR1'");
        }
        String summonerName = parts[0];
        String tagLine = parts[1];

        User user = User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .studentId(dto.getStudentId())
                .mainSummonerName(summonerName)
                .tagLine(tagLine)
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    /**
     * 이메일로 사용자 조회
     */
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다."));
        return UserResponseDto.fromEntity(user);
    }

    /**
     * ID로 사용자 조회
     */
    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.fromEntity(user);
    }

    /**
     * 유저 정보 + 리그 수동 갱신
     */
    @Transactional
    public void refreshUserAndLeague(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Riot ID 기반으로 조회
        String summonerName = user.getMainSummonerName();
        String tagLine = user.getTagLine();

        semigg.semi.dto.SummonerDto summoner = riotApiService.getSummonerByRiotId(summonerName, tagLine);
        user.updateSummonerInfo(summoner.getName(), summoner.getTagLine());

        LeagueDto leagueDto = riotApiService.getLeagueBySummonerId(summoner.getId());

        League league = user.getLeague();
        if (league == null) {
            league = League.builder()
                    .tier(leagueDto.getTier())
                    .rank(leagueDto.getRank())
                    .leaguePoints(leagueDto.getLeaguePoints())
                    .user(user)
                    .build();
            user.setLeague(league);
        } else {
            league.updateInfo(leagueDto);
        }
    }
}

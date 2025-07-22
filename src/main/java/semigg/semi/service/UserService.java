package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.League;
import semigg.semi.domain.User;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.SummonerDto;
import semigg.semi.dto.UserRequestDto;
import semigg.semi.dto.UserResponseDto;
import semigg.semi.repository.UserRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RiotApiService riotApiService;
    private final EmailService emailService;

    /**
     * 회원가입
     */
    @Transactional
    public Long register(UserRequestDto dto, String verificationCode) {
        try {
            log.info("회원가입 요청: email={}, name={}, studentId={}, mainSummonerName={}, tagLine={}",
                    dto.getEmail(), dto.getName(), dto.getStudentId(), dto.getMainSummonerName(), dto.getTagLine());

            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            //인증코드 완료 부분따로 설정
            boolean isVerified = emailService.verifyCode(dto.getEmail(), verificationCode);
            if (!isVerified) {
                throw new IllegalArgumentException("이메일 인증 코드가 올바르지 않거나 만료되었습니다.");
            }

            User user = User.builder()
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .studentId(dto.getStudentId())
                    .mainSummonerName(dto.getMainSummonerName())
                    .tagLine(dto.getTagLine())
                    .build();

            User savedUser = userRepository.save(user);
            log.info("회원가입 완료, id={}", savedUser.getId());
            return savedUser.getId();
        } catch (Exception e) {
            log.error("회원가입 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
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
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. Riot API - 소환사 기본 정보 조회
        String summonerName = user.getMainSummonerName();
        String tagLine = user.getTagLine();

        SummonerDto summonerDto = riotApiService.getSummonerByRiotId(summonerName, tagLine);
        user.updateSummonerInfo(summonerDto.getName(), summonerDto.getTagLine());

        // 3. Riot API - 리그 정보 조회
        //LeagueDto leagueDto = riotApiService.getLeagueBySummonerId(summonerDto.getId());

        // 4. 리그 정보 갱신
        Optional<LeagueDto> leagueOpt = riotApiService.getLeagueBySummonerId(summonerDto.getId());

        if (leagueOpt.isPresent()) {
            LeagueDto leagueDto = leagueOpt.get();
            League league = user.getLeague();
            if (league == null) {
                league = League.builder()
                        .tier(leagueDto.getTier())
                        .rank(leagueDto.getRank())
                        .leaguePoints(leagueDto.getLeaguePoints())
                        .wins(leagueDto.getWins())
                        .losses(leagueDto.getLosses())
                        .queueType(leagueDto.getQueueType())
                        .user(user)
                        .build();
                user.setLeague(league);
            } else {
                league.updateLeagueInfo(
                        leagueDto.getTier(),
                        leagueDto.getRank(),
                        leagueDto.getLeaguePoints(),
                        leagueDto.getWins(),
                        leagueDto.getLosses(),
                        leagueDto.getQueueType()
                );
            }
        } else {
            log.info("리그 정보 없음: 아직 랭크 게임을 하지 않았을 수 있음.");
        }
    }
}

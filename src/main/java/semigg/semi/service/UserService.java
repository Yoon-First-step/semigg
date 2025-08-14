package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.config.JwtTokenProvider;
import semigg.semi.domain.lol.League;
import semigg.semi.domain.User;
import semigg.semi.dto.*;
import semigg.semi.dto.LolDto.LeagueDto;
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

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 회원가입
     */
    @Transactional
    public Long register(UserRequestDto dto, String verificationCode) {
        try {
            log.info("회원가입 요청: email={}, name={}, studentId={}",
                    dto.getEmail(), dto.getName(), dto.getStudentId());

            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            boolean isVerified = emailService.verifyCode(dto.getEmail(), verificationCode);
            if (!isVerified) {
                throw new IllegalArgumentException("이메일 인증 코드가 올바르지 않거나 만료되었습니다.");
            }

            // 🔒 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(dto.getPassword());

            User user = User.builder()
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .studentId(dto.getStudentId())
                    .password(encodedPassword) // 비밀번호 저장
                    .build();

            User savedUser = userRepository.save(user);
            log.info("회원가입 완료, id={}", savedUser.getId());
            return savedUser.getId();
        } catch (Exception e) {
            log.error("회원가입 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 발급 or 인증 완료 처리
        return jwtTokenProvider.generateToken(user.getId(), user.getEmail());
    }

    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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

    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 중복 이메일 확인
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.updateEmail(newEmail); // 엔티티에 해당 메서드 있어야 함
    }

    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 비밀번호 확인 (비교 방식은 실제 인코딩 방식에 따라 달라짐)
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedNewPassword); // 엔티티 메서드 필요
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
    }

}

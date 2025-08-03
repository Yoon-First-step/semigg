package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.RiotAccount;
import semigg.semi.domain.User;
import semigg.semi.dto.*;
import semigg.semi.repository.RiotAccountRepository;
import semigg.semi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiotAccountService {

    private final RiotAccountRepository riotAccountRepository;
    private final UserRepository userRepository;
    private final RiotApiService riotApiService;

    //계정 등록 서비스
    @Transactional
    public RiotAccount registerAccount(Long userId, RegisterAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 🔍 1. Riot API 호출
        RiotApiResponse api = riotApiService
                .getRiotApiResponse(request.getSummonerName(), request.getTagLine())
                .orElseThrow(() -> new IllegalArgumentException("소환사 정보를 찾을 수 없습니다."));

        // ✅ 2. 본계정 여부 확인
        boolean hasMain = riotAccountRepository.existsByUserIdAndIsMainAccountTrue(userId);
        // ✅ 3. 엔티티 생성
        SummonerLeagueInfo league = api.getLeagueInfo();
        SummonerProfileDto profile = api.getProfile();

        RiotAccount account = new RiotAccount(
                user,
                api.getSummonerName(),
                api.getTagLine(),
                league.getTier(),
                league.getRank(),
                league.getLp(),
                league.getWins(),
                league.getLosses(),
                league.getMainPosition(),
                !hasMain,
                profile.getProfileIconId(),
                LocalDateTime.now()
        );

        // ✅ 4. 저장 및 반환
        return riotAccountRepository.save(account);
    }

    // ✅ 유저의 Riot 계정 목록 조회
    public List<RiotAccountDto> getAccountsByUser(Long userId) {
        return riotAccountRepository.findByUserId(userId).stream()
                .map(RiotAccountDto::from)
                .toList();
    }

    // ✅ 본계정 변경
    @Transactional
    public void updateMainAccount(Long userId, Long newMainAccountId) {
        List<RiotAccount> accounts = riotAccountRepository.findByUserId(userId);

        for (RiotAccount account : accounts) {
            account.changeMainAccount(account.getId().equals(newMainAccountId));
        }
    }

    // ✅ 전적 업데이트
    @Transactional
    public void updateStats(Long accountId, RiotAccountDto updatedStats) {
        RiotAccount account = riotAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));

        account.updateStats(
                updatedStats.tier(),
                updatedStats.rank(),
                updatedStats.lp(),
                updatedStats.wins(),
                updatedStats.losses()
        );
    }

    public List<RiotAccountDto> getRiotAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<RiotAccount> accounts = riotAccountRepository.findAllByUser(user);

        return accounts.stream()
                .map(account -> RiotAccountDto.builder()
                        .summonerName(account.getSummonerName())
                        .tagLine(account.getTagLine())
                        .tier(account.getTier())
                        .lp(account.getLp())
                        .mainPosition(account.getMainPosition())
                        .isMainAccount(account.isMainAccount())
                        .build()
                ).toList();
    }
}
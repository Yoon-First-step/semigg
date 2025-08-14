package semigg.semi.service.lol;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.lol.LolAccount;
import semigg.semi.domain.User;
import semigg.semi.domain.lol.MostChampion;
import semigg.semi.dto.*;
import semigg.semi.dto.LolDto.LeaderboardEntryDto;
import semigg.semi.dto.LolDto.RiotAccountDto;
import semigg.semi.repository.LolAccountRepository;
import semigg.semi.repository.UserRepository;
import semigg.semi.service.RiotApiService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LolAccountService {

    private final LolAccountRepository riotAccountRepository;
    private final UserRepository userRepository;
    private final RiotApiService riotApiService;
    private final LolAccountRepository lolAccountRepository;
    private final LolStatsService lolStatsService;

    //계정 등록 서비스
    @Transactional
    public LolAccount registerAccount(Long userId, RegisterAccountRequest request) {

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

        LolAccount account = new LolAccount(
                user,
                api.getSummonerName(),
                api.getTagLine(),
                profile.getProfileIconId(),
                !hasMain,
                league.getTier(),
                league.getRank(),
                league.getLp(),
                league.getWins(),
                league.getLosses(),
                league.getMainPosition(),
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
        List<LolAccount> accounts = riotAccountRepository.findByUserId(userId);

        for (LolAccount account : accounts) {
            account.changeMainAccount(account.getId().equals(newMainAccountId));

        }
    }

    // ✅ 전적 업데이트
    @Transactional
    public void updateStats(Long accountId, RiotAccountDto updatedStats) {
        LolAccount account = riotAccountRepository.findById(accountId)
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

        List<LolAccount> accounts = riotAccountRepository.findAllByUser(user);

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

    @Transactional
    public void refreshAccountStats(Long accountId, String puuid) {
        LolAccount acc = lolAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));

        // 1) 통계 계산
        String mainPosition = lolStatsService.getMainPosition(puuid);  // positions→main 계산
        List<String> top3 = lolStatsService.getMostChampions(puuid, 3); // 챔피언 top3

        // 2) 엔티티 반영
        acc.updateMainPosition(mainPosition); // LolAccount에 세터/도메인 메서드 하나 두세요

        // MostChampion 컬렉션 갱신 (orphanRemoval=true 가정)
        acc.getMostChampions().clear();
        for (String name : top3) {
            acc.getMostChampions().add(new MostChampion(name, acc));
        }
        // LolAccountRepository는 변경 감지로 flush
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard(Long userId) {
        // 예: 상위 LP 순
        List<LolAccount> accounts = lolAccountRepository.findTopByUserIdAndIsMainAccountTrueOrderByCreatedAtDesc(userId);
        AtomicInteger rank = new AtomicInteger(1);

        return accounts.stream()
                .map(a -> LeaderboardEntryDto.builder()
                        .rank(rank.getAndIncrement())
                        .name(a.getSummonerName())
                        .riotId(a.getTagLine())
                        .tier(a.getTier())
                        .leaguePoints(a.getLp())
                        .mainPosition(a.getMainPosition())
                        .winRate(a.getWinRate())
                        .profileIconId(a.getProfileIconId())
                        .mostChampions(
                                a.getMostChampions().stream()
                                        .map(MostChampion::getChampionName)
                                        .toList()
                        )
                        .build())
                .toList();
    }
}
package semigg.semi.service.tft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semigg.semi.domain.User;
import semigg.semi.domain.tft.TftAccount;
import semigg.semi.dto.*;
import semigg.semi.dto.TftDto.*;
import semigg.semi.repository.TftAccountRepository;
import semigg.semi.repository.UserRepository;
import semigg.semi.service.RiotApiService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TftAccountService {

    private final TftStatsService tftStatsService;
    private final TftAccountRepository tftAccountRepository;

    private final RiotApiService riotApiService;

    private final UserRepository userRepository;

    /**
     * 계정 등록
     */
    public TftAccount registerAccount(Long userId, RegisterAccountRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1) 소환사 & PUUID
        var summoner = riotApiService.getSummonerByNameAndTag(req.getSummonerName(), req.getTagLine());
        String puuid = riotApiService.getSummonerByRiotId(req.getSummonerName(), req.getTagLine()).getPuuid(); // or 별도 fetchPuuidByRiotId

        // 2) 현재 랭크 정보 & 통계 스냅샷
        TftRankDto rank = tftStatsService.getCurrentRank(puuid);               // tier, rank, lp
        TftStatsSnapshot s = tftStatsService.buildStatsSnapshot(puuid);        // totalGames, wins, top4, winRate, top4Rate, avgPlacement

        // 3) 첫 등록이면 본계정
        boolean hasMain = tftAccountRepository.existsByUserIdAndIsMainAccountTrue(userId);


        // 4) 엔티티 생성/저장
        TftAccount account =
                new TftAccount(
                user,
                summoner.getName(),
                req.getTagLine(),
                summoner.getProfileIconId(),
                !hasMain,// 첫 등록이면 본계정
                        0,
                rank.getTier(),
                rank.getRank(),
                rank.getLp(),
                s.getWins(),
                s.getLosses(),
                s.getWinRate(),
                s.getTotalGames(),
                s.getProtectRate(),
                LocalDateTime.now()
        );

        return tftAccountRepository.save(account);
    }

    /**
     * 본계정 변경
     */
    public void changeMainAccount(Long userId, Long accountId) {
        // 모두 false로
        tftAccountRepository.clearMainByUserId(userId);
        // 지정 계정 true
        TftAccount acc = tftAccountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));
        acc.changeMainAccount(true);
    }


    /**
     * 리더보드 (본계정만, LP 내림차순)
     */
    @Transactional(readOnly = true)
    public List<TftLeaderboardEntryDto> getLeaderboard() {
        List<TftAccount> accounts = tftAccountRepository
                .findAllByIsMainAccountTrueOrderByLpDesc();

        AtomicInteger rank = new AtomicInteger(1);

        return accounts.stream()
                .map(a ->TftLeaderboardEntryDto.builder()
                        .rank(rank.getAndIncrement())
                        .name(a.getSummonerName())
                        .riotId(a.getTagLine())
                        .tier(a.getTier())
                        .leaguePoints(a.getLp())
                        .winRate(a.getWinRate())
                        .protectRate(a.getProtectRate())          // TFT용 필드가 있다면 DTO에 추가
                        .profileId(a.getProfileIconId())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public TftProfileCardDto getProfileCardByUser(Long userId) {
        User user = getUserOrThrow(userId);

        TftAccount acc = tftAccountRepository
                .findTopByUserIdAndIsMainAccountTrueOrderByIdDesc(userId)
                .orElseThrow(() -> new IllegalStateException("본계정(TFT)이 등록되어 있지 않습니다."));

        return buildProfileCard(user, acc);
    }

    @Transactional(readOnly = true)
    public TftProfileCardDto getProfileCardByAccount(Long userId, Long accountId) {
        User user = getUserOrThrow(userId);

        TftAccount acc = tftAccountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 계정을 찾을 수 없습니다."));

        return buildProfileCard(user, acc);
    }

    private TftProfileCardDto buildProfileCard(User user, TftAccount acc) {
        return TftProfileCardDto.builder()
                .name(user.getName())
                .studentId(user.getStudentId())
                .profileIconId(acc.getProfileIconId())
                .summonerName(acc.getSummonerName())
                .tagLine(acc.getTagLine())
                .tier(acc.getTier() + " " + acc.getRank())
                .build();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<TftProfileCardDto> getAllUserProfileCards() {
        List<TftAccount> accounts = tftAccountRepository.findAllWithUser(); // 사용자 포함 fetch

        return accounts.stream()
                .map(acc -> buildProfileCard(acc.getUser(), acc))
                .toList();
    }


    @Transactional
    public void refreshAccountStats(Long tftAccountId) {
        TftAccount acc = tftAccountRepository.findById(tftAccountId)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));

        TftRankDto r = tftStatsService.getCurrentRank(acc.getPuuid());
        TftComputedStats s = tftStatsService.computeStatsSince(acc.getPuuid(), 20);

        acc.updateTftStats(
                r.getTier(), r.getRank(), r.getLp(),
                s.getWins(), s.getLosses(),s.getPlayCount(),
                s.getWinRate(), s.getProtectRate());
    }


}

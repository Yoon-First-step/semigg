package semigg.semi.service.tft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semigg.semi.domain.tft.TftAccount;
import semigg.semi.dto.TftDto.TftLeaderboardEntryDto;
import semigg.semi.repository.TftAccountRepository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TftLeaderboardService {

    private final TftAccountRepository tftAccountRepository;

    public List<TftLeaderboardEntryDto> getRankedLeaderboard() {
        List<TftAccount> accounts = tftAccountRepository.findTopByUserIdAndIsMainAccountTrue();

        List<TftLeaderboardEntryDto> rawList = accounts.stream()
                .map(account -> TftLeaderboardEntryDto.builder()
                        .profileId(account.getProfileIconId())
                        .name(account.getUser().getName())
                        .studentId(account.getUser().getStudentId())
                        .tier(account.getTier())
                        .leaguePoints(account.getLp())
                        .riotId(account.getSummonerName() + "#" + account.getTagLine())
                        .playCount(account.getPlayCount())
                        .winRate(account.getWinRate()) // getter 계산 포함 가능
                        .protectRate(account.getProtectRate())
                        .build()
                )
                .collect(Collectors.toList());

        // 티어 우선순위 + LP 내림차순으로 정렬
        rawList.sort(Comparator
                .comparing((TftLeaderboardEntryDto dto) -> getTierRank(dto.getTier())).reversed()
                .thenComparing(TftLeaderboardEntryDto::getLeaguePoints, Comparator.reverseOrder())
        );

        // 순위 부여
        AtomicInteger rank = new AtomicInteger(1);
        return rawList.stream()
                .map(dto -> TftLeaderboardEntryDto.builder()
                        .profileId(dto.getProfileId())
                        .rank(rank.getAndIncrement())
                        .name(dto.getName())
                        .studentId(dto.getStudentId())
                        .tier(dto.getTier())
                        .leaguePoints(dto.getLeaguePoints())
                        .riotId(dto.getRiotId())
                        .playCount(dto.getPlayCount())
                        .winRate(dto.getWinRate())
                        .protectRate(dto.getProtectRate())
                        .build())
                .collect(Collectors.toList());
    }

    private int getTierRank(String tier) {
        return switch (tier.toUpperCase()) {
            case "CHALLENGER" -> 9;
            case "GRANDMASTER" -> 8;
            case "MASTER" -> 7;
            case "DIAMOND" -> 6;
            case "EMERALD" -> 5;
            case "PLATINUM" -> 4;
            case "GOLD" -> 3;
            case "SILVER" -> 2;
            case "BRONZE" -> 1;
            case "IRON" -> 0;
            default -> -1;
        };
    }
}

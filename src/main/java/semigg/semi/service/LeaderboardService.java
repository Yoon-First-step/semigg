package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semigg.semi.domain.MostChampion;
import semigg.semi.domain.User;
import semigg.semi.domain.RiotAccount;
import semigg.semi.dto.LeaderboardEntryDto;
import semigg.semi.repository.UserRepository;
import semigg.semi.repository.RiotAccountRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final RiotAccountRepository riotAccountRepository;

    public List<LeaderboardEntryDto> getRankedLeaderboard() {
        List<RiotAccount> accounts = riotAccountRepository.findAllByMainAccountTrueWithUser();

        List<LeaderboardEntryDto> rawList = accounts.stream()
                .map(account -> LeaderboardEntryDto.builder()
                        .name(account.getUser().getName())
                        .studentId(account.getUser().getStudentId())
                        .tier(account.getTier())
                        .leaguePoints(account.getLp())
                        .riotId(account.getSummonerName() + "#" + account.getTagLine())
                        .mainPosition(account.getMainPosition())
                        .winRate(account.getWinRate()) // getter 계산 포함 가능
                        .mostChampions(
                                account.getMostChampions().stream()
                                        .map(MostChampion::getChampionName)
                                        .collect(Collectors.toList()))
                        .build()
                )
                .collect(Collectors.toList());

        // 티어 우선순위 + LP 내림차순으로 정렬
        rawList.sort(Comparator
                .comparing((LeaderboardEntryDto dto) -> getTierRank(dto.getTier())).reversed()
                .thenComparing(LeaderboardEntryDto::getLeaguePoints, Comparator.reverseOrder())
        );

        // 순위 부여
        AtomicInteger rank = new AtomicInteger(1);
        return rawList.stream()
                .map(dto -> LeaderboardEntryDto.builder()
                        .rank(rank.getAndIncrement())
                        .name(dto.getName())
                        .studentId(dto.getStudentId())
                        .tier(dto.getTier())
                        .leaguePoints(dto.getLeaguePoints())
                        .riotId(dto.getRiotId())
                        .mainPosition(dto.getMainPosition())
                        .winRate(dto.getWinRate())
                        .mostChampions(dto.getMostChampions())
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
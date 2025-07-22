package semigg.semi.dto;

import semigg.semi.domain.League;

import java.time.LocalDateTime;

public record LeaderboardDto(
        String nickname,
        String summonerName,
        String tier,
        String rank,
        int lp,
        int wins,
        int losses,
        double winRate
) {
    public static LeaderboardDto from(League stats) {
        int total = stats.getWins() + stats.getLosses();
        double winRate = total == 0 ? 0 : (stats.getWins() * 100.0 / total);
        return new LeaderboardDto(
                stats.getUser().getName(),
                stats.getUser().getMainSummonerName(),
                stats.getTier(),
                stats.getRank(),
                stats.getLeaguePoints(),
                stats.getWins(),
                stats.getLosses(),
                winRate
        );
    }
}
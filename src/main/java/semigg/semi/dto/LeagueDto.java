package semigg.semi.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class LeagueDto {

    private final String queueType;
    private final String tier;
    private final String rank;
    private final int leaguePoints;
    private final int wins;
    private final int losses;
    private final String summonerName;
    private final String summonerId;
    private final String leagueId;

    private final String tagLine;            // 🆕 추가
    private final String mainPosition;       // 🆕 추가
    private final List<String> mostChampions;// 🆕 추가

    @Builder
    public LeagueDto(String queueType, String tier, String rank, int leaguePoints, int wins, int losses,
                     String summonerName, String summonerId, String leagueId,
                     String tagLine, String mainPosition, List<String> mostChampions) {
        this.queueType = queueType;
        this.tier = tier;
        this.rank = rank;
        this.leaguePoints = leaguePoints;
        this.wins = wins;
        this.losses = losses;
        this.summonerName = summonerName;
        this.summonerId = summonerId;
        this.leagueId = leagueId;
        this.tagLine = tagLine;
        this.mainPosition = mainPosition;
        this.mostChampions = mostChampions;
    }

    public double getWinRate() {
        int total = wins + losses;
        return total == 0 ? 0.0 : Math.round((wins * 100.0 / total) * 10) / 10.0;
    }
}
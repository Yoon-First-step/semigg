package semigg.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiotApiResponse {
    private String summonerName;     // ✅ 추가
    private String tagLine;          // ✅ 추가

    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;
    private String mainPosition;
    private List<String> mostChampions;
}
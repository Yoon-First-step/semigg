package semigg.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiotApiResponse {
    private String summonerName;
    private String tagLine;
    private SummonerLeagueInfo leagueInfo;
    private SummonerProfileDto profile;
    private List<String> mostChampions;
}
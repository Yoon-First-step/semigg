package semigg.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SummonerLeagueInfo {
    private String queueType;
    private String tier;
    private String rank;
    private int lp;
    private int wins;
    private int losses;
    private String mainPosition;
}

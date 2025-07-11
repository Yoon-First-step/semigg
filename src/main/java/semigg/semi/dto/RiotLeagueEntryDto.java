package semigg.semi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiotLeagueEntryDto {

    @JsonProperty("leagueId")
    private String leagueId;

    @JsonProperty("queueType")
    private String queueType;

    @JsonProperty("tier")
    private String tier;

    @JsonProperty("rank")
    private String rank;

    @JsonProperty("summonerId")
    private String summonerId;

    @JsonProperty("summonerName")
    private String summonerName;

    @JsonProperty("leaguePoints")
    private int leaguePoints;

    @JsonProperty("wins")
    private int wins;

    @JsonProperty("losses")
    private int losses;

    @JsonProperty("hotStreak")
    private boolean hotStreak;

    @JsonProperty("veteran")
    private boolean veteran;

    @JsonProperty("freshBlood")
    private boolean freshBlood;

    @JsonProperty("inactive")
    private boolean inactive;
}

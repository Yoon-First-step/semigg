package semigg.semi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummonerDto {

    @JsonProperty("id")
    private String id;  // encrypted summonerId

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("puuid")
    private String puuid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("profileIconId")
    private int profileIconId;

    @JsonProperty("revisionDate")
    private long revisionDate;

    @JsonProperty("summonerLevel")
    private long summonerLevel;

    @JsonIgnore
    private String tagLine;
}

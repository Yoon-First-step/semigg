package semigg.semi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueDto {

    private String queueType;       // 예: RANKED_SOLO_5x5
    private String tier;            // 예: GOLD, SILVER
    private String rank;            // 예: I, II, III, IV
    private int leaguePoints;       // 예: 50
    private int wins;               // 승리 수
    private int losses;             // 패배 수
    private String summonerName;    // 소환사 이름
    private String summonerId;      // EncryptedSummonerId
    private String leagueId;        // 리그 ID (optional)
    // 필요시 toString()도 추가 가능
}

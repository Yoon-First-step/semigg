package semigg.semi.dto.LolDto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeaderboardEntryDto {
    private int rank;// 종합 순위
    private int profileIconId;
    private String name;            // 사용자 실명
    private String studentId;       // 학번
    private String tier;            // ex. PLATINUM
    private int leaguePoints;       // LP
    private String riotId;          // ex. hideonbush#KR1
    private String mainPosition;    // ex. MID
    private double winRate;         // ex. 62.5
    private List<String> mostChampions; // ex. ["Ahri", "LeBlanc", "Zed"]
}

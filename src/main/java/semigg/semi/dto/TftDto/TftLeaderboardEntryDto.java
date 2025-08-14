package semigg.semi.dto.TftDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TftLeaderboardEntryDto {
        private int rank;// 종합 순위
        private int profileId;
        private String name;            // 사용자 실명
        private String studentId;       // 학번
        private String tier;            // ex. PLATINUM
        private int leaguePoints;       // LP
        private String riotId;          // ex. hideonbush#KR1
        private int playCount;
        private double winRate;// ex. 62.5
        private double protectRate;
}

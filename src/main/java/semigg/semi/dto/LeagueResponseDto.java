package semigg.semi.dto;

import lombok.*;
import semigg.semi.domain.League;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueResponseDto {

    private Long id;
    private String tier;         // 예: GOLD, PLATINUM
    private String rank;         // 예: I, II, III, IV
    private int leaguePoints;    // 리그 포인트 (LP)
    private Long userId;         // 유저 ID

    public static LeagueResponseDto fromEntity(League league) {
        return LeagueResponseDto.builder()
                .id(league.getId())
                .tier(league.getTier())
                .rank(league.getRank())
                .leaguePoints(league.getLeaguePoints())
                .userId(league.getUser().getId())
                .build();
    }
}

package semigg.semi.dto.TftDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TftStatsSnapshot {
    private final int totalGames;
    private final int wins;
    private final int losses;
    private final double winRate;
    private final double protectRate;
}
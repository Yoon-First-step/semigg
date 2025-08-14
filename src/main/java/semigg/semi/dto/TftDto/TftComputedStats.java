package semigg.semi.dto.TftDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TftComputedStats {
    private final int playCount;
    private final int wins;
    private final int losses;
    private final double winRate;    // %
    private final double protectRate;   // %
}

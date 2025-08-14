package semigg.semi.dto.TftDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TftParticipantDto {

    private String puuid;      // 플레이어 고유 ID
    private int placement;     // 등수 (1~8)
    private int level;         // 플레이어 레벨 (선택)
    private int lastRound;     // 마지막 라운드 (선택)

}
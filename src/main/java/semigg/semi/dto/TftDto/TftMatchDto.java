package semigg.semi.dto.TftDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TftMatchDto {
    private TftInfo info;
    @Getter @Setter
    public static class TftInfo {
        private List<TftParticipantDto> participants;
        private Long game_datetime; // 필요시 시즌/기간 필터링
        private Integer queue_id;   // 필요시 랭크만 필터
    }
}


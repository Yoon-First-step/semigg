package semigg.semi.dto.TftDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TftMatchDto {
    private Info info;

    @Getter
    public static class Info {
        private List<TftParticipantDto> participants;

        @JsonProperty("game_datetime")
        private Long gameDatetime; // 경기 시작 시간 (Epoch millis)

        @JsonProperty("queue_id")
        private Integer queueId;   // 큐 타입 (e.g., 1100 = 랭크 게임)
    }
}
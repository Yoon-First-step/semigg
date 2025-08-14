package semigg.semi.dto.LolDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MatchDto {
    private Info info;

    @Getter
    public static class Info {
        @JsonProperty("gameStartTimestamp")
        private long gameStartTimestamp;

        @JsonProperty("queueId")
        private int queueId;

        private List<Participant> participants;
    }

    @Getter
    public static class Participant {
        private String puuid;
        private String championName;

        @JsonProperty("teamPosition")
        private String teamPosition;
    }
}
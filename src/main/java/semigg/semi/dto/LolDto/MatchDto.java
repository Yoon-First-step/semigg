package semigg.semi.dto.LolDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MatchDto {
    private Info info;

    @Getter
    public static class Info {
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

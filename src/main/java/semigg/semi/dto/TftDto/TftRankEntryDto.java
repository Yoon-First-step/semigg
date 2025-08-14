package semigg.semi.dto.TftDto;

import lombok.Getter;

@Getter
public class TftRankEntryDto { // /tft/league/v1/entries/by-summoner 응답 요소
    private String queueType;     // "RANKED_TFT" | "RANKED_TFT_TURBO"
    private String tier;          // IRON..CHALLENGER
    private String rank;          // I..IV (티어 내 단계)
    private int leaguePoints;
    private int wins;
    private int losses;
}

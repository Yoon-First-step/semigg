package semigg.semi.dto.TftDto;

import lombok.Getter;

@Getter
public class TftSummonerDto {  // /tft/summoner/v1/summoners/by-puuid 응답
    private String id;     // encryptedSummonerId
    private String puuid;
    private String name;
    private int profileIconId;
}

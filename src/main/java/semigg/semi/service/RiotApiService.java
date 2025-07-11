package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.SummonerDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RiotApiService {

    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.platform-url}")
    private String platformUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 소환사 정보 조회
    public SummonerDto getSummonerByName(String summonerName) {
        String url = platformUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SummonerDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SummonerDto.class
        );

        return response.getBody();
    }

    // 리그 정보 조회
    public LeagueDto getLeagueBySummonerId(String encryptedSummonerId) {
        String url = platformUrl + "/lol/league/v4/entries/by-summoner/" + encryptedSummonerId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<LeagueDto[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                LeagueDto[].class
        );

        // 솔로랭크만 필터링
        return Arrays.stream(response.getBody())
                .filter(dto -> "RANKED_SOLO_5x5".equals(dto.getQueueType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("솔로랭크 리그 정보가 없습니다."));
    }

    public SummonerDto getSummonerByRiotId(String name, String tagLine) {
        String url = "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/" +
                UriUtils.encodePath(name, StandardCharsets.UTF_8) + "/" +
                UriUtils.encodePath(tagLine, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SummonerDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SummonerDto.class
        );

        return response.getBody();
    }

}

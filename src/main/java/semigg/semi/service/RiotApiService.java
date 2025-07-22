package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.SummonerDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiotApiService {


    private static final Logger log = LoggerFactory.getLogger(RiotApiService.class);

    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.platform-url}")
    private String platformUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 소환사 정보 조회
    public SummonerDto getSummonerByName(String summonerName) {
        String url = platformUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName ;

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
    public Optional<LeagueDto>  getLeagueBySummonerId(String encryptedSummonerId) {
        String url = platformUrl + "/lol/league/v4/entries/by-summoner/" + encryptedSummonerId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<LeagueDto[]> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    LeagueDto[].class
            );
            return Arrays.stream(response.getBody())
                    .filter(dto -> "RANKED_SOLO_5x5".equals(dto.getQueueType()))
                    .findFirst();

        }catch (HttpClientErrorException e) {
            log.error("리그 정보 조회 실패: {}", e.getMessage());
            return Optional.empty();
        }

        // 솔로랭크만 필터링
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

    // puuid로 리그 정보 조회
    public Optional<LeagueDto> getLeagueByPuuid(String puuid) {
        String url = "https://asia.api.riotgames.com/lol/league/v1/entries/by-puuid/" + puuid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<LeagueDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    LeagueDto[].class
            );

            return Arrays.stream(response.getBody())
                    .filter(dto -> "RAN  KED_SOLO_5x5".equals(dto.getQueueType()))
                    .findFirst();

        } catch (HttpClientErrorException e) {
            log.error("puuid 기반 리그 조회 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String getPuuidByRiotId(String summonerName, String tagLine) {
        SummonerDto dto = getSummonerByRiotId(summonerName, tagLine);
        return dto.getPuuid();
    }
}

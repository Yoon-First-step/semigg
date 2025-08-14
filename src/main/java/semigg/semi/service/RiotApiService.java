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
import semigg.semi.config.RiotApiProperties;
import semigg.semi.dto.*;
import semigg.semi.dto.LolDto.LeagueDto;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiotApiService {

    private final RiotApiProperties riotApiProperties;
    private static final Logger log = LoggerFactory.getLogger(RiotApiService.class);

    @Value("${riot.api.riotregion-url}")
    private String riotRegionUrl;

    @Value("${riot.api.match-url}")
    private String matchUrl;

    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.platform-url}")
    private String platformUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public SummonerDto getSummonerByName(String summonerName) {
        String url = platformUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), SummonerDto.class).getBody();
    }

    public Optional<LeagueDto> getLeagueBySummonerId(String encryptedSummonerId) {
        String url = platformUrl + "/lol/league/v4/entries/by-summoner/" + encryptedSummonerId;
        try {
            ResponseEntity<LeagueDto[]> response = restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), LeagueDto[].class);
            return Arrays.stream(response.getBody())
                    .filter(dto -> "RANKED_SOLO_5x5".equals(dto.getQueueType()))
                    .findFirst();
        } catch (HttpClientErrorException e) {
            log.error("리그 정보 조회 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public SummonerDto getSummonerByRiotId(String name, String tagLine) {
        String url = riotRegionUrl + "/riot/account/v1/accounts/by-riot-id/" +
                UriUtils.encodePath(name, StandardCharsets.UTF_8) + "/" +
                UriUtils.encodePath(tagLine, StandardCharsets.UTF_8);
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), SummonerDto.class).getBody();
    }

    public Optional<LeagueDto> getLeagueInfoByNameAndTag(String summonerName, String tagLine) {
        try {
            String puuid = fetchPuuidByRiotId(summonerName, tagLine);
            String encryptedSummonerId = fetchSummonerIdByPuuid(puuid);
            LeagueDto[] leagueDtos = fetchLeagueEntries(encryptedSummonerId);

            return Arrays.stream(leagueDtos)
                    .filter(dto -> "RANKED_SOLO_5x5".equals(dto.getQueueType()))
                    .findFirst()
                    .map(dto -> LeagueDto.builder()
                            .queueType(dto.getQueueType())
                            .tier(dto.getTier())
                            .rank(dto.getRank())
                            .leaguePoints(dto.getLeaguePoints())
                            .wins(dto.getWins())
                            .losses(dto.getLosses())
                            .summonerName(dto.getSummonerName())
                            .summonerId(dto.getSummonerId())
                            .leagueId(dto.getLeagueId())
                            .tagLine(tagLine)
                            .build());
        } catch (Exception e) {
            log.error("[getLeagueInfoByNameAndTag] 예외 발생: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public SummonerDto getSummonerByNameAndTag(String summonerName, String tagLine) {
        String url = riotRegionUrl + "/riot/account/v1/accounts/by-riot-id/" + summonerName + "/" + tagLine;
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), SummonerDto.class).getBody();
    }

    public Optional<RiotApiResponse> getRiotApiResponse(String summonerName, String tagLine) {
        SummonerDto summoner = getSummonerByNameAndTag(summonerName, tagLine);
        String summonerId = summoner.getId();
        String puuid = fetchPuuidByRiotId(summonerName, tagLine);

        Optional<LeagueDto> league = getLeagueBySummonerId(summonerId);
        if (league.isEmpty()) return Optional.empty();
        LeagueDto l = league.get();

        SummonerLeagueInfo leagueInfo = new SummonerLeagueInfo(
                "RANKED_SOLO_5x5",
                l.getTier(),
                l.getRank(),
                l.getLeaguePoints(),
                l.getWins(),
                l.getLosses(),
                null // 메인 포지션은 StatsService 등에서 처리
        );

        SummonerProfileDto profile = new SummonerProfileDto(summoner.getProfileIconId());

        RiotApiResponse response = new RiotApiResponse(
                summoner.getName(),
                tagLine,
                leagueInfo,
                profile,
                Collections.emptyList() // mostChamps는 StatsService에서 별도 계산
        );

        return Optional.of(response);
    }

    private String fetchPuuidByRiotId(String name, String tag) {
        String url = String.format("%s/riot/account/v1/accounts/by-riot-id/%s/%s", riotRegionUrl, name, tag);
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), AccountDto.class).getBody().getPuuid();
    }

    private String fetchSummonerIdByPuuid(String puuid) {
        String url = String.format("%s/lol/summoner/v4/summoners/by-puuid/%s", platformUrl, puuid);
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), SummonerDto.class).getBody().getId();
    }

    private LeagueDto[] fetchLeagueEntries(String summonerId) {
        String url = String.format("%s/lol/league/v4/entries/by-summoner/%s", platformUrl, summonerId);
        return restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), LeagueDto[].class).getBody();
    }

    private HttpEntity<Void> createAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);
        return new HttpEntity<>(headers);
    }
}
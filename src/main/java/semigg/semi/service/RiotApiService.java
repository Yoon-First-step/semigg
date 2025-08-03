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

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                            .tagLine(tagLine)                                  // 확장 필드
                            .mainPosition(calculateMainPosition(Collections.singletonList(puuid)))
                            .mostChampions(getMostChampions(puuid, 3))
                            .build());

        } catch (Exception e) {
            log.error("[getLeagueInfoByNameAndTag] 예외 발생: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public SummonerDto getSummonerByNameAndTag(String summonerName, String tagLine) {
        String url = String.format("%s/riot/account/v1/accounts/by-riot-id/%s/%s", riotRegionUrl, summonerName, tagLine);

        ResponseEntity<SummonerDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createAuthEntity(),
                SummonerDto.class
        );

        return response.getBody();
    }
    private String fetchPuuidByRiotId(String name, String tag) {
        String url = String.format("%s/riot/account/v1/accounts/by-riot-id/%s/%s", riotRegionUrl, name, tag);
        ResponseEntity<AccountDto> response = restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), AccountDto.class);
        return response.getBody().getPuuid();
    }

    private String fetchSummonerIdByPuuid(String puuid) {
        String url = String.format("%s/lol/summoner/v4/summoners/by-puuid/%s", platformUrl, puuid);
        ResponseEntity<SummonerDto> response = restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), SummonerDto.class);
        return response.getBody().getId();
    }

    private LeagueDto[] fetchLeagueEntries(String summonerId) {
        String url = String.format("%s/lol/league/v4/entries/by-summoner/%s", platformUrl, summonerId);
        ResponseEntity<LeagueDto[]> response = restTemplate.exchange(url, HttpMethod.GET, createAuthEntity(), LeagueDto[].class);
        return response.getBody();
    }

    private HttpEntity<Void> createAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);
        return new HttpEntity<>(headers);
    }

    public Optional<RiotApiResponse> getRiotApiResponse(String summonerName, String tagLine) {
        // 1. 소환사 ID 조회
        SummonerDto summoner = getSummonerByNameAndTag(summonerName, tagLine);
        String summonerId = summoner.getId();
        String puuid = fetchPuuidByRiotId(summonerName, tagLine);

        // 2. 리그 정보 조회
        Optional<LeagueDto> league = getLeagueBySummonerId(summonerId);

        // 3. 챔피언 정보 및 포지션 계산
        List<String> positionHistory = fetchPositionHistory(puuid); // 포지션 목록을 얻는 메서드 필요
        String mainPosition = calculateMainPosition(positionHistory);
        List<String> mostChamps = getMostChampions(summoner.getPuuid(), 3); // 상위 3개 챔피언 추출

        if (league.isEmpty()) return Optional.empty();

        LeagueDto l = league.get();
        return Optional.of(new RiotApiResponse(
                summoner.getName(),     // summonerName
                tagLine,                // tagLine
                "RANKED_SOLO_5x5",      // queueType ← 명시적 문자열 필요
                l.getTier(),
                l.getRank(),
                l.getLeaguePoints(),
                l.getWins(),
                l.getLosses(),
                mainPosition,
                mostChamps
        ));
    }

    //메인 포지션 리스트 추출
    private String calculateMainPosition(List<String> positions) {
        if (positions == null || positions.isEmpty()) return "UNKNOWN";

        Map<String, Long> frequencyMap = positions.stream()
                .collect(Collectors.groupingBy(pos -> pos, Collectors.counting()));

        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }


    // RiotApiService 내부
    public List<String> getMostChampions(String puuid, int limit) {
        // 예시: 실제 게임 데이터 분석 로직을 여기에 구현
        List<String> championHistory = fetchMatchChampionHistory(puuid); // 예: puuid 기반 챔피언 리스트 조회
        return championHistory == null ? List.of() : calculateTopChampions(championHistory, limit);
    }

    private List<String> calculateTopChampions(List<String> champions, int limit) {
        if (champions == null || champions.isEmpty()) return List.of();

        return champions.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableList());
    }
    // 공통: MatchId 리스트 조회
    private List<String> fetchMatchIds(String puuid, int count) {
        try {
            String url = String.format("%s/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%d", matchUrl, puuid, count);
            ResponseEntity<String[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    String[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            log.error("Match ID 조회 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }

    //챔피언 추출 필드
    private List<String> fetchMatchChampionHistory(String puuid) {
        List<String> matchIds = fetchMatchIds(puuid, 10);
        List<String> champions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            match.getInfo().getParticipants().stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .ifPresent(p -> champions.add(p.getChampionName()));
        }
        return champions;
    }

    //포지션 추출 필드
    private List<String> fetchPositionHistory(String puuid) {
        List<String> matchIds = fetchMatchIds(puuid, 10);
        List<String> positions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            match.getInfo().getParticipants().stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .ifPresent(p -> positions.add(p.getTeamPosition()));
        }
        return positions;
    }

    //매치 기록 중에 riot api 를 통해 자세한 내용 추출 ( 포지션 및 챔피언 )
    private MatchDto fetchMatchDetail(String matchId) {
        try {
            String url = String.format("%s/lol/match/v5/matches/%s", matchUrl, matchId);
            ResponseEntity<MatchDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    MatchDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("매치 상세 호출 실패: {}", e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);
        return headers;
    }
}

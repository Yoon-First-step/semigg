package semigg.semi.service.lol;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import semigg.semi.dto.LolDto.MatchDto;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LolStatsService {

    private static final Logger log = LoggerFactory.getLogger(LolStatsService.class);

    @Value("${riot.api.match-url}")
    private String baseUrl;

    @Value("${riot.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 공통: MatchId 리스트 조회
    private List<String> fetchMatchIds(String puuid, int count) {
        try {
            String url = String.format("%s/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%d", baseUrl, puuid, count);
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

    //매치 기록 중에 riot api 를 통해 자세한 내용 추출 ( 포지션 및 챔피언 )
    private MatchDto fetchMatchDetail(String matchId) {
        try {
            String url = String.format("%s/lol/match/v5/matches/%s", baseUrl, matchId);
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

    // 내부: 최근 n경기에서 내 포지션만 추출
    private List<String> fetchPositionHistory(String puuid) {
        List<String> matchIds = fetchMatchIds(puuid, 10); // 필요시 count 조절
        List<String> positions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            if (match == null || match.getInfo() == null || match.getInfo().getParticipants() == null) continue;

            match.getInfo().getParticipants().stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .ifPresent(p -> {
                        String pos = p.getTeamPosition();
                        positions.add(pos == null || pos.isBlank() ? "UNKNOWN" : pos);
                    });
        }
        return positions;
    }


    public String calculateMainPosition(List<String>positions){
        if (positions == null || positions.isEmpty()) return "UNKNOWN";

        Map<String, Long> frequencyMap = positions.stream()
                .collect(Collectors.groupingBy(pos -> pos, Collectors.counting()));

        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }

    //챔피언 추출 필드
    private List<String> fetchMatchChampionHistory(String puuid) {
        List<String> matchIds = fetchMatchIds(puuid, 10);
        List<String> champions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            if (match == null) continue;
            match.getInfo().getParticipants().stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .ifPresent(p -> champions.add(p.getChampionName()));
        }
        return champions;
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

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", apiKey);
        return headers;
    }

    public String getMainPosition(String puuid){
        List<String> positions = fetchPositionHistory(puuid);
        return calculateMainPosition(positions);
    }

    public List<String> getMostChampions(String puuid, int limit){
        List<String> champions = fetchMatchChampionHistory(puuid);
        return calculateTopChampions(champions, limit);
    }
}

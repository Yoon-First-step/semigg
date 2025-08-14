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
import org.springframework.web.client.RestTemplate;
import semigg.semi.dto.LolDto.MatchDto;

import java.time.LocalDate;
import java.time.ZoneId;
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

    // 기준 날짜 (예: 2024-07-01 이후 경기만)
    LocalDate cutoffDate = LocalDate.of(2024, 7, 1);
    long sinceEpochMillis = cutoffDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();

    public List<String> fetchRankedMatchIdsSince(String puuid, long sinceEpochMillis) {
        int start = 0;
        int count = 20;
        List<String> filteredMatches = new ArrayList<>();

        while (true) {
            List<String> matchIds = fetchMatchIds(puuid, start, count);
            if (matchIds.isEmpty()) break;

            for (String matchId : matchIds) {
                MatchDto detail = fetchMatchDetail(matchId);
                if (detail == null) continue;

                long gameStart = detail.getInfo().getGameStartTimestamp();
                int queueId = detail.getInfo().getQueueId();

                // 랭크 게임이면서 기준 날짜 이후의 경기만 포함
                if ((queueId == 420 || queueId == 440) && gameStart >= sinceEpochMillis) {
                    filteredMatches.add(matchId);
                }

                // 기준 날짜보다 이전이면 더 이상 조회할 필요 없음
                if (gameStart < sinceEpochMillis) return filteredMatches;
            }

            start += count;
        }

        return filteredMatches;
    }

    private List<String> fetchMatchIds(String puuid, int start, int count) {
        try {
            String url = String.format(
                    "%s/lol/match/v5/matches/by-puuid/%s/ids?start=%d&count=%d",
                    baseUrl, puuid, start, count
            );

            ResponseEntity<String[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    String[].class
            );

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            log.error("📛 Match ID 조회 실패 (puuid={}): {}", puuid, e.getMessage(), e);
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
        } catch (Exception e) {
            log.error("📛 Match 상세 조회 실패 (matchId={}): {}", matchId, e.getMessage(), e);
            return null;
        }
    }

    //내 포지션만 추출
    private List<String> fetchPositionHistorySince(String puuid, long sinceEpochMillis) {
        List<String> matchIds = fetchRankedMatchIdsSince(puuid, sinceEpochMillis);
        List<String> positions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            if (match == null || match.getInfo() == null) continue;

            match.getInfo().getParticipants().stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .map(p -> {
                        String position = p.getTeamPosition();
                        return (position == null || position.isBlank()) ? "UNKNOWN" : position;
                    })
                    .findFirst()
                    .ifPresent(positions::add);
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
    private List<String> fetchMatchChampionHistorySince(String puuid, long sinceEpochMillis) {
        List<String> matchIds = fetchRankedMatchIdsSince(puuid, sinceEpochMillis);
        List<String> champions = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchDto match = fetchMatchDetail(matchId);
            if (match == null || match.getInfo() == null) continue;

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
        List<String> positions = fetchPositionHistorySince(puuid,sinceEpochMillis);
        return calculateMainPosition(positions);
    }

    public List<String> getMostChampions(String puuid, int limit){
        List<String> champions = fetchMatchChampionHistorySince(puuid,sinceEpochMillis);
        return calculateTopChampions(champions, limit);
    }
}

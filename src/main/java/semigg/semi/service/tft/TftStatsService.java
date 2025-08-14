package semigg.semi.service.tft;

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
import semigg.semi.dto.TftDto.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TftStatsService {
    private static final Logger log = LoggerFactory.getLogger(TftStatsService.class);

    @Value("${riot.api.platform-url}")
    private String platformUrl;

    @Value("${riot.api.match-url}")
    private String baseUrl;

    @Value("${riot.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 최근 count 경기 기준으로 게임 수/순방률/승률/평균등수 계산
     */
    public TftComputedStats computeStats(String puuid, int count) {
        List<String> matchIds = fetchMatchIds(puuid, count);

        int total = 0;
        int wins = 0;        // 1등
        int top4 = 0;        // 1~4등
        int losses = 0;
        long placementSum = 0;

        for (String matchId : matchIds) {
            TftMatchDto match = fetchMatch(matchId);
            if (match == null || match.getInfo() == null) continue;

            Optional<TftParticipantDto> me = match.getInfo().getParticipants()
                    .stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst();

            if (me.isEmpty()) continue;

            int placement = me.get().getPlacement(); // 1~8
            total++;
            placementSum += placement;
            if (placement == 1) wins++;
            if (placement <= 4) top4++;
        }

        double winRate = total == 0 ? 0 : (wins * 100.0) / total;
        double protectRate = total == 0 ? 0 : (top4 * 100.0) / total;

        return new TftComputedStats(total, wins, losses, winRate, protectRate);
    }

    private List<String> fetchMatchIds(String puuid, int count) {
        try {
            String url = String.format(
                    "%s/tft/match/v1/matches/by-puuid/%s/ids?start=0&count=%d",
                    baseUrl, puuid, count
            );
            ResponseEntity<String[]> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers()), String[].class
            );
            String[] body = res.getBody();
            return body == null ? List.of() : Arrays.asList(body);
        } catch (Exception e) {
            // log.warn("TFT match ids error", e);
            return List.of();
        }
    }

    private TftMatchDto fetchMatch(String matchId) {
        try {
            String url = String.format("%s/tft/match/v1/matches/%s", baseUrl, matchId);
            ResponseEntity<TftMatchDto> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers()), TftMatchDto.class
            );
            return res.getBody();
        } catch (Exception e) {
            // log.warn("TFT match error {}: {}", matchId, e.getMessage());
            return null;
        }
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Riot-Token", apiKey);
        return h;
    }

    private HttpEntity<Void> auth() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Riot-Token", apiKey);
        return new HttpEntity<>(h);
    }

    // 1) PUUID -> TFT SummonerId 조회
    private String getTftSummonerIdByPuuid(String puuid) {
        String url = String.format("%s/tft/summoner/v1/summoners/by-puuid/%s", platformUrl, puuid);
        ResponseEntity<TftSummonerDto> res =
                restTemplate.exchange(url, HttpMethod.GET, auth(), TftSummonerDto.class);
        return Objects.requireNonNull(res.getBody()).getId(); // encryptedSummonerId
    }

    // 2) SummonerId -> 현재 랭크 정보 조회
    public TftRankDto getCurrentRank(String puuid) {
        try {
            String summonerId = getTftSummonerIdByPuuid(puuid);

            String url = String.format("%s/tft/league/v1/entries/by-summoner/%s", platformUrl, summonerId);
            ResponseEntity<TftRankEntryDto[]> res =
                    restTemplate.exchange(url, HttpMethod.GET, auth(), TftRankEntryDto[].class);

            TftRankEntryDto[] entries = res.getBody();

            if (entries == null || entries.length == 0) {
                throw new IllegalStateException("해당 PUUID의 랭크 정보를 찾을 수 없습니다.");
            }

            return Arrays.stream(entries)
                    .filter(e -> "RANKED_TFT".equals(e.getQueueType()))
                    .findFirst()
                    .map(e -> new TftRankDto(
                            e.getTier(),
                            e.getRank(),
                            e.getLeaguePoints(),
                            e.getWins(),
                            e.getLosses()
                    ))
                    .orElseThrow(() -> new IllegalStateException("해당 PUUID의 RANKED_TFT 모드 랭크 정보가 없습니다."));
        } catch (Exception ex) {
            throw new RuntimeException("TFT 랭크 정보 조회 중 오류가 발생했습니다.", ex);
        }
    }

    public TftStatsSnapshot buildStatsSnapshot(String puuid) {
        TftComputedStats s = computeStats(puuid, 20);
        return new TftStatsSnapshot(
                s.getPlayCount(),
                s.getWins(),
                s.getLosses(),
                s.getWinRate(),
                s.getProtectRate()
        );
    }

}

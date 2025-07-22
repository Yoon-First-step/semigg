package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.LeaderboardDto;
import semigg.semi.dto.LeagueDto;
import semigg.semi.dto.LeagueResponseDto;
import semigg.semi.service.LeagueService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    // 유저 ID로 리그 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<LeagueDto> getLeagueByUserId(@PathVariable Long userId) {
        LeagueDto leagueDto = leagueService.findByUserId(userId);
        return ResponseEntity.ok(leagueDto);
    }

    @GetMapping
    public ResponseEntity<LeagueDto> getLeagueBySummoner(
            @RequestParam String summonerName,
            @RequestParam String tagLine) {

        LeagueDto leagueDto = leagueService.findBySummonerNameAndTagLine(summonerName, tagLine);
        return ResponseEntity.ok(leagueDto);
    }

    @GetMapping("/riot")
    public ResponseEntity<LeagueResponseDto> getLeagueFromRiot(
            @RequestParam String summonerName,
            @RequestParam String tagLine) {

        LeagueResponseDto leagueDto = leagueService.fetchLeagueFromRiotApi(summonerName, tagLine);
        return ResponseEntity.ok(leagueDto);
    }

//    @GetMapping("/api/leaderboard")
//    public List<LeaderboardDto> getLeaderboard() {
//        return statsRepository.findAllOrderedByRank().stream()
//                .map(LeaderboardDto::from)
//                .collect(Collectors.toList());
//    }
}

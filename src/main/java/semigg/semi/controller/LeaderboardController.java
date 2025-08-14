package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semigg.semi.dto.LolDto.LeaderboardEntryDto;
import semigg.semi.dto.TftDto.TftLeaderboardEntryDto;
import semigg.semi.service.lol.LolLeaderboardService;
import semigg.semi.service.tft.TftAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LolLeaderboardService leaderboardService;
    private final TftAccountService tftAccountService;

    @GetMapping("/lol")
    public ResponseEntity<List<LeaderboardEntryDto>> getLolLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getRankedLeaderboard());
    }

    @GetMapping("/tft")
    public ResponseEntity<List<TftLeaderboardEntryDto>> getTftLeaderboard() {
        return ResponseEntity.ok(tftAccountService.getLeaderboard());
    }
}
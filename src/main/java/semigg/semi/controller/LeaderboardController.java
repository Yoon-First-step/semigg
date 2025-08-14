package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.LolDto.LeaderboardEntryDto;
import semigg.semi.dto.TftDto.TftLeaderboardEntryDto;
import semigg.semi.service.lol.LolLeaderboardService;
import semigg.semi.service.tft.TftAccountService;
import semigg.semi.service.tft.TftLeaderboardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LolLeaderboardService leaderboardService;

    private final TftAccountService tftAccountService;

    //리더보드 순위 정렬 후 리스트로 정보 전달
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getRankedLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping
    public List<TftLeaderboardEntryDto> leaderboard() {
        return tftAccountService.getLeaderboard();
    }

}
package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.LeaderboardEntryDto;
import semigg.semi.service.LeaderboardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    //리더보드 순위 정렬 후 리스트로 정보 전달
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getRankedLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

}
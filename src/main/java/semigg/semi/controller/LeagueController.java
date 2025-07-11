package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.LeagueResponseDto;
import semigg.semi.service.LeagueService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    // 유저 ID로 리그 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<LeagueResponseDto> getLeagueByUserId(@PathVariable Long userId) {
        LeagueResponseDto leagueDto = leagueService.findByUserId(userId);
        return ResponseEntity.ok(leagueDto);
    }
}

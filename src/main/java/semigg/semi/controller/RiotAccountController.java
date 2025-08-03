package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semigg.semi.dto.ProfileCardDto;
import semigg.semi.dto.RiotAccountDto;
import semigg.semi.service.RiotAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class RiotAccountController {

    private final RiotAccountService riotAccountService;

    // ✅ 특정 유저의 모든 Riot 계정 조회
    @GetMapping("/{userId}/riot-accounts")
    public ResponseEntity<List<RiotAccountDto>> getRiotAccountsByUser(@PathVariable Long userId) {
        List<RiotAccountDto> response = riotAccountService.getRiotAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
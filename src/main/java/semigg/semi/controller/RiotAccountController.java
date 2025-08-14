package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import semigg.semi.domain.User;
import semigg.semi.dto.RegisterAccountRequest;
import semigg.semi.service.lol.LolAccountService;
import semigg.semi.service.tft.TftAccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/riot/accounts")
public class RiotAccountController {

    private final LolAccountService lolAccountService;
    private final TftAccountService tftAccountService;

    /**
     * LoL 계정 등록
     */
    @PostMapping("/lol/register")
    public ResponseEntity<Long> registerLolAccount(
            @AuthenticationPrincipal User user,
            @RequestBody RegisterAccountRequest request
    ) {
        var account = lolAccountService.registerAccount(user.getId(), request);
        return ResponseEntity.ok(account.getId());
    }

    /**
     * LoL 본계정 변경
     */
    @PatchMapping("/lol/{accountId}/main")
    public ResponseEntity<Void> changeLolMainAccount(
            @AuthenticationPrincipal User user,
            @PathVariable Long accountId
    ) {
        lolAccountService.changeMainAccount(user.getId(), accountId);
        return ResponseEntity.ok().build();
    }

    /**
     * TFT 계정 등록
     */
    @PostMapping("/tft/register")
    public ResponseEntity<Long> registerTftAccount(
            @AuthenticationPrincipal User user,
            @RequestBody RegisterAccountRequest request
    ) {
        var account = tftAccountService.registerAccount(user.getId(), request);
        return ResponseEntity.ok(account.getId());
    }

    /**
     * TFT 본계정 변경
     */
    @PatchMapping("/tft/{accountId}/main")
    public ResponseEntity<Void> changeTftMainAccount(
            @AuthenticationPrincipal User user,
            @PathVariable Long accountId
    ) {
        tftAccountService.changeMainAccount(user.getId(), accountId);
        return ResponseEntity.ok().build();
    }
}
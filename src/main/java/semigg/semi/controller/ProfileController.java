package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semigg.semi.domain.User;
import semigg.semi.domain.lol.LolAccount;
import semigg.semi.dto.LolDto.ProfileCardDto;
import semigg.semi.dto.TftDto.TftProfileCardDto;
import semigg.semi.dto.TftDto.TftUserProfileDto;
import semigg.semi.repository.LolAccountRepository;
import semigg.semi.service.lol.LolAccountService;
import semigg.semi.service.tft.TftAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final TftAccountService tftAccountService;
    private final LolAccountService lolAccountService;

    @GetMapping("/lol/all-cards")
    public ResponseEntity<List<ProfileCardDto>> getAllLolProfileCards() {
        List<ProfileCardDto> profiles = lolAccountService.getAllUserProfileCards();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/tft/all-cards")
    public ResponseEntity<List<TftProfileCardDto>> getAllUserProfileCards() {
        List<TftProfileCardDto> profiles = tftAccountService.getAllUserProfileCards();
        return ResponseEntity.ok(profiles);
    }

}
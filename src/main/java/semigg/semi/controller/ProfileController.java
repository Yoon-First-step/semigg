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
import semigg.semi.repository.LolAccountRepository;
import semigg.semi.service.tft.TftAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final LolAccountRepository riotAccountRepository;

    private final TftAccountService tftAccountService;

    //프로필 카드에 들어갈 정보 전달
    @GetMapping("/{userId}/card")
    public ResponseEntity<ProfileCardDto> getProfileCard(@PathVariable Long userId) {
        List<LolAccount> accounts = riotAccountRepository.findByUserId(userId);
        return ResponseEntity.ok(ProfileCardDto.from(accounts));
    }

    // 4) 내 프로필 카드(본계정)
    @GetMapping("/profile-card")
    public TftProfileCardDto myCard(@AuthenticationPrincipal User user) {
        return tftAccountService.getProfileCardByUser(user.getId());
    }

}
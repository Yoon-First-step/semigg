package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semigg.semi.domain.RiotAccount;
import semigg.semi.dto.ProfileCardDto;
import semigg.semi.repository.RiotAccountRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final RiotAccountRepository riotAccountRepository;

    //프로필 카드에 들어갈 정보 전달
    @GetMapping("/{userId}/card")
    public ResponseEntity<ProfileCardDto> getProfileCard(@PathVariable Long userId) {
        List<RiotAccount> accounts = riotAccountRepository.findByUserId(userId);

        return ResponseEntity.ok(ProfileCardDto.from(accounts));
    }
}
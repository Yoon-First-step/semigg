package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.UserRequestDto;
import semigg.semi.dto.UserResponseDto;
import semigg.semi.service.UserService;
import semigg.semi.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody UserRequestDto dto, @RequestParam("code") String code) {
        Long userId = userService.register(dto, code);
        return ResponseEntity.ok(userId);
    }

    // 이메일로 사용자 조회
//    @GetMapping("/email")
//    public ResponseEntity<UserResponseDto> getByEmail(@RequestParam("email") String email) {
//        return ResponseEntity.ok(userService.findByEmail(email));
//    }

    //이메일 인증 코드 발송
    @PostMapping("/send-auth-code")
    public ResponseEntity<Void> sendAuthCode(@RequestParam("email") String email) {
        emailService.sendAuthCode(email);
        return ResponseEntity.ok().build();
    }

//    // ID로 사용자 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(userService.findById(id));
//    }

    //ID로 사용자 정보 업데이트
    @PostMapping("/{id}/refresh")
    public ResponseEntity<Void> refreshUserAndLeague(@PathVariable Long id) {
        userService.refreshUserAndLeague(id);
        return ResponseEntity.ok().build();
    }

}

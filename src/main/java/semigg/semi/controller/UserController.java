package semigg.semi.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import semigg.semi.config.JwtTokenProvider;
import semigg.semi.domain.User;
import semigg.semi.dto.*;
import semigg.semi.domain.UserPrincipal;
import semigg.semi.service.UserService;
import semigg.semi.service.EmailService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody UserRequestDto dto, @RequestParam("code") String code) {
        Long userId = userService.register(dto, code);
        return ResponseEntity.ok(userId);
    }

    //이메일 인증 코드 발송
    @PostMapping("/send-auth-code")
    public ResponseEntity<Void> sendAuthCode(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        emailService.sendAuthCode(email);
        return ResponseEntity.ok().build();
    }

    //이메일 인증 완료시 true 반환
    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyCode(@RequestBody VerificationRequest dto) {
        boolean result = emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(result);
    }

    //이메일 변경시
    @PatchMapping("/email")
    public ResponseEntity<Void> changeEmail(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ChangeEmailRequest request) {
        userService.updateEmail(principal.getId(), request.getNewEmail());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class ChangeEmailRequest {
        private String newEmail;
    }

    //패스워드 변경시
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ChangePasswordRequest request) {
        userService.updatePassword(principal.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        // 1. 사용자 조회
        User user = userService.findByEmail(request.getEmail());
        UserPrincipal userPrincipal = UserPrincipal.from(user);

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(userPrincipal.getId(), userPrincipal.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal.getId());

        // 4. 응답 DTO 반환
        return ResponseEntity.ok(
                new LoginResponseDto(user.getId(), user.getEmail(), accessToken, refreshToken)
        );
    }

}

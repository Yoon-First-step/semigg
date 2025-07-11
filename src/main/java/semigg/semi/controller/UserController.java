package semigg.semi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semigg.semi.dto.UserRequestDto;
import semigg.semi.dto.UserResponseDto;
import semigg.semi.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody UserRequestDto requestDto) {
        Long userId = userService.register(requestDto);
        return ResponseEntity.ok(userId);
    }

    // 이메일로 사용자 조회
    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // ID로 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<Void> refreshUserAndLeague(@PathVariable Long id) {
        userService.refreshUserAndLeague(id);
        return ResponseEntity.ok().build();
    }


}

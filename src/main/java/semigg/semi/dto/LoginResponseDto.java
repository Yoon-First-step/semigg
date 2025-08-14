package semigg.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String email;
    private String accessToken;
    private String refreshToken; // 선택적 (토큰 재발급 전략에 따라)
}

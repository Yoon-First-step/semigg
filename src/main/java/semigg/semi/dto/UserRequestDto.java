package semigg.semi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    private String email;             // 로그인 ID
    private String name;              // 이름
    private String studentId;         // 학번
    private String riotId;  // 예: "Hide on bush#KR1   // 소환사 태그

}

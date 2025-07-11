package semigg.semi.dto;

import lombok.*;
import semigg.semi.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private String studentId;
    private String mainSummonerName;
    private String tagLine;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .studentId(user.getStudentId())
                .mainSummonerName(user.getMainSummonerName())
                .tagLine(user.getTagLine())
                .build();
    }
}


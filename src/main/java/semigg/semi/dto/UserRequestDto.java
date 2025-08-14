package semigg.semi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String studentId;
    @NotBlank
    private String password;
}
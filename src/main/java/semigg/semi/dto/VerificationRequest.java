package semigg.semi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {
    private String email;
    private String code;
}

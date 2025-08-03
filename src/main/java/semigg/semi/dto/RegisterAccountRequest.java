package semigg.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAccountRequest {
    private String summonerName;
    private String tagLine;
    private boolean isMainAccount;
}
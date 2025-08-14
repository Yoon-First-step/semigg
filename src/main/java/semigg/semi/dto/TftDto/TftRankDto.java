package semigg.semi.dto.TftDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class TftRankDto {
    private String tier;
    private String rank;
    private int lp;
    private int wins;
    private int losses;
}
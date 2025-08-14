package semigg.semi.dto.TftDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import semigg.semi.dto.TftDto.TftProfileCardDto;

import java.util.List;

@Getter
@Builder
public class TftUserProfileDto {
    private String name;
    private String studentId;
    private List<TftProfileCardDto> accounts;
}
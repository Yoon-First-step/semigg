package semigg.semi.dto;

import lombok.Builder;
import java.util.List;
import semigg.semi.domain.RiotAccount;

@Builder
public record ProfileCardDto(
        List<RiotAccountDto> accounts
) {
    public static ProfileCardDto from(List<RiotAccount> accounts) {
        return new ProfileCardDto(
                accounts.stream()
                        .map(a -> RiotAccountDto.builder()
                                .profileIconId(a.getProfileIconId())
                                .summonerName(a.getSummonerName())
                                .tagLine(a.getTagLine())
                                .tier(a.getTier())
                                .isMainAccount(a.isMainAccount())
                                .build())
                        .toList()
        );
    }
}
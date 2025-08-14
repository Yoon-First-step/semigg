package semigg.semi.dto.LolDto;

import lombok.Builder;
import java.util.List;
import semigg.semi.domain.lol.LolAccount;

@Builder
public record ProfileCardDto(
        List<RiotAccountDto> accounts
) {
    public static ProfileCardDto from(List<LolAccount> accounts) {
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
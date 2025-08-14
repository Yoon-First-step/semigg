package semigg.semi.dto.LolDto;

import lombok.Builder;
import semigg.semi.domain.User;
import semigg.semi.domain.lol.LolAccount;

import java.util.List;

@Builder
public record ProfileCardDto(
        String name,
        String studentId,
        Integer profileIconId,
        String summonerName,
        String tagLine,
        String tier,
        List<RiotAccountDto> accounts
) {
    public static ProfileCardDto from(User user, List<LolAccount> accounts) {
        // 대표 계정 (isMainAccount == true) 선택
        LolAccount mainAccount = accounts.stream()
                .filter(LolAccount::isMainAccount)
                .findFirst()
                .orElse(accounts.isEmpty() ? null : accounts.get(0));

        return new ProfileCardDto(
                user.getName(),
                user.getStudentId(),
                mainAccount != null ? mainAccount.getProfileIconId() : null,
                mainAccount != null ? mainAccount.getSummonerName() : null,
                mainAccount != null ? mainAccount.getTagLine() : null,
                mainAccount != null ? mainAccount.getTier() : null,
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
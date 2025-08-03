package semigg.semi.dto;

import lombok.Builder;
import semigg.semi.domain.RiotAccount;

@Builder
public record RiotAccountDto(
        int profileIconId,
        String summonerName,
        String tagLine,
        String tier,
        String rank,
        int lp,
        int wins,
        int losses,
        String mainPosition,
        boolean isMainAccount
) {
    public static RiotAccountDto from(RiotAccount entity) {
        return new RiotAccountDto(
                entity.getProfileIconId(),
                entity.getSummonerName(),
                entity.getTagLine(),
                entity.getTier(),
                entity.getRank(),
                entity.getLp(),
                entity.getWins(),
                entity.getLosses(),
                entity.getMainPosition(),
                entity.isMainAccount()
        );
    }
}


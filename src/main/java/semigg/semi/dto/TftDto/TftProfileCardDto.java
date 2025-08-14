package semigg.semi.dto.TftDto;
import lombok.Builder;
import semigg.semi.domain.User;
import semigg.semi.dto.LolDto.LeagueDto;
import semigg.semi.dto.SummonerDto;

@Builder
public record TftProfileCardDto(
        String name,
        String studentId,
        int profileIconId,
        String summonerName,
        String tagLine,
        String tier,
        String rank,
        int leaguePoints
) {
    public static TftProfileCardDto from(User user, SummonerDto summoner, LeagueDto league) {
        return new TftProfileCardDto(
                user.getName(),
                user.getStudentId(),
                summoner.getProfileIconId(),
                summoner.getSummonerName(),
                summoner.getTagLine(),
                league.getTier(),
                league.getRank(),
                league.getLeaguePoints()
        );
    }
}
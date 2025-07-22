package semigg.semi.dto;

import semigg.semi.dto.LeagueDto;

public class LeagueResponseDto {
    private String tier;
    private String rank;
    private int wins;
    private int losses;
    private double winRate;

    // 정적 팩토리 메서드
    public static LeagueResponseDto fromDto(LeagueDto dto) {
        LeagueResponseDto response = new LeagueResponseDto();
        response.tier = dto.getTier();
        response.rank = dto.getRank();
        response.wins = dto.getWins();
        response.losses = dto.getLosses();

        int total = dto.getWins() + dto.getLosses();
        response.winRate = total > 0 ? (dto.getWins() * 100.0 / total) : 0.0;

        return response;
    }

    // Getter/Setter 또는 Lombok @Getter @Setter @NoArgsConstructor 등 추가 가능
}
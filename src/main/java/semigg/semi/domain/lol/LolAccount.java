package semigg.semi.domain.lol;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semigg.semi.domain.RiotAccount;
import semigg.semi.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LolAccount extends RiotAccount {

    private String tier;
    private String rank;
    private int lp;
    private int wins;
    private int losses;
    private String mainPosition;
    private double winRate;

    private LocalDateTime lastUpdated;

    public LolAccount(
            User user,
            String summonerName,
            String tagLine,
            int profileIconId,
            boolean isMainAccount,
            String tier,
            String rank,
            int lp,
            int wins,
            int losses,
            String mainPosition,
            LocalDateTime lastUpdated
    ){
        super(user, summonerName, tagLine, profileIconId, isMainAccount);
        this.tier = tier;
        this.rank = rank;
        this.lp = lp;
        this.wins = wins;
        this.losses = losses;
        this.mainPosition = mainPosition;
        this.lastUpdated = lastUpdated;
        this.winRate = calculateWinRate();
    }

    private double calculateWinRate() {
        int total = wins + losses;
        return total == 0 ? 0.0 : (wins * 100.0) / total;
    }
    // 전적 업데이트 도메인 메서드
    public void updateStats(String tier, String rank, int lp, int wins, int losses) {
        this.tier = tier;
        this.rank = rank;
        this.lp = lp;
        this.wins = wins;
        this.losses = losses;
        this.lastUpdated = LocalDateTime.now();
        this.winRate = calculateWinRate();
    }
    // 메인 포지션 갱신
    public void updateMainPosition(String mainPosition) {
        this.mainPosition = mainPosition;
    }
    @OneToMany(mappedBy = "riotAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MostChampion> mostChampions = new ArrayList<>();
}

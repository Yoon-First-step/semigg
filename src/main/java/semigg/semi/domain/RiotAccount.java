package semigg.semi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiotAccount {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String summonerName;
    private String tagLine;
    private String tier;
    private String rank;
    private int lp;
    private int wins;
    private int losses;
    private String mainPosition;
    private boolean isMainAccount;
    private double winRate;
    private LocalDateTime lastUpdated;

    // 생성자 방식으로 객체 생성
    public RiotAccount(
            User user,
            String summonerName,
            String tagLine,
            String tier,
            String rank,
            int lp,
            int wins,
            int losses,
            String mainPosition,
            boolean isMainAccount,
            LocalDateTime lastUpdated
    ) {
        this.user = user;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.tier = tier;
        this.rank = rank;
        this.lp = lp;
        this.wins = wins;
        this.losses = losses;
        this.mainPosition = mainPosition;
        this.isMainAccount = isMainAccount;
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

    public void changeMainAccount(boolean isMain) {
        this.isMainAccount = isMain;
    }

    @OneToMany(mappedBy = "riotAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MostChampion> mostChampions = new ArrayList<>();
}
package semigg.semi.domain.tft;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import semigg.semi.domain.RiotAccount;
import semigg.semi.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TftAccount extends RiotAccount {

    private String tier;
    private String rank;
    private int lp;
    private int wins;
    private int losses;
    private double winRate;
    private int playCount;
    private double protectRate;
    private LocalDateTime LastUpdated;

    public TftAccount(
            User user,
            String summonerName,
            String tagLine,
            int profileIconId,
            boolean isMainAccount,
            int accountId,
            String tier,
           String rank,
            int lp,
            int wins,
            int losses,
            double winRate,
            int playCount,
            double protectRate,
            LocalDateTime LastUpdated
    ){
        super(user, summonerName, tagLine, profileIconId, isMainAccount,accountId);
        this.tier = tier;
        this.rank = rank;
        this.lp = lp;
        this.wins = wins;
        this.losses = losses;
        this.winRate = winRate;
        this.playCount = playCount;
        this.protectRate = protectRate;
        this.LastUpdated = LastUpdated;
        this.winRate = calculateWinRate();
    }

    private double calculateWinRate() {
        int total = wins + losses;
        return total == 0 ? 0.0 : (wins * 100.0) / total;
    }

    // ✅ 서비스에서 호출할 갱신 메서드 (전달받은 값을 그대로 세팅)
    public void updateTftStats(
            String tier,
            String rank,
            int lp,
            int wins,
            int losses,
            int playCount,
            double winRate,
            double protectRate
    ) {
        this.tier = tier;
        this.rank = rank;
        this.lp = lp;
        this.wins = wins;
        this.losses = losses;
        this.playCount = playCount;
        this.winRate = winRate;
        this.protectRate = protectRate;
    }

    public String getCurrentRank() {
        if (tier == null || tier.isBlank()) {
            return "UNRANKED";
        }
        return String.format("%s %s (%d LP)", tier, rank, lp);
    }
}
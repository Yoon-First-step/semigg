package semigg.semi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RiotAccount {
    @Id
    @GeneratedValue
    private Long id;
    private String summonerName;
    private String tagLine;
    private int profileIconId;

    private boolean isMainAccount;
    private int accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable  = false, unique = true)
    private String puuid;

    public RiotAccount(User user, String summonerName, String tagLine, int profileIconId, boolean isMainAccount, int accountId) {
        this.user = user;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.profileIconId = profileIconId;
        this.isMainAccount = isMainAccount;
        this.accountId = accountId;
    }
    public void changeMainAccount(boolean isMain) {
        this.isMainAccount = isMain;
    }

}
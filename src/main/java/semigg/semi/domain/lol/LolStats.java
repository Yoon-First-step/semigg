package semigg.semi.domain.lol;

import jakarta.persistence.*;
import lombok.*;
import semigg.semi.domain.User;

@Entity
@Getter
@Table(name = "lolstats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LolStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String summonerName;
    private String tagLine;
    private String tier;
    @Column(name = "league_rank", nullable = false)
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 계산용 필드 예시
    public int getWinRate() {
        return (int)((double) wins / (wins + losses) * 100);
    }
}

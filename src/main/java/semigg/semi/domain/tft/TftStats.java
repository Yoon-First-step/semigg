package semigg.semi.domain.tft;

import jakarta.persistence.*;
import lombok.*;
import semigg.semi.domain.User;

@Entity
@Getter
@Table(name = "tftstats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TftStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String summonerName;
    private String tagLine;
    private String tier;
    @Column(name = "tft_league_rank", nullable = false)
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}

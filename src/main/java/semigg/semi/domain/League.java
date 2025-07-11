package semigg.semi.domain;

import jakarta.persistence.*;
import lombok.*;
import semigg.semi.dto.LeagueDto;

@Entity
@Table(name = "leagues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tier;          // e.g. GOLD, PLATINUM

    @Column(name = "league_rank", nullable = false)
    private String rank;       // e.g. I, II

    @Column(nullable = false)
    private int leaguePoints;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    @Column(nullable = false)
    private String queueType;     // e.g. RANKED_SOLO_5x5

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void updateInfo(LeagueDto dto) {
        this.tier = dto.getTier();
        this.rank = dto.getRank();
        this.leaguePoints = dto.getLeaguePoints();
    }

    // == 연관관계 메서드 == //
    public void setUser(User user) {
        this.user = user;
    }
}



package semigg.semi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;  // 로그인 ID (이메일), 고유 식별자

    @Column(nullable = false, length = 20)
    private String name;  // 이름

    @Column(nullable = false, length = 20)
    private String studentId;  // 학번

    @Column(nullable = false, length = 30)
    private String mainSummonerName;  // 대표 계정 이름

    @Column(nullable = false, length = 10)
    private String tagLine;  // 대표 계정 태그

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private League league;

    /**
     * 양방향 연관관계 편의 메서드
     * @param league User와 연결할 League 객체
     */
    public void setLeague(League league) {
        this.league = league;
        league.setUser(this);
    }

    public void updateSummonerInfo(String newName, String newTagLine) {
        this.mainSummonerName = newName;
        this.tagLine = newTagLine;
    }
}


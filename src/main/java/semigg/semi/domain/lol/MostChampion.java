package semigg.semi.domain.lol;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ✅ 기본 생성자 추가
public class MostChampion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String championName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lol_account_id", nullable = false)
    private LolAccount riotAccount;

    // 생성자
    public MostChampion(String championName, LolAccount riotAccount) {
        this.championName = championName;
        this.riotAccount = riotAccount;
    }
}
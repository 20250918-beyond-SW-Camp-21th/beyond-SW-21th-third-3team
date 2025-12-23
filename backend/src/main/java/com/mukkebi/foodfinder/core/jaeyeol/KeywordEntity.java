package com.mukkebi.foodfinder.core.jaeyeol;



import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "keyword")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Enum의 이름 (예: HANGOVER) - 코드상 식별용

    @Column(nullable = false)
    private String label; // 화면 표시용 (예: #해장 필요)

    @Column(nullable = false, length = 500)
    private String promptMessage; // AI 지시용 메시지

    // DB에 리스트를 바로 넣을 수 없으므로, 콤마(,)로 구분된 문자열로 저장하거나
    // 별도 테이블로 분리해야 하지만, MVP 단계에서는 문자열 저장 추천 (예: "MORNING,LUNCH")
    @Column(nullable = false)
    private String availableMealTypes;

    @Builder
    public KeywordEntity(String name, String label, String promptMessage, String availableMealTypes) {
        this.name = name;
        this.label = label;
        this.promptMessage = promptMessage;
        this.availableMealTypes = availableMealTypes;
    }
}
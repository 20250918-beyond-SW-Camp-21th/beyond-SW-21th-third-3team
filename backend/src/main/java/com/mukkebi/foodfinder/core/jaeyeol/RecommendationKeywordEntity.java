package com.mukkebi.foodfinder.core.jaeyeol;

// 중간테이블

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "recommendation_keyword")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationKeywordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id")
    private RecommendationKeywordEntity recommendation;

    // 어떤 태그를 선택했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private KeywordEntity keyword;

    @Builder
    public RecommendationKeywordEntity(RecommendationKeywordEntity recommendation, KeywordEntity keyword) {
        this.recommendation = recommendation;
        this.keyword = keyword;
    }
}
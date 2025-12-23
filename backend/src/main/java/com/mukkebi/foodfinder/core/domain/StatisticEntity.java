package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.ActionStatus;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "statistics",
        indexes = {
                // 특정 유저의 통계를 뽑거나, 시간대별/요일별 전체 통계를 낼 때 성능 최적화
                @Index(name = "idx_stats_user_action", columnList = "user_id, actionTime"),
                @Index(name = "idx_stats_status", columnList = "status")
        }
)
public class StatisticEntity extends BaseEntity {

    @Column(nullable=false, name = "user_id")
    private Long userId;      // FK

    @Column(name = "review_id")
    private Long reviewId;    // FK

    // FK 대신 외부 API의 ID를 직접 저장 (String 타입)
    private String restaurantId;

    // 통계 화면에서 식당 이름을 바로 보여주기 위한 스냅샷
    private String restaurantName;

    private String categoryAtTime;      // 선택 당시의 카테고리 (한식/중식 등)
    private Double distanceAtTime;      // 선택 당시의 거리
    private LocalDateTime actionTime;   // 요일/시간 통계용

    @Enumerated(EnumType.STRING)
    private ActionStatus status;        // SELECTED 추천 선택, EXITED: 추천 이탈

    // 정적 팩토리 메서드
    public static StatisticEntity createLog(User user, Restaurant restaurant, ActionStatus status) {
        StatisticEntity log = new StatisticEntity();
        log.userId = user.getId();
        // record의 id를 가져올 때는 .id() 메서드를 사용합니다.
        log.restaurantId = restaurant.id();
        log.restaurantName = restaurant.name();
        log.categoryAtTime = restaurant.category();
        log.distanceAtTime = restaurant.distance(); // 카카오 API가 준 거리 정보 사용
        // 카카오 API의 distance가 String일 경우 Double.parseDouble() 처리가 필요할 수 있음
        log.actionTime = LocalDateTime.now();
        log.status = status;
        return log;
    }

    // 나중에 리뷰 작성 시 연결을 위한 메서드
    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }
}
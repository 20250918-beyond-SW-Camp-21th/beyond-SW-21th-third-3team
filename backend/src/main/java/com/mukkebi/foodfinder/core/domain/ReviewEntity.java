package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "review",
        indexes = {
            @Index(name="idx_review_user",columnList="userId")
        }
)
public class ReviewEntity extends BaseEntity {

    @Column(nullable = true, length = 1000)
    private String content;

    @Column(nullable=false)
    private double rating;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false)
    private Long reconmmendationId;

}

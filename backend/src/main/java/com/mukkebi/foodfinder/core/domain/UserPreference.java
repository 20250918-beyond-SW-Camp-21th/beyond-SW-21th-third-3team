package com.mukkebi.foodfinder.core.domain;

import com.mukkebi.foodfinder.core.enums.PreferenceType;
import com.mukkebi.foodfinder.storage.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferenceType preferenceType;

    private boolean liked;

    @Builder
    public UserPreference(User user, PreferenceType preferenceType, boolean liked) {
        this.user = user;
        this.preferenceType = preferenceType;
        this.liked = liked;
    }
}

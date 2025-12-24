package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByUserId(Long userId);
    List<ReviewEntity> findByRestaurantId(Long restaurantId);
}

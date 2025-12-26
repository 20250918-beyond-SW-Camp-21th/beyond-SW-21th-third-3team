package com.mukkebi.foodfinder.storage;

import com.mukkebi.foodfinder.core.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

//    List<Review> findTop20ByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
//
//    List<Review> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        select avg(r.rating)
        from Review r
        where r.restaurantId = :restaurantId
    """)
    Double findAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("""
        select avg(r.rating)
        from Review r
        where r.userId = :userId
    """)
    Double findAverageRatingByUserId(@Param("userId") Long userId);

    @Query("""
    select r
    from Review r
    where r.restaurantId = :restaurantId
      and (:cursorId is null or r.id < :cursorId)
    order by r.id desc
""")
    List<Review> findByRestaurantWithCursor(
            @Param("restaurantId") Long restaurantId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
    select r
    from Review r
    where r.userId = :userId
      and (:cursorId is null or r.id < :cursorId)
    order by r.id desc
""")
    List<Review> findByUserWithCursor(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}

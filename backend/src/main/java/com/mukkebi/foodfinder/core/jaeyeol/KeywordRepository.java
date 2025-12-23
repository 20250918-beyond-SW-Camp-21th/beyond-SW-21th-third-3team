package com.mukkebi.foodfinder.core.jaeyeol;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<KeywordEntity, Long> {
    // 화면에 뿌려줄 때: 아침/점심/저녁 타입이 포함된 키워드 찾기
    // availableMealTypes 컬럼에 "LUNCH"가 포함되어 있는지 검색 (LIKE 검색)
    List<KeywordEntity> findAllByAvailableMealTypesContaining(String mealType);

    // Enum 이름으로 찾기
    Optional<KeywordEntity> findByName(String name);
}

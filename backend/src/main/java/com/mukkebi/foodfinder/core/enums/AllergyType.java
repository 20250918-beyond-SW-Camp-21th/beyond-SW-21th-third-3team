package com.mukkebi.foodfinder.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllergyType {

    PEANUT,      // 땅콩
    TREE_NUT,    // 견과류 (아몬드, 호두, 캐슈넛 등)
    MILK,        // 우유 / 유제품
    EGG,         // 계란
    WHEAT,       // 밀 (글루텐)
    SOY,         // 대두
    SHRIMP,      // 새우
    CRAB,        // 게
    SHELLFISH,   // 조개류
    FISH,        // 생선
    PORK,        // 돼지고기
    CHICKEN,     // 닭고기
    BEEF         // 소고기
}

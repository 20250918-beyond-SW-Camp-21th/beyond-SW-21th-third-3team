package com.mukkebi.foodfinder.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllergyType {

    // 주요 식품 알레르기
    PEANUT("알레르기", "땅콩"),
    TREE_NUT("알레르기", "견과류(아몬드/호두/캐슈넛 등)"),
    MILK("알레르기", "우유/유제품"),
    EGG("알레르기", "계란"),
    WHEAT("알레르기", "밀(글루텐)"),
    SOY("알레르기", "대두"),
    SHRIMP("알레르기", "새우"),
    CRAB("알레르기", "게"),
    SHELLFISH("알레르기", "조개류"),
    FISH("알레르기", "생선"),
    PORK("알레르기", "돼지고기"),
    CHICKEN("알레르기", "닭고기"),
    BEEF("알레르기", "소고기"),

    // 식이 제한
    VEGAN("식이제한", "비건"),
    LACTOSE_FREE("식이제한", "락토-프리"),
    GLUTEN_FREE("식이제한", "글루텐-프리");

    private final String group;       // "알레르기", "식이제한"
    private final String displayName; // 화면에 보여줄 이름 (한글)
}

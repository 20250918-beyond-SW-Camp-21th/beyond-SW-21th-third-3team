-- =====================================================
-- 해시태그 초기 데이터 삽입 스크립트
-- 프론트엔드 constants/hashtags.js 기준
-- =====================================================

-- 기존 데이터 삭제
DELETE FROM hash_tag_meal_types;
DELETE FROM hash_tag;

-- =====================================================
-- hash_tag 테이블 데이터 삽입
-- =====================================================

-- 기분/상황 카테고리
INSERT INTO hash_tag (id, code, label, prompt_message, created_at, updated_at, status) VALUES
(1, 'COMFORT', '든든하게', '오후 일정을 버틸 수 있도록 고기나 밥 위주의 양이 푸짐하고 든든한', NOW(), NOW(), 'ACTIVE'),
(2, 'LIGHT', '가볍게', '아침에 먹어도 부담스럽지 않고 소화가 잘 되는 깔끔하고 가벼운', NOW(), NOW(), 'ACTIVE'),
(3, 'SPECIAL', '특별한 날', '오늘 하루 고생한 나에게 보상해주는 고급스럽고 특별한 분위기의', NOW(), NOW(), 'ACTIVE'),
(4, 'QUICK', '빠르게', '주문하면 금방 나오고 회전율이 빨라 소중한 시간을 아낄 수 있는', NOW(), NOW(), 'ACTIVE'),
(5, 'HEALTHY', '건강하게', '칼로리가 낮고 샐러드나 건강식 위주로 구성된 신선하고 건강한', NOW(), NOW(), 'ACTIVE'),

-- 음식 종류 카테고리
(6, 'KOREAN', '한식', '한국 전통 음식을 선호합니다. 밥, 국, 반찬이 있는 정식이나 찌개, 불고기, 비빔밥 등을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(7, 'CHINESE', '중식', '중국 요리를 선호합니다. 짜장면, 짬뽕, 탕수육, 마라탕 등 중화요리를 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(8, 'JAPANESE', '일식', '일본 요리를 선호합니다. 초밥, 라멘, 돈카츠, 우동 등 일식을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(9, 'WESTERN', '양식', '서양 요리를 선호합니다. 파스타, 스테이크, 피자, 햄버거 등 양식을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(10, 'ASIAN', '아시안', '동남아시아 요리를 선호합니다. 쌀국수, 팟타이, 커리 등 아시안 음식을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(11, 'SNACK', '분식', '분식을 선호합니다. 떡볶이, 김밥, 라면, 순대 등 간단한 분식을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),
(12, 'CAFE', '카페', '카페 음식을 선호합니다. 브런치, 샌드위치, 샐러드와 함께 커피를 즐길 수 있는 곳을 추천해주세요.', NOW(), NOW(), 'ACTIVE'),

-- 맛 카테고리
(13, 'SPICY', '매운맛', '하루의 스트레스가 확 풀릴 정도로 입안이 얼얼하고 매운 자극적인', NOW(), NOW(), 'ACTIVE'),
(14, 'MILD', '순한맛', '맵지 않고 부드러워서 누구나 편하게 먹을 수 있는 순한', NOW(), NOW(), 'ACTIVE'),
(15, 'SAVORY', '고소한맛', '고소하고 담백한 맛이 일품인', NOW(), NOW(), 'ACTIVE'),
(16, 'SWEET', '달콤한맛', '달콤하고 디저트 느낌이 나는', NOW(), NOW(), 'ACTIVE'),

-- 가격대 카테고리
(17, 'BUDGET', '가성비', '주머니 사정이 가벼워도 배부르고 맛있게 먹을 수 있는 가성비 좋은', NOW(), NOW(), 'ACTIVE'),
(18, 'PREMIUM', '프리미엄', '가격대가 있지만 그만큼 퀄리티가 보장되는 고급스러운', NOW(), NOW(), 'ACTIVE'),

-- 상황 카테고리
(19, 'ALONE', '혼밥', '혼자 가도 눈치 보지 않고 편안하게 식사할 수 있는 1인석이 있거나 조용한', NOW(), NOW(), 'ACTIVE'),
(20, 'DATE', '데이트', '조명이 은은하고 인테리어가 예뻐서 대화 나누기 좋은 로맨틱한', NOW(), NOW(), 'ACTIVE'),
(21, 'GROUP', '단체', '여러 명이 함께 가도 넉넉하게 앉을 수 있고 다양한 메뉴가 있는', NOW(), NOW(), 'ACTIVE'),
(22, 'MEETING', '회식', '회식하기 좋은 분위기에 술과 안주가 맛있는', NOW(), NOW(), 'ACTIVE');

-- =====================================================
-- hash_tag_meal_types 테이블 데이터 삽입
-- =====================================================

-- 1. COMFORT (든든하게) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (1, 'LUNCH'), (1, 'DINNER');

-- 2. LIGHT (가볍게) - BREAKFAST, LUNCH
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (2, 'BREAKFAST'), (2, 'LUNCH');

-- 3. SPECIAL (특별한 날) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (3, 'LUNCH'), (3, 'DINNER');

-- 4. QUICK (빠르게) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (4, 'BREAKFAST'), (4, 'LUNCH'), (4, 'DINNER');

-- 5. HEALTHY (건강하게) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (5, 'BREAKFAST'), (5, 'LUNCH'), (5, 'DINNER');

-- 6. KOREAN (한식) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (6, 'BREAKFAST'), (6, 'LUNCH'), (6, 'DINNER');

-- 7. CHINESE (중식) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (7, 'LUNCH'), (7, 'DINNER');

-- 8. JAPANESE (일식) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (8, 'LUNCH'), (8, 'DINNER');

-- 9. WESTERN (양식) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (9, 'LUNCH'), (9, 'DINNER');

-- 10. ASIAN (아시안) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (10, 'LUNCH'), (10, 'DINNER');

-- 11. SNACK (분식) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (11, 'LUNCH'), (11, 'DINNER');

-- 12. CAFE (카페) - BREAKFAST, LUNCH
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (12, 'BREAKFAST'), (12, 'LUNCH');

-- 13. SPICY (매운맛) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (13, 'LUNCH'), (13, 'DINNER');

-- 14. MILD (순한맛) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (14, 'BREAKFAST'), (14, 'LUNCH'), (14, 'DINNER');

-- 15. SAVORY (고소한맛) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (15, 'BREAKFAST'), (15, 'LUNCH'), (15, 'DINNER');

-- 16. SWEET (달콤한맛) - BREAKFAST, LUNCH
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (16, 'BREAKFAST'), (16, 'LUNCH');

-- 17. BUDGET (가성비) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (17, 'BREAKFAST'), (17, 'LUNCH'), (17, 'DINNER');

-- 18. PREMIUM (프리미엄) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (18, 'LUNCH'), (18, 'DINNER');

-- 19. ALONE (혼밥) - BREAKFAST, LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (19, 'BREAKFAST'), (19, 'LUNCH'), (19, 'DINNER');

-- 20. DATE (데이트) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (20, 'LUNCH'), (20, 'DINNER');

-- 21. GROUP (단체) - LUNCH, DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (21, 'LUNCH'), (21, 'DINNER');

-- 22. MEETING (회식) - DINNER
INSERT INTO hash_tag_meal_types (hash_tag_id, meal_type) VALUES (22, 'DINNER');

-- =====================================================
-- 시퀀스 업데이트 (PostgreSQL)
-- =====================================================
SELECT setval('hash_tag_id_seq', 22, true);

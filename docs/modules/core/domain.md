# :core:domain

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

domain이 필요로 하는 repository contract와 재사용 가능한 비즈니스 흐름을 표현하는 usecase를 둔다.

## 포함 대상

- domain이 요구하는 repository contract.
- 여러 repository 조합, 재사용 비즈니스 흐름, validation·정렬·필터링·상태 계산을 수행하는 usecase.
- domain model을 입력과 반환으로 사용하는 비즈니스 규칙.

## 제외 대상

- repository 구현체, DTO, Entity, Retrofit, Room, DataStore, Hilt binding module.
- Android, Compose, Navigation 의존 코드와 화면 전용 UI state 가공.

## 공개 경계와 의존성

- repository contract는 data와 feature가 함께 사용하므로 공개한다.
- repository는 DTO·Entity·DAO·API를 노출하지 않고 `:core:model`의 순수 모델을 사용한다.
- 새 usecase는 단순 repository 한 번 호출 후 반환하는 위임만 필요할 때는 만들지 않는다. 기존 usecase 이동 단계에서는 로직을 재작성하지 않는다.
- domain은 `:core:model`에만 의존하며 data와 feature에 의존하지 않는다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/domain). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

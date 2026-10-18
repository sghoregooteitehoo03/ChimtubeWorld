# :core:model

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

navigation parameter, feature public contract, 여러 모듈에서 공유하는 순수 상태 모델을 제공한다.

## 포함 대상

- feature API가 navigation contract로 노출하는 모델.
- domain repository와 usecase가 반환하는 순수 모델.
- 여러 feature에서 공통으로 사용하는 Android 비의존 상태 모델.

## 제외 대상

- Room Entity, Retrofit DTO, Android와 Compose 의존 타입.
- 특정 feature 내부에서만 쓰는 `UiState`, 화면 표시 전용 가공 모델, data 구현 세부 타입.

## 공개 경계와 의존성

- model은 feature API와 domain contract에 노출될 수 있으므로 기본 공개 범위를 사용한다.
- model은 다른 core, feature, app 모듈에 의존하지 않는다.
- DTO와 Entity 변환은 `:core:data` mapper에서 담당한다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/data/model). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

# :core:ui

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

feature가 공통으로 사용하는 MVI 기반 `BaseViewModel`과 `:core:model`을 표시하는 공통 Compose View를 제공한다.

## 포함 대상

- `UiState`, `UiEvent`, `UiSideEffect` contract.
- 읽기 전용 state와 side effect stream, `currentState`, `reduce`, `postSideEffect`를 제공하는 `BaseViewModel`.
- 여러 feature에서 재사용하는 model 기반 Compose View.

## 제외 대상

- Hilt ViewModel 생성, feature 전용 UI state, 화면 조합, navigation, domain/data/network 저장소 접근.
- model을 feature 요구에 맞게 별도 UI model로 변환하는 정책.

## 공개 경계와 의존성

- Contract와 `BaseViewModel`은 feature ViewModel이 구현·상속하므로 공개한다.
- 내부 mutable flow는 private으로 유지하고 외부에는 읽기 전용 `StateFlow`와 `SharedFlow`만 노출한다.
- 공통 View는 공유 순수 모델을 직접 받아 표시한다. feature 전용 component와 Screen은 `:core:ui`에 두지 않는다.
- ui는 `:core:model`, `:core:designsystem`, Lifecycle·Coroutine에만 의존한다. domain과 data 계층에는 의존하지 않는다.

## Side effect와 테스트

- `BaseViewModel.sideEffect`는 replay 없이 동작한다. `extraBufferCapacity = 1`은 구독자가 있을 때의 추가 버퍼이며, 구독자가 없을 때 발생한 effect를 나중에 재생하는 저장소가 아니다.
- navigation·Snackbar처럼 현재 수집자에게 전달할 단발성 동작에 사용한다. 반드시 복원·재표시해야 하는 결과나 완료 여부는 해당 화면의 state 등 별도 지속 상태로 표현한다.
- 테스트는 event를 발생시키기 전에 수집을 시작한다. `backgroundScope.launch(UnconfinedTestDispatcher(testScheduler))`로 같은 scheduler에서 수집을 먼저 시작하고, 생산 작업을 진행한 뒤 관찰한 effect를 확인한다.
- 수집 작업과 ViewModel coroutine은 테스트 종료 시 정리한다. 현재 stream 동작을 테스트 편의에 맞춰 변경하지 않는다.

작성 방법과 향후 화면 테스트는 [테스트 가이드](../../development/testing.md)를 따른다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/presentation/ReUseableView.kt). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

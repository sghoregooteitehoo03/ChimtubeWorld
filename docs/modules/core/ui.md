# :core:ui

namespace/package는 `com.sghore.chimtubeworld.core.ui`이며 library·library.compose convention을 적용한다.

## 역할

feature가 공통으로 사용하는 MVI 기반 `BaseViewModel`과 `:core:model`을 표시하는 공통 Compose View와 Android UI 실행 helper를 제공한다.

## 포함 대상

- `UiState`, `UiEvent`, `UiSideEffect` contract.
- 읽기 전용 state와 side effect stream, `currentState`, `reduce`, `postSideEffect`를 제공하는 `BaseViewModel`.
- 여러 feature에서 재사용하는 model 기반 Compose View와 Paging Compose 목록.
- Context·Intent·Custom Tabs 기반 외부 앱 실행 helper `OpenOtherApp`. 기존 앱 실행·웹 fallback·실패 토스트 동작을 유지한다.

## 제외 대상

- Hilt ViewModel 생성, feature 전용 UI state, 화면 조합, navigation, domain/data/network 저장소 접근.
- model을 feature 요구에 맞게 별도 UI model로 변환하는 정책.

## 공개 경계와 의존성

- Contract와 `BaseViewModel`은 feature ViewModel이 구현·상속하므로 공개한다.
- 내부 mutable flow는 private으로 유지하고 외부에는 읽기 전용 `StateFlow`와 `SharedFlow`만 노출한다.
- 공통 View는 공유 순수 모델을 직접 받아 표시한다. feature 전용 component와 Screen은 `:core:ui`에 두지 않는다.
- ui는 `:core:model`, `:core:designsystem`, Compose·Paging Compose·Lifecycle·Coroutine·AndroidX Core KTX·Browser에 의존한다. Core KTX와 Browser는 OpenOtherApp 구현 의존성이다. 공개 시그니처에 나타나는 ViewModel·LazyPagingItems·StateFlow·SharedFlow를 위해 Lifecycle ViewModel KTX·Paging Compose·Coroutines Android는 `api`로 제공한다. domain과 data 계층에는 의존하지 않는다.

## Side effect와 테스트

- `BaseViewModel.sideEffect`는 replay 없이 동작한다. `extraBufferCapacity = 1`은 구독자가 있을 때의 추가 버퍼이며, 구독자가 없을 때 발생한 effect를 나중에 재생하는 저장소가 아니다.
- navigation·Snackbar처럼 현재 수집자에게 전달할 단발성 동작에 사용한다. 반드시 복원·재표시해야 하는 결과나 완료 여부는 해당 화면의 state 등 별도 지속 상태로 표현한다.
- 테스트는 event를 발생시키기 전에 수집을 시작한다. `backgroundScope.launch(UnconfinedTestDispatcher(testScheduler))`로 같은 scheduler에서 수집을 먼저 시작하고, 생산 작업을 진행한 뒤 관찰한 effect를 확인한다.
- 수집 작업과 ViewModel coroutine은 테스트 종료 시 정리한다. 현재 stream 동작을 테스트 편의에 맞춰 변경하지 않는다.

작성 방법과 화면 테스트는 [테스트 가이드](../../development/testing.md)를 따른다.

## 현재 포함된 구현과 경계

- `base/BaseContract.kt`, `base/BaseViewModel.kt`: 여섯 feature의 Contract·ViewModel이 사용하는 MVI 기반 타입이다. stream 수명과 구독 정책은 위 기준을 따른다.
- `PagingRowList.kt`: `LazyPagingItems`를 받고 designsystem의 `RowItemCollocate`를 조합한다.
- `BottomNavigationBar`는 app의 `presentation/ReUseableView.kt`에 둔다. 앱 탭 정의를 사용하는 navigation chrome이다. 모델·navigation에 의존하지 않는 `ScreenScaffold`는 designsystem이 제공한다.
- `OpenOtherApp.kt`: 공유 Android UI helper로 각 feature EntryProvider가 Route에서 전달받은 외부 실행 side effect를 연결한다. 패키지·스킴·웹 fallback·실패 토스트 동작을 보존하며 순수 Kotlin common에는 두지 않는다.

## 현재 구현 참고

- [현재 소스](../../../core/ui/src/main/java/com/sghore/chimtubeworld/core/ui)
- [app chrome](../../../app/src/main/java/com/sghore/chimtubeworld/presentation/ReUseableView.kt)
- 빌드 설정: [build.gradle.kts](../../../core/ui/build.gradle.kts). 구현 예제는 위 현재 소스와 모듈 경계 규칙을 따른다.

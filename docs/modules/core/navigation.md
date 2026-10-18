# :core:navigation

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

Navigation 3의 top-level stack, 탭별 sub-stack, back stack 조작, navigation 결과 전달을 공통으로 제공한다. feature route 정의와 화면 조합은 소유하지 않는다.

## 포함 대상

- `NavigationState`, `rememberNavigationState`, `toEntries`.
- `Navigator`의 이동, 뒤로가기, `popUpTo`, 결과 전달 API.
- 타입 안전한 `NavigationResultKey<T>`.
- NavDisplay entry의 saveable state와 ViewModelStore 유지.

## 제외 대상

- feature별 `NavKey`, feature 이동 확장 함수, EntryProvider, Screen, bottom navigation UI.
- route별 중복 진입 방지, 완료 후 복귀 같은 feature 흐름 정책.

## Navigation 정책

- 현재 탭을 다시 선택하면 현재 sub-stack을 root만 남기고 비운다.
- 다른 top-level 탭을 선택하면 그 탭의 sub-stack을 유지한다.
- top-level key가 아닌 key 이동은 현재 sub-stack 끝에 추가한다. 동일 key를 자동 제거하지 않는다.
- 뒤로가기는 child 화면을 먼저 pop하고, 탭 root에서는 이전 top-level 탭으로 이동한다. 시작 탭 root에서는 `false`를 반환한다.
- `popUpTo`는 현재 sub-stack에만 적용한다.
- result는 `NavigationResultKey<T>`로 저장하고 `consumeResult`에서 읽은 즉시 제거한다. 프로세스 복원 범위는 아니다.

## 공개 경계와 의존성

- feature API는 `:core:navigation`을 통해 `NavKey`, `Navigator`, result key를 public contract로 노출한다.
- feature impl은 대응 feature API의 공개 의존성을 통해 navigation type을 사용한다.
- app은 top-level state와 EntryProvider를 조립하고 `NavDisplay`에 `toEntries` 결과를 전달한다.
- navigation은 model, domain, data, feature, app에 의존하지 않는다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/NavigationScreen.kt). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

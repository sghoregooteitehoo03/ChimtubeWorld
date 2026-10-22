# :core:navigation

Step 11에서 템플릿의 stack·Navigator 정책을 도입했다. 패키지는 `com.sghore.chimtubeworld.core.navigation`이다. 화면 key와 entry는 feature 분리 전까지 [app/navigation](../../../app/src/main/java/com/sghore/chimtubeworld/navigation)에 둔다.

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
- 다른 top-level 탭을 선택하면 그 탭의 sub-stack을 유지한다. 이미 방문한 탭은 top-level 이력에서 제거 후 끝에 추가하고, 시작 탭(YouTube)을 선택하면 top-level 이력만 시작 탭으로 초기화한다.
- top-level key가 아닌 key 이동은 현재 sub-stack 끝에 추가한다. 동일 key를 자동 제거하지 않는다.
- 뒤로가기는 child 화면을 먼저 pop하고, 탭 root에서는 이전 top-level 탭으로 이동한다. 시작 탭 root에서는 `false`를 반환하며 NavDisplay가 시스템 뒤로가기를 가로채지 않아 앱을 종료한다. 현재 탭 재선택과 탭 root 뒤로가기는 Step 11의 의도된 정책 변경이다.
- `popUpTo`는 현재 sub-stack에만 적용한다.
- result는 `NavigationResultKey<T>`로 저장하고 `consumeResult`에서 읽은 즉시 제거한다. 프로세스 복원 범위는 아니다.

## 공개 경계와 의존성

- feature API는 `:core:navigation`을 통해 `NavKey`, `Navigator`, result key를 public contract로 노출한다.
- feature impl은 대응 feature API의 공개 의존성을 통해 navigation type을 사용한다.
- app은 top-level state와 EntryProvider를 조립하고 `NavDisplay`에 `toEntries` 결과를 전달한다.
- navigation은 model, domain, data, feature, app에 의존하지 않는다. Android library·Compose convention을 적용하고 navigation3-runtime은 `api`, lifecycle-viewmodel-navigation3는 `implementation`으로 제공한다.

## 현재 구현 참고

- [NavigationState](../../../core/navigation/src/main/java/com/sghore/chimtubeworld/core/navigation/NavigationState.kt), [Navigator](../../../core/navigation/src/main/java/com/sghore/chimtubeworld/core/navigation/Navigator.kt), [NavigatorTest](../../../core/navigation/src/test/java/com/sghore/chimtubeworld/core/navigation/NavigatorTest.kt).
- 5개 탭은 YouTube·Chzzk·Webtoon·Cafe·Store 순서다. `PlaylistsKey`, `VideosKey`, `AddBookmarkKey`, `EditBookmarkKey`도 현재 app이 소유한다. feature 분리 시 각 API의 navigation 계약과 impl EntryProvider로 옮긴다.
- `VideoSource` enum이 source를 표현하며 drawable ID를 key에 저장하지 않는다. EditBookmark는 직렬화 가능한 model의 `Video`·`Bookmark`를 사용한다.
- `BookmarkResultKey`의 결과는 현재 표시 중인 Videos entry가 `consumeResult`로 한 번 소비하고 기존 ViewModel의 목록 갱신 함수로 전달한다. 결과는 메모리 수명이며 프로세스 재생성 시 목록은 기존 repository 조회로 다시 로딩한다.
- 공유로 생성하는 AddBookmark key에는 URL과 요청 ID를 보관한다. 같은 URL의 연속 공유도 서로 다른 entry·ViewModel을 사용한다.

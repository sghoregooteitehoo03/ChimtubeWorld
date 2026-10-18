# App 작업 규칙

## 현재 상태

현재 `:app`은 화면·데이터·DI·navigation 구현을 모두 포함한다. [기존 MainActivity](../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/MainActivity.kt)와 [NavigationScreen](../../app/src/main/java/com/sghore/chimtubeworld/presentation/ui/NavigationScreen.kt)을 참고한다.

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**: 아래 책임은 목표 구조다. [기준선](../migration/baseline.md)의 기존 동작을 유지하며 단계별로 옮긴다. 현재 GlobalViewModel·공통 TopAppBar·문자열 route를 이 문서만을 근거로 바꾸지 않는다.

## 목표 책임

- `:app`은 composition root다. 화면 기능이나 feature 전용 UI state를 구현하지 않는다.
- MainActivity는 앱 theme와 root composable을 시작한다. 앱 전역 ViewModel은 앱 수준 UI 요구가 있을 때만 사용한다.
- app 조립 계층은 navigation 상태와 Navigator를 만들고 feature EntryProvider를 NavDisplay에 전달한다.

## Navigation chrome

- top-level 탭 정의는 app이 소유한다. app은 탭 조립에 한해 feature API의 top-level NavKey를 직접 참조할 수 있다.
- 탭 표시·뒤로가기·공유 인텐트 진입은 기존 동작을 보존한다. Navigation 3 전환 시 [navigation 규칙](core/navigation.md)을 적용하고 회귀를 확인한다.
- 목표 구조에서는 화면 제목과 뒤로가기를 feature Screen이 소유한다. 공통 UI 변경은 별도 전환 단계에서 수행한다.
- 공통 아이콘은 designsystem drawable을 `R.drawable`로 직접 참조하며 별도 icon 단일 진입점을 만들지 않는다.

## 의존성 조립

- app은 앱 그래프에 필요한 data·common Hilt binding과 화면을 제공하는 feature API·impl에 의존한다.
- data 구현과 저장소·네트워크 세부사항을 app 화면 코드에서 직접 사용하지 않는다.
- feature 간 이동은 대상 feature API의 확장 함수로 연결한다. app의 탭 선택은 등록된 NavKey를 Navigator에 전달할 수 있다.
- 현재 적용한 convention과 app에 남기는 설정은 [build-logic 규칙](../development/build-logic.md), 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.

# Feature 작업 규칙

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 화면은 `app`의 [presentation](../../app/src/main/java/com/sghore/chimtubeworld/presentation)에 있다. 아래는 목표 구조이며 기존 상태와 이동 제약은 [기준선](../migration/baseline.md)을 따른다. 이동 단계에서는 로직을 유지하고, MVI·문자열 리소스·테스트는 이후 전환 단계에서 적용한다.


Step 11에서 NavKey·이동 확장 함수·EntryProvider 및 assisted ViewModel 생성은 단일 app 안에서 먼저 적용했다. 현재 위치는 [app/navigation](../../app/src/main/java/com/sghore/chimtubeworld/navigation)이며, feature 분리 시 아래 API·impl 경계로 옮긴다. MVI 전환은 이번 단계에 포함하지 않는다.

## 모듈 경계

- 대상 feature는 `youtube`(playlist·videos 포함), `chzzk`, `webtoon`, `cafe`, `store`, `bookmark`다.
- feature는 `api`와 `impl`을 분리한다. API는 `@Serializable NavKey`, Navigator 확장 함수, navigation result key만 제공한다.
- 호출 모듈은 구체 NavKey를 직접 생성하지 않고 API의 `navigateTo...` 확장 함수를 사용한다.
- impl은 대응 API와 이동 대상 feature API에 직접 의존한다. API에는 Compose UI·ViewModel·data 구현을 두지 않는다.
- navigation parameter는 최소 식별자 또는 core model의 순수 공유 모델로 제한한다. 결과 key는 feature API가 `NavigationResultKey<T>` object로 선언한다.

## API·Impl 구조

```text
feature/<name>/api/
  navigation/<Name>Navigation.kt
feature/<name>/impl/
  <name>_screen/
    <Name>Contract.kt
    <Name>Route.kt
    <Name>Screen.kt
    <Name>ViewModel.kt
    component/<Name>Component.kt
  navigation/<Name>EntryProvider.kt
```

- 한 화면의 Contract·Route·Screen·ViewModel을 `<name>_screen`에 함께 둔다.
- navigation에는 app의 NavDisplay 조립에 쓰는 EntryProvider만 둔다.
- component는 feature 내부 재사용에 한정한다. 여러 feature가 쓰는 component는 designsystem 또는 ui 승격을 검토한다.

## MVI와 Compose 화면

- Contract는 UiState·UiEvent·UiSideEffect를 구현한다. 지속 값은 state, 입력은 event, navigation·Snackbar·외부 앱 실행은 side effect에 둔다.
- ViewModel은 BaseViewModel을 상속하고 onEvent에서 처리한다. 비즈니스 규칙은 domain에 둔다.
- Route는 state 수집과 외부 콜백으로의 side effect 전달만 담당하며 Navigator를 받지 않는다. 화면 진입 인자를 event로 보내 최초 조회를 시작하지 않는다.
- EntryProvider 또는 app이 side effect 콜백을 navigator 동작으로 연결한다.
- Screen은 uiState·onEvent만 받아 레이아웃과 component를 조합한다. Screen·component는 ViewModel·Hilt·Navigator를 직접 알지 않는다.
- component는 표시와 입력 전달만 담당하며 화면 전용 상태 조합·navigation·ViewModel 생성을 넣지 않는다.
- 서로 다른 Kotlin 선언과 논리 블록 사이에는 공백 한 줄을 둔다.

## 화면 진입 인자와 ViewModel 생성

- 인자 없는 Route는 `hiltViewModel()` 기본 파라미터로 ViewModel을 얻는다.
- 인자 있는 화면은 API의 직렬화 가능한 NavKey에 식별자를 둔다. ViewModel은 `@AssistedInject`, `@Assisted`, `@HiltViewModel(assistedFactory = …)`, `@AssistedFactory`로 생성한다.
- EntryProvider가 entry의 ViewModelStore를 사용하는 `hiltViewModel<VM, Factory>(creationCallback = …)`에서 factory.create를 호출하고 Route의 필수 인자로 전달한다. Factory를 별도 주입해 직접 ViewModel을 만들지 않는다.
- 최초 조회는 ViewModel 초기화에서 시작한다. 재시도는 생성자의 동일 식별자를 사용한다. 재구성 때 최초 조회 event를 다시 보내지 않는다.
- 같은 entry의 ViewModel이 유지되면 assisted 인자를 다시 주입하지 않는다. 다른 식별자는 새 NavKey로 진입한다.
- assisted 인자 자체는 저장되지 않는다. 목표 구조에서는 rememberNavBackStack에 직렬화 가능한 NavKey를 보관하고 복원된 entry에서 같은 인자로 ViewModel을 생성한다. stack 수명은 [navigation 규칙](core/navigation.md)을 따른다.
- 단위 테스트는 ViewModel을 직접 생성한다. Hilt 연결의 컴파일 확인과 실제 재생성·프로세스 복원 검증은 구분한다.

## 화면 문자열과 오류 상태

- 제목·버튼·입력 라벨·접근성 설명·오류 문구는 impl의 `src/main/res/values/strings.xml`이 소유한다. 이름에 feature 접두사를 사용하고 Screen에서 stringResource로 읽는다.
- 데이터 삽입은 리소스 형식 인자를 사용한다. 서버 데이터와 사용자 입력은 문자열 데이터로 유지한다.
- ViewModel은 표시 문구나 리소스 ID 대신 의미 있는 상태를 전달한다. 오류 종류에 맞는 표현을 선택하며 기존 실패·빈 응답 정책을 임의로 바꾸지 않는다.
- app 탭 이름은 app 리소스가 소유한다. 문구가 같다는 이유만으로 공통 UiText 추상화나 공통 문자열을 만들지 않는다.

## Preview

- 모든 Screen과 시각적 component에 Preview를 둔다. 샘플 UiState·빈 onEvent·샘플 콜백으로 그린다.
- Hilt 주입과 side effect 수집을 맡는 Route는 Preview 대상이 아니다.

## 새 feature 추가 절차

1. settings에 `:feature:<name>:api`와 `:feature:<name>:impl`을 등록한다. namespace는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.
2. api에는 `alias(libs.plugins.android.feature.api)`, impl에는 `alias(libs.plugins.android.feature.impl)`을 적용한다. 전제 core 모듈이 먼저 생성돼 있어야 한다.
3. 각 모듈에 빈 `<manifest />`를 둔다. impl은 대응 API와 이동 대상 API를 의존하며 convention 의존성을 다시 선언하지 않는다.
4. API의 navigation contract, impl의 화면·문자열·Preview·EntryProvider를 연결한다.
5. app에 API·impl 의존성을 추가하고 root composable의 entryProvider에 등록한다. top-level 탭이면 app 탭 정의·문자열·아이콘도 연결한다.
6. 분기 있는 로직과 상태별 Screen 테스트는 [테스트 가이드](../development/testing.md)를 따른다.
7. 대상 모듈 compile·test 후 app assembleDebug를 확인한다. 명령과 완료 보고는 [품질 규칙](../development/quality.md)을 따른다.

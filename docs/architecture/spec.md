# 프로젝트 명세

## 목적과 현재 상태

ChimtubeWorld의 기존 기능과 배포 호환성을 유지하며 화면 기능을 `feature`, 공유 계약과 기반 기능을 `core`로 분리한다.

- 현재 등록된 모듈은 `:app`, `:core:common`, `:core:model`, `:core:database`, `:core:network`, `:core:domain`, `:core:data`, `:core:testing`, `:core:designsystem`, `:core:ui`, `:core:navigation`이다. Step 7에서 repository contract 4개를 domain에, repository 구현·PagingSource·mapper·Firestore·Jsoup을 data에 추출했다. 단순 위임·UI용 오류 래핑만 수행하던 usecase는 제거했으며 현재 usecase는 없다.
- Step 8에서 testing에 domain 계약 기반 Fake 4개·공유 모델 fixture 8개·MainDispatcherRule과 자체 테스트를 추가했다. data의 repository·mapper·PagingSource 단위 테스트도 추가했다. Jsoup 직접 크롤링·Firestore 채널 조회 등 제외 경계는 [테스트 가이드](../development/testing.md#현재-테스트-예제와-경계)에 기록한다. 테스트 실행·app 빌드 통과는 별도로 검증한다.
- Step 9에서 Material 2 theme·모델 독립 공통 component·공유 drawable·주/야간 colors를 designsystem으로, PagingRowList·미사용 MVI base 타입을 ui로 추출했다. 이동 당시 UI·문자열 route·상태 관리는 그대로 유지했다.
- Step 10에서 theme을 Material 3 ColorScheme·Typography·Shapes로 바꾸고 app·공통 component의 Material 2 사용을 Material 3로 전환했다. 색상 역할·타이포그래피 매핑과 외형 보존 기준은 [designsystem 문서](../modules/core/designsystem.md)를 따른다. 직접 Material 2·accompanist-insets-ui 의존성은 제거했으며 toolbar-compose의 런타임 전이 Material 2 의존성은 유지한다. 시스템 바 처리·인셋·문자열 route·상태 관리는 보존한다. 빌드·라이트/다크 실기기 비교는 로컬 검증이 필요하다.
- Step 11에서 단일 app 내 Navigation 3 전환과 `:core:navigation` 도입을 구현했다. NavKey·EntryProvider는 feature 분리 전까지 app/navigation에 두며, 인자 있는 ViewModel은 AssistedInject, 북마크 결과는 NavigationResultKey로 전달한다. 각 Screen이 기존 외형의 상단바를 소유하고 GlobalViewModel·문자열 route를 제거했다. 탭 재선택·root 뒤로가기는 [navigation 정책](../modules/core/navigation.md)을 적용한다. 빌드·테스트 및 기기 스모크 결과는 별도 검증이 필요하다.
- 현재 의존성은 app → common·model·domain·data·designsystem·ui·navigation, ui → model·designsystem, data → domain·model·common·network·database, domain → model이다. testing → domain은 공개 테스트 API이며 data → testing은 `testImplementation`이다. production 소비자는 없다. common·model·database·network는 다른 프로젝트 모듈에 의존하지 않는다. `Bookmark`·`Video`는 순수 모델로 model에, `BookmarkEntity`·`ReadHistoryEntity`는 database에 둔다. 읽음 기록은 데이터 계층 내부에서만 사용하므로 별도 `ReadHistory` 모델은 없다.
- app ViewModel은 domain repository contract를 직접 사용한다. data의 internal 구현체를 `DataModule`에서 Hilt binding하며, Firestore singleton provider는 data의 `FirestoreModule`에 둔다. Room provider는 database, OkHttp·API별 Retrofit 서비스와 RemoteDataSource binding은 network에 둔다. DTO·Entity → model 변환, 기존 Firestore 쿼리와 Jsoup 접근은 data 내부에 유지한다. app의 빈 `AppModule`·`AssistedInjectModule`은 제거했다. `OpenOtherApp`은 Context 기반 외부 실행 helper로 Step 9에서도 app에 유지하며 feature 분리 시 실행 콜백 경계를 재검토한다.
- 앱 UI는 Material 3·Navigation 3를 사용한다. 화면별 `*ScreenState`와 개별 함수 호출은 유지하며 feature 분리·MVI 전환은 후속 단계다.
- **마이그레이션 진행 중 — 대상 모듈 생성 후 적용**: 아래 feature 모듈 지도와 MVI 규칙은 목표 구조다. core 모듈은 모두 생성했다. 의존성은 추출된 모듈부터 적용한다. 데이터·식별자 보존 기준과 기존 상태는 [기준선](../migration/baseline.md), 동작 확인은 [스모크 체크리스트](../migration/smoke-checklist.md)를 따른다. database의 빌드·identity hash·덮어 설치 검증은 [database 문서](../modules/core/database.md#빌드-설정과-스키마-보존)에 따른다.
- 모듈 이동과 로직 재작성은 분리한다. core 추출과 Material 3·Navigation 3 전환 이후 feature 이동, MVI·테스트 적용은 각 후속 단계에서 수행한다.

## 아키텍처

- `app`은 composition root다. feature entry, Hilt 그래프, 앱 수준 navigation chrome을 조립한다.
- feature는 `api`와 `impl`로 분리한다. API는 navigation 계약을, impl은 화면과 화면 상태를 소유한다.
- core는 feature나 app에 의존하지 않는다.
- 비즈니스 데이터 흐름은 feature ViewModel → domain repository contract → data 구현이다. data 구현이 network·database 경계를 조합하며 Firestore도 data가 담당한다. usecase는 여러 repository를 조합하는 비즈니스 흐름이 필요할 때만 도입한다.
- repository의 suspend 조회·저장 예외는 호출자에게 전파한다. ViewModel이 이를 UI 상태로 변환하며, 현재는 기존 `Resource` 방출·오류 문구·출력 동작을 private Flow helper로 보존한다. 이 임시 래핑은 후속 MVI 단계에서 오류 상태 처리 방식으로 전환한다.
- 화면 상태의 목표는 `UiState`, `UiEvent`, `UiSideEffect`와 `BaseViewModel`을 사용하는 MVI다.

## 모듈 지도

| 목표 모듈 | 책임 |
| --- | --- |
| `:app` | 앱 조립과 top-level navigation. 현재 화면·ViewModel·navigation chrome 포함, 데이터 구현·theme·공통 View는 core로 추출 |
| `:core:common` | 공통 결과 래퍼·유틸·상수 추출 완료. dispatcher는 후속 도입 |
| `:core:model` | Bookmark·Video를 포함한 공유 순수 모델 8개. Bookmark·Video에 Kotlin Serialization 적용 |
| `:core:network` | YouTube·Cafe·Chzzk 서비스·전체 DTO·RemoteDataSource·네트워크 DI 추출. Gson·기존 요청 설정 유지 |
| `:core:database` | Room Entity 2개·DAO·Database·Room Hilt 제공 모듈 추출. 기존 v2 스키마 유지 |
| `:core:domain` | repository contract 4개. 현재 usecase 없음. 순수 Kotlin paging-common 예외 허용 |
| `:core:data` | internal repository 구현 4개·PagingSource 4개·mapper·Firestore provider 및 접근·Jsoup 추출 |
| `:core:testing` | domain repository Fake 4개·공유 모델 fixture 8개·MainDispatcherRule·자체 테스트. production 의존 금지 |
| `:core:designsystem` | Material 3 theme·모델 독립 공통 component·drawable·주/야간 colors |
| `:core:ui` | PagingRowList·미사용 MVI 기반 타입. model 기반 View는 후속 추가 |
| `:core:navigation` | Navigation 3 stack·Navigator·결과 전달·5개 탭 정책 테스트 도입 |
| `:feature:youtube:api`, `:feature:youtube:impl` | YouTube 채널·playlist·videos |
| `:feature:chzzk:api`, `:feature:chzzk:impl` | 치지직. 현재 `twitchScreen`·`TwitchRepository` 구현에서 이동 |
| `:feature:webtoon:api`, `:feature:webtoon:impl` | 웹툰 |
| `:feature:cafe:api`, `:feature:cafe:impl` | 카페 |
| `:feature:store:api`, `:feature:store:impl` | 스토어 |
| `:feature:bookmark:api`, `:feature:bookmark:impl` | 북마크 조회·추가·수정·선택 |

세부 책임과 공개 경계는 [문서 안내의 Core 목록](../README.md#core-문서), [feature 규칙](../modules/feature.md)을 따른다. DataStore는 사용하지 않으며 도입 대상이 아니다. 별도 Firebase 모듈은 만들지 않는다.

## 목표 의존성 방향

- app → 조립에 필요한 feature API·impl과 core.
- feature API → core model·navigation(`api`).
- feature impl → 대응 API·이동 대상 API, convention의 common·domain·designsystem·ui, 테스트 전용 testing.
- data → domain·model·common·network·database. domain → model. ui → model·designsystem.
- domain의 공개 모델·`Flow<PagingData<...>>`를 위해 model·coroutines-core·순수 Kotlin `paging-common`을 `api`로 제공한다. domain은 `Resource`·common·javax.inject에 의존하지 않으며 Paging runtime·Compose·Android UI 의존성도 추가하지 않는다. 이 이동 단계의 Paging 예외와 공개 메서드는 [domain 문서](../modules/core/domain.md)를 따른다.
- testing → domain(`api`), model은 domain의 `api`를 통해 제공한다. production에서는 testing에 의존하지 않는다.
- 나머지 core는 다른 프로젝트 모듈에 의존하지 않는다.

## 기술 선택

Kotlin·AGP·KSP, Compose, Hilt, Lifecycle·Coroutines, Retrofit·Gson·OkHttp, Room, Firestore, Paging, Jsoup를 유지한다. Moshi와 DataStore는 도입하지 않는다. Compose UI는 Material 3를 사용한다. Navigation 3·Kotlin Serialization은 Step 11에서 적용했다. Navigation Compose와 app의 Gson 의존성은 제거했으며 network의 Gson은 유지한다.

버전·SDK·plugin alias의 단일 기준은 [버전 카탈로그](../../gradle/libs.versions.toml)다. 앱 설정과 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.

# 프로젝트 명세

## 목적과 현재 구조

ChimtubeWorld는 배포 호환성을 유지하며 화면 기능을 `feature`, 공유 계약과 기반 기능을 `core`로 분리한다.

- `:app`은 화면·ViewModel 없이 앱 조립, navigation chrome, 공유 인텐트 처리를 담당한다.
- 여섯 feature는 API·impl로 분리하며 모두 Contract·BaseViewModel·onEvent, Route/Screen/EntryProvider 기반 MVI를 사용한다. 화면 문자열·Preview·ViewModel 및 Screen 테스트는 각 impl이 소유한다.
- 모든 NavKey·Navigator 이동 확장 함수·result key는 feature API가 공개한다. 각 impl은 모든 소유 key를 등록하는 `<name>Entry` 함수 하나만 공개한다. youtube impl은 bookmark api의 이동 함수·BookmarkResultKey를 사용하며 impl 간 의존은 없다.
- 인자 있는 playlist·videos·bookmark 화면은 AssistedInject와 entry의 creationCallback으로 생성한다. 최초 조회는 생성자 인자로 init에서 시작한다. 북마크 선택·삭제 확인은 UiState, navigation·외부 실행·Toast·클립보드는 Route가 전달하고 EntryProvider가 처리하는 side effect다. 외부 앱 실행은 core:ui의 OpenOtherApp을 사용한다.
- repository contract는 domain, internal 구현체·mapper·PagingSource·Firestore·Jsoup은 data가 소유한다. 단순 위임·UI 오류 래핑용 usecase는 만들지 않으며 현재 usecase는 없다. Firestore provider는 data, Room provider는 database, OkHttp·Retrofit·RemoteDataSource binding은 network에 둔다.
- 공유 Bookmark·Video·VideoSource는 model, 저장 Entity는 database에 둔다. 읽음 기록은 data 내부에서만 사용하며 Post.isRead로 노출한다.
- 공통 Fake·fixture·MainDispatcherRule은 testing, repository·mapper·PagingSource 단위 테스트는 data에 둔다. Jsoup·Firestore 등 제외 경계는 [테스트 가이드](../development/testing.md#현재-테스트-예제와-경계)를 따른다.
- Material 3 색상·타이포그래피 매핑은 [designsystem](../modules/core/designsystem.md), 탭·뒤로가기는 [navigation 정책](../modules/core/navigation.md)을 따른다. 데이터·식별자 보존은 [기준선](../migration/baseline.md), Room 검증은 [database 문서](../modules/core/database.md#빌드-설정과-스키마-보존)를 따른다.

## 아키텍처

- `app`은 composition root다. feature entry, Hilt 그래프, 앱 수준 navigation chrome을 조립한다.
- feature는 `api`와 `impl`로 분리한다. API는 navigation 계약을, impl은 화면과 화면 상태를 소유한다.
- core는 feature나 app에 의존하지 않는다.
- 비즈니스 데이터 흐름은 feature ViewModel → domain repository contract → data 구현이다. data 구현이 network·database 경계를 조합하며 Firestore도 data가 담당한다. usecase는 여러 repository를 조합하는 비즈니스 흐름이 필요할 때만 도입한다.
- repository의 suspend 조회·저장 예외는 호출자에게 전파한다. ViewModel은 suspendRunCatching으로 조회 실패를 isLoading·hasLoadError로 변환하고 Screen은 feature 문자열 리소스를 선택한다. 취소는 다시 던지고 기존 Exception 외 오류 전파와 출력 정책은 유지한다. Bookmark 저장·삭제 예외 전파와 Cafe 읽음 저장 실패 무시 정책도 유지한다. 공통 Resource 래퍼는 사용하지 않는다.
- 화면 상태는 `UiState`, `UiEvent`, `UiSideEffect`와 `BaseViewModel`을 사용하는 MVI다.

## 모듈 지도

| 모듈 | 책임 |
| --- | --- |
| `:app` | 앱·Hilt·feature entry 조립, top-level navigation·하단바, Activity·공유 인텐트 처리 |
| `:core:common` | Kotlin 유틸·상수·suspendRunCatching·dispatcher DI |
| `:core:model` | Bookmark·Video를 포함한 공유 순수 모델 8개와 VideoSource enum. Bookmark·Video·VideoSource에 Kotlin Serialization 적용 |
| `:core:network` | YouTube·Cafe·Chzzk 서비스·DTO·RemoteDataSource·네트워크 DI. Gson 유지 |
| `:core:database` | Room Entity 2개·DAO·Database·Room Hilt 제공. 기존 v2 스키마 유지 |
| `:core:domain` | repository contract 4개. 현재 usecase 없음. 순수 Kotlin paging-common 예외 허용 |
| `:core:data` | internal repository 구현 4개·PagingSource 4개·mapper·Firestore provider 및 접근·Jsoup |
| `:core:testing` | domain repository Fake 4개·공유 모델 fixture 8개·MainDispatcherRule·자체 테스트. production 의존 금지 |
| `:core:designsystem` | Material 3 theme·모델 독립 공통 component·ScreenScaffold·drawable·주/야간 colors |
| `:core:ui` | PagingRowList·OpenOtherApp·MVI 기반 타입 |
| `:core:navigation` | Navigation 3 stack·Navigator·결과 전달·5개 탭 정책과 테스트 |
| `:feature:youtube:api`, `:feature:youtube:impl` | YouTube 채널·playlist·videos·북마크 선택 다이얼로그 |
| `:feature:chzzk:api`, `:feature:chzzk:impl` | 치지직. Chzzk* 화면 이름과 domain의 TwitchRepository 계약 사용 |
| `:feature:webtoon:api`, `:feature:webtoon:impl` | 웹툰 |
| `:feature:cafe:api`, `:feature:cafe:impl` | 카페. Paging·collapsing toolbar 사용 |
| `:feature:store:api`, `:feature:store:impl` | 스토어 |
| `:feature:bookmark:api`, `:feature:bookmark:impl` | 북마크 추가·수정·삭제·결과 key. 영상 재생 위치 선택 UI는 youtube impl 소유 |

세부 책임과 공개 경계는 [문서 안내의 Core 목록](../README.md#core-문서), [feature 규칙](../modules/feature.md)을 따른다. DataStore는 사용하지 않으며 도입 대상이 아니다. 별도 Firebase 모듈은 만들지 않는다.

## 의존성 방향

- app → 조립에 필요한 feature API·impl과 core.
- feature API → core model·navigation(`api`).
- feature impl → 대응 API·이동 대상 API, convention의 common·domain·designsystem·ui, 테스트 전용 testing. youtube impl → bookmark api의 편집 이동·BookmarkResultKey를 사용하며 bookmark impl은 youtube에 의존하지 않는다.
- data → domain·model·common·network·database. domain → model. ui → model·designsystem.
- domain의 공개 모델·`Flow<PagingData<...>>`를 위해 model·coroutines-core·순수 Kotlin `paging-common`을 `api`로 제공한다. domain은 `Resource`·common·javax.inject에 의존하지 않으며 Paging runtime·Compose·Android UI 의존성도 추가하지 않는다. Paging 예외와 공개 메서드는 [domain 문서](../modules/core/domain.md)를 따른다.
- testing → domain(`api`), model은 domain의 `api`를 통해 제공한다. production에서는 testing에 의존하지 않는다.
- 나머지 core는 다른 프로젝트 모듈에 의존하지 않는다.

## 기술 선택

Kotlin·AGP·KSP, Compose, Hilt, Lifecycle·Coroutines, Retrofit·Gson·OkHttp, Room, Firestore, Paging, Jsoup를 유지한다. Moshi와 DataStore는 도입하지 않는다. Compose UI는 Material 3를 사용한다. Navigation 3·Kotlin Serialization을 사용한다. Navigation Compose와 app의 Gson 의존성은 제거했으며 network의 Gson은 유지한다.

버전·SDK·plugin alias의 단일 기준은 [버전 카탈로그](../../gradle/libs.versions.toml)다. 앱 설정과 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.

# 프로젝트 명세

## 목적과 현재 상태

ChimtubeWorld의 기존 기능과 배포 호환성을 유지하며 화면 기능을 `feature`, 공유 계약과 기반 기능을 `core`로 분리한다.

- 현재 등록된 모듈은 `:app`, `:core:common`, `:core:model`, `:core:database`, `:core:network`, `:core:domain`, `:core:data`, `:core:testing`, `:core:designsystem`, `:core:ui`, `:core:navigation`, `:feature:webtoon:api`, `:feature:webtoon:impl`, `:feature:store:api`, `:feature:store:impl`, `:feature:cafe:api`, `:feature:cafe:impl`, `:feature:chzzk:api`, `:feature:chzzk:impl`, `:feature:youtube:api`, `:feature:youtube:impl`, `:feature:bookmark:api`, `:feature:bookmark:impl`이다. Step 7에서 repository contract 4개를 domain에, repository 구현·PagingSource·mapper·Firestore·Jsoup을 data에 추출했다. 단순 위임·UI용 오류 래핑만 수행하던 usecase는 제거했으며 현재 usecase는 없다.
- Step 8에서 testing에 domain 계약 기반 Fake 4개·공유 모델 fixture 8개·MainDispatcherRule과 자체 테스트를 추가했다. data의 repository·mapper·PagingSource 단위 테스트도 추가했다. Jsoup 직접 크롤링·Firestore 채널 조회 등 제외 경계는 [테스트 가이드](../development/testing.md#현재-테스트-예제와-경계)에 기록한다. 테스트 실행·app 빌드 통과는 별도로 검증한다.
- Step 9에서 Material 2 theme·모델 독립 공통 component·공유 drawable·주/야간 colors를 designsystem으로, PagingRowList·미사용 MVI base 타입을 ui로 추출했다. 이동 당시 UI·문자열 route·상태 관리는 그대로 유지했다.
- Step 10에서 theme을 Material 3 ColorScheme·Typography·Shapes로 바꾸고 app·공통 component의 Material 2 사용을 Material 3로 전환했다. 색상 역할·타이포그래피 매핑과 외형 보존 기준은 [designsystem 문서](../modules/core/designsystem.md)를 따른다. 직접 Material 2·accompanist-insets-ui 의존성은 제거했으며 toolbar-compose의 런타임 전이 Material 2 의존성은 유지한다. 시스템 바 처리·인셋·문자열 route·상태 관리는 보존한다. 빌드·라이트/다크 실기기 비교는 로컬 검증이 필요하다.
- Step 11에서 단일 app 내 Navigation 3 전환과 `:core:navigation` 도입을 구현했다. 분리 전 NavKey·EntryProvider는 app/navigation에 두며, 인자 있는 ViewModel은 AssistedInject, 북마크 결과는 NavigationResultKey로 전달한다. 각 Screen이 기존 외형의 상단바를 소유하고 GlobalViewModel·문자열 route를 제거했다. 탭 재선택·root 뒤로가기는 [navigation 정책](../modules/core/navigation.md)을 적용한다. 빌드·테스트 및 기기 스모크 결과는 별도 검증이 필요하다.
- Step 12에서 webtoon·store를 api/impl로 분리했다. API는 인자 없는 NavKey와 Navigator 확장 함수, impl은 기존 Route·Screen·State·ViewModel·화면 전용 리소스와 EntryProvider를 소유한다. app이 두 entry를 등록하며 탭 순서·아이콘과 화면 동작은 유지한다. ScreenScaffold는 designsystem, OpenOtherApp은 ui로 이동했다. Step 13에서 두 feature의 MVI·문자열 리소스·Preview·ViewModel 및 Screen 테스트 작성을 완료했다. 빌드·실기기 확인은 별도 검증이 필요하다.
- Step 14에서 cafe·chzzk를 같은 api/impl 패턴으로 이동했다. API는 CafeKey·ChzzkKey와 Navigator 확장 함수를, impl은 기존 Route·Screen·State·ViewModel·화면 전용 composable과 EntryProvider를 소유한다. 두 화면은 공통 designsystem 리소스만 사용하므로 이동할 app 전용 리소스는 없다. Cafe의 Paging·collapsing toolbar, Chzzk의 Twitch* 이름·채널 선택 다이얼로그와 repository·Resource 처리를 유지했다. Step 15에서 cafe·chzzk의 MVI·문자열 리소스·Preview·ViewModel 및 Screen 테스트를 작성했고 Twitch* 화면 이름을 Chzzk*로 전환했다. domain의 TwitchRepository 계약은 유지한다. 빌드·테스트·기기 확인은 별도 검증이 필요하다.
- Step 16에서 youtube(playlist·videos·북마크 선택 다이얼로그 포함)·bookmark를 api/impl로 이동해 여섯 feature의 분리를 완료했다. feature 내부 사용 여부와 관계없이 모든 @Serializable NavKey·Navigator 이동 확장 함수·result key는 API에 공개 선언한다. youtube API는 YoutubeKey·PlaylistsKey·VideosKey와 이동 확장 함수를, bookmark API는 AddBookmarkKey·EditBookmarkKey와 이동 확장 함수·BookmarkResultKey를 소유한다. 각 feature impl은 화면과 모든 소유 key를 등록하는 entry 함수 하나를 공개한다. VideoSource는 youtube·bookmark에서 공유하므로 core:model의 순수 @Serializable enum으로 이동했다. youtube impl → bookmark api로 편집 이동·결과 소비를 연결하며 impl 간 의존은 없다. Step 16에서는 기존 상태 관리와 AssistedInject·creationCallback·UI·결과 전달 동작을 유지했다. Step 17에서 youtube·playlist·videos와 bookmark 추가·수정을 Contract·BaseViewModel·onEvent로 전환하고 문자열·Preview·ViewModel 및 Screen 테스트를 작성했다. 최초 조회는 assisted 생성자 인자로 init에서 시작한다. 영상 선택·삭제 확인은 UiState이며 navigation·외부 실행·Toast·클립보드 효과는 Route가 전달하고 EntryProvider가 처리한다. 마지막 Resource 사용처를 전환해 래퍼를 삭제했다. Step 17 Gradle 실행은 샌드박스 JDK의 Bad CPU type 오류로 시작하지 못했으며 로컬 빌드·테스트·기기 확인이 필요하다.
- 현재 의존성은 app → 여섯 feature API/impl 및 common·model·domain·data·designsystem·ui·navigation, ui → model·designsystem, data → domain·model·common·network·database, domain → model이다. testing → domain은 공개 테스트 API이며 data → testing은 `testImplementation`이다. production 소비자는 없다. common·model·database·network는 다른 프로젝트 모듈에 의존하지 않는다. `Bookmark`·`Video`·`VideoSource`는 순수 모델로 model에, `BookmarkEntity`·`ReadHistoryEntity`는 database에 둔다. 읽음 기록은 데이터 계층 내부에서만 사용하므로 별도 `ReadHistory` 모델은 없다.
- feature ViewModel은 domain repository contract를 직접 사용한다. data의 internal 구현체를 `DataModule`에서 Hilt binding하며, Firestore singleton provider는 data의 `FirestoreModule`에 둔다. Room provider는 database, OkHttp·API별 Retrofit 서비스와 RemoteDataSource binding은 network에 둔다. DTO·Entity → model 변환, 기존 Firestore 쿼리와 Jsoup 접근은 data 내부에 유지한다. app의 빈 `AppModule`·`AssistedInjectModule`은 제거했다. `OpenOtherApp`은 Context 기반 외부 앱·Custom Tabs 실행 helper로 ui에 두며 feature들이 공유한다.
- 앱 UI는 Material 3·Navigation 3를 사용한다. 여섯 feature 모두 Contract·BaseViewModel·onEvent 기반 MVI를 사용한다. app에는 화면·ViewModel 없이 앱 조립과 navigation chrome·공유 인텐트 처리만 남는다.
- **마이그레이션 진행 중**: core와 여섯 feature의 api/impl 모듈은 모두 생성했다. Step 17까지 아래 MVI 규칙을 적용했으며 릴리스 준비와 최종 검증은 후속 단계다. 데이터·식별자 보존 기준과 기존 상태는 [기준선](../migration/baseline.md), 동작 확인은 [스모크 체크리스트](../migration/smoke-checklist.md)를 따른다. database의 빌드·identity hash·덮어 설치 검증은 [database 문서](../modules/core/database.md#빌드-설정과-스키마-보존)에 따른다.
- 모듈 이동과 로직 재작성은 분리한다. core 추출과 Material 3·Navigation 3 전환 이후 feature 이동, MVI·테스트 적용은 각 후속 단계에서 수행한다.

## 아키텍처

- `app`은 composition root다. feature entry, Hilt 그래프, 앱 수준 navigation chrome을 조립한다.
- feature는 `api`와 `impl`로 분리한다. API는 navigation 계약을, impl은 화면과 화면 상태를 소유한다.
- core는 feature나 app에 의존하지 않는다.
- 비즈니스 데이터 흐름은 feature ViewModel → domain repository contract → data 구현이다. data 구현이 network·database 경계를 조합하며 Firestore도 data가 담당한다. usecase는 여러 repository를 조합하는 비즈니스 흐름이 필요할 때만 도입한다.
- repository의 suspend 조회·저장 예외는 호출자에게 전파한다. ViewModel은 suspendRunCatching으로 조회 실패를 isLoading·hasLoadError로 변환하고 Screen은 feature 문자열 리소스를 선택한다. 취소는 다시 던지고 기존 Exception 외 오류 전파와 출력 정책은 유지한다. Bookmark 저장·삭제 예외 전파와 Cafe 읽음 저장 실패 무시 정책도 유지한다. Resource 래퍼는 Step 17에서 사용처가 없어 삭제했다.
- 화면 상태는 `UiState`, `UiEvent`, `UiSideEffect`와 `BaseViewModel`을 사용하는 MVI다.

## 모듈 지도

| 모듈 | 책임 |
| --- | --- |
| `:app` | 앱·Hilt·feature entry 조립, top-level navigation·하단바, Activity·공유 인텐트 처리 |
| `:core:common` | Kotlin 유틸·상수·suspendRunCatching·dispatcher DI. Step 17에서 미사용 Resource 삭제 |
| `:core:model` | Bookmark·Video를 포함한 공유 순수 모델 8개와 VideoSource enum. Bookmark·Video·VideoSource에 Kotlin Serialization 적용 |
| `:core:network` | YouTube·Cafe·Chzzk 서비스·전체 DTO·RemoteDataSource·네트워크 DI 추출. Gson·기존 요청 설정 유지 |
| `:core:database` | Room Entity 2개·DAO·Database·Room Hilt 제공 모듈 추출. 기존 v2 스키마 유지 |
| `:core:domain` | repository contract 4개. 현재 usecase 없음. 순수 Kotlin paging-common 예외 허용 |
| `:core:data` | internal repository 구현 4개·PagingSource 4개·mapper·Firestore provider 및 접근·Jsoup 추출 |
| `:core:testing` | domain repository Fake 4개·공유 모델 fixture 8개·MainDispatcherRule·자체 테스트. production 의존 금지 |
| `:core:designsystem` | Material 3 theme·모델 독립 공통 component·ScreenScaffold·drawable·주/야간 colors |
| `:core:ui` | PagingRowList·OpenOtherApp·MVI 기반 타입. model 기반 View는 후속 추가 |
| `:core:navigation` | Navigation 3 stack·Navigator·결과 전달·5개 탭 정책 테스트 도입 |
| `:feature:youtube:api`, `:feature:youtube:impl` | YouTube 채널·playlist·videos·북마크 선택 다이얼로그. Step 16 이동, Step 17 MVI·문자열·Preview·테스트 작성 완료 |
| `:feature:chzzk:api`, `:feature:chzzk:impl` | 치지직. Step 14 api/impl 이동 완료. Step 15 MVI·문자열 리소스·Preview·테스트 작성 및 Chzzk* 화면 이름 전환 완료, domain TwitchRepository 유지 |
| `:feature:webtoon:api`, `:feature:webtoon:impl` | 웹툰. Step 12 api/impl 이동, Step 13 MVI·문자열 리소스·Preview·테스트 작성 완료 |
| `:feature:cafe:api`, `:feature:cafe:impl` | 카페. Step 14 api/impl 이동 완료, 기존 Paging·collapsing toolbar 유지, Step 15 MVI·문자열 리소스·Preview·테스트 작성 완료 |
| `:feature:store:api`, `:feature:store:impl` | 스토어. Step 12 api/impl 이동, Step 13 MVI·문자열 리소스·Preview·테스트 작성 완료 |
| `:feature:bookmark:api`, `:feature:bookmark:impl` | 북마크 추가·수정·삭제·결과 key. Step 16 이동, Step 17 MVI·문자열·Preview·테스트 작성 완료. 영상 재생 위치 선택 UI는 youtube impl 소유 |

세부 책임과 공개 경계는 [문서 안내의 Core 목록](../README.md#core-문서), [feature 규칙](../modules/feature.md)을 따른다. DataStore는 사용하지 않으며 도입 대상이 아니다. 별도 Firebase 모듈은 만들지 않는다.

## 목표 의존성 방향

- app → 조립에 필요한 feature API·impl과 core.
- feature API → core model·navigation(`api`).
- feature impl → 대응 API·이동 대상 API, convention의 common·domain·designsystem·ui, 테스트 전용 testing. youtube impl → bookmark api의 편집 이동·BookmarkResultKey를 사용하며 bookmark impl은 youtube에 의존하지 않는다.
- data → domain·model·common·network·database. domain → model. ui → model·designsystem.
- domain의 공개 모델·`Flow<PagingData<...>>`를 위해 model·coroutines-core·순수 Kotlin `paging-common`을 `api`로 제공한다. domain은 `Resource`·common·javax.inject에 의존하지 않으며 Paging runtime·Compose·Android UI 의존성도 추가하지 않는다. 이 이동 단계의 Paging 예외와 공개 메서드는 [domain 문서](../modules/core/domain.md)를 따른다.
- testing → domain(`api`), model은 domain의 `api`를 통해 제공한다. production에서는 testing에 의존하지 않는다.
- 나머지 core는 다른 프로젝트 모듈에 의존하지 않는다.

## 기술 선택

Kotlin·AGP·KSP, Compose, Hilt, Lifecycle·Coroutines, Retrofit·Gson·OkHttp, Room, Firestore, Paging, Jsoup를 유지한다. Moshi와 DataStore는 도입하지 않는다. Compose UI는 Material 3를 사용한다. Navigation 3·Kotlin Serialization은 Step 11에서 적용했다. Navigation Compose와 app의 Gson 의존성은 제거했으며 network의 Gson은 유지한다.

버전·SDK·plugin alias의 단일 기준은 [버전 카탈로그](../../gradle/libs.versions.toml)다. 앱 설정과 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.

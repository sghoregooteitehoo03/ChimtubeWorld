# 프로젝트 명세

## 목적과 현재 상태

ChimtubeWorld의 기존 기능과 배포 호환성을 유지하며 화면 기능을 `feature`, 공유 계약과 기반 기능을 `core`로 분리한다.

- 현재 등록된 모듈은 `:app`, `:core:common`, `:core:model`, `:core:database`, `:core:network`다. Step 4·5의 공통 코드·모델·Room 추출에 이어 Step 6에서 Retrofit 서비스·DTO·네트워크 DI와 RemoteDataSource 경계를 추출했다.
- 현재 의존성은 app → common·model·database·network이며 네 core 모듈은 다른 프로젝트 모듈에 의존하지 않는다. `Bookmark`·`Video`는 순수 모델로 model에, `BookmarkEntity`·`ReadHistoryEntity`는 database에 둔다. 읽음 기록은 데이터 계층 내부에서만 사용하므로 별도 `ReadHistory` 모델은 없다.
- repository와 `BookmarkEntity` ↔ `Bookmark` mapper는 Step 7까지 app의 data 패키지에 둔다. Firestore provider는 app의 `AppModule`에 유지한다. Room provider는 database로, OkHttp·API별 Retrofit 서비스와 RemoteDataSource binding은 network로 옮겼다. app의 repository·PagingSource는 network의 datasource를 사용하고 DTO → model 변환과 Jsoup은 Step 7까지 app에 유지한다. `OpenOtherApp`은 Step 9까지 app에 유지한다.
- 앱은 여전히 Material 2, Navigation Compose 문자열 route, 화면별 `*ScreenState`와 개별 함수 호출을 사용한다. Compose convention의 Material 3 추가는 UI 전환을 뜻하지 않는다.
- **마이그레이션 진행 중 — 대상 모듈 생성 후 적용**: common·model·database·network 외의 아래 core·feature 모듈 지도와 MVI 규칙은 목표 구조다. 의존성은 추출된 모듈부터 적용한다. 데이터·식별자 보존 기준과 기존 상태는 [기준선](../migration/baseline.md), 동작 확인은 [스모크 체크리스트](../migration/smoke-checklist.md)를 따른다. database의 빌드·identity hash·덮어 설치 검증은 [database 문서](../modules/core/database.md#빌드-설정과-스키마-보존)에 따른다.
- 모듈 이동과 로직 재작성은 분리한다. core 추출, Material 3 전환, Navigation 3 전환, feature 이동, MVI·테스트 적용은 각 후속 단계에서 수행한다.

## 아키텍처

- `app`은 composition root다. feature entry, Hilt 그래프, 앱 수준 navigation chrome을 조립한다.
- feature는 `api`와 `impl`로 분리한다. API는 navigation 계약을, impl은 화면과 화면 상태를 소유한다.
- core는 feature나 app에 의존하지 않는다.
- 비즈니스 데이터 흐름은 feature ViewModel → domain usecase 또는 repository contract로 향한다. data 구현이 network·database 경계를 조합하며 Firestore도 data가 담당한다.
- 화면 상태의 목표는 `UiState`, `UiEvent`, `UiSideEffect`와 `BaseViewModel`을 사용하는 MVI다.

## 모듈 지도

| 목표 모듈 | 책임 |
| --- | --- |
| `:app` | 앱 조립과 top-level navigation. 현재 common·model·database·network 추출분 외의 구현 포함 |
| `:core:common` | 공통 결과 래퍼·유틸·상수 추출 완료. dispatcher는 후속 도입 |
| `:core:model` | Bookmark·Video를 포함한 공유 순수 모델 8개 |
| `:core:network` | YouTube·Cafe·Chzzk 서비스·전체 DTO·RemoteDataSource·네트워크 DI 추출. Gson·기존 요청 설정 유지 |
| `:core:database` | Room Entity 2개·DAO·Database·Room Hilt 제공 모듈 추출. 기존 v2 스키마 유지 |
| `:core:domain` | repository contract·비즈니스 규칙 |
| `:core:data` | repository 구현·mapper·Firestore·PagingSource·Jsoup |
| `:core:testing` | 테스트 전용 Fake·fixture·Main dispatcher 도구 |
| `:core:designsystem` | theme·공통 component·drawable |
| `:core:ui` | 공통 model 기반 View·MVI 기반 타입 |
| `:core:navigation` | Navigation 3 stack·Navigator·결과 전달 |
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
- data → domain·model·network·database. domain → model. ui → model·designsystem.
- testing → domain(`api`), model은 domain의 `api`를 통해 제공한다. production에서는 testing에 의존하지 않는다.
- 나머지 core는 다른 프로젝트 모듈에 의존하지 않는다.

## 기술 선택

Kotlin·AGP·KSP, Compose, Hilt, Lifecycle·Coroutines, Retrofit·Gson·OkHttp, Room, Firestore, Paging, Jsoup를 유지한다. Moshi와 DataStore는 도입하지 않는다. Material 3·Navigation 3·Kotlin Serialization의 실제 사용은 후속 전환 단계다.

버전·SDK·plugin alias의 단일 기준은 [버전 카탈로그](../../gradle/libs.versions.toml)다. 앱 설정과 로컬 준비는 [프로젝트 시작 가이드](../development/project-setup.md)를 따른다.

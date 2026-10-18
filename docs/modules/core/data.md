# :core:data

Step 7에서 app의 repository 구현·PagingSource·mapper·Firestore provider를 추출했다. namespace와 package는 `com.sghore.chimtubeworld.core.data`다. 기존 동작 보존 기준은 [마이그레이션 기준선](../../migration/baseline.md)을 따른다.

## 역할

domain repository contract의 구현, remote/local 저장소 조합, data 타입과 domain model 사이 mapper를 담당한다.

## 포함 대상

- domain repository contract의 구현체.
- DTO와 Entity를 공유 순수 모델로 변환하는 `toDomain`, domain model을 Entity로 변환하는 mapper.
- remote datasource와 DAO를 조합하는 조회·저장·캐시 정책.
- 구현체를 domain contract에 연결하는 Hilt binding.
- Firestore SDK 접근과 Hilt 제공·binding은 **`:core:data` 안에 둔다**. 사용자 결정이며 별도 Firebase 모듈을 만들지 않는다. 기존 컬렉션·쿼리를 보존한다.
- PagingSource와 Jsoup 크롤링 구현도 data가 소유한다.

## 제외 대상

- Retrofit endpoint, DTO 정의, Room Entity·DAO·Database 생성. Firestore 인스턴스 제공은 이 모듈이 담당한다.
- feature UI state, Compose UI, navigation, domain repository interface.

## 공개 경계와 의존성

- repository 구현체와 mapper는 data 내부 구현으로 `internal`을 유지한다.
- 외부 계층에는 DTO와 Entity를 노출하지 않고 `:core:model` 타입으로 변환한다.
- data는 `:core:domain`, `:core:model`, `:core:common`, `:core:network`, `:core:database`에 `implementation`으로 의존한다. app과 feature에는 의존하지 않는다.
- 실제 저장소 조합과 fallback 정책은 repository에 둔다. 아직 정의되지 않은 fallback을 임의로 추가하지 않는다.
- suspend 조회·저장 함수는 예외를 호출자에게 전파하며 `Resource` 래핑이나 UI 오류 상태 변환을 하지 않는다. 해당 처리는 ViewModel이 담당한다. PagingSource의 기존 예외 → `LoadResult.Error` 변환은 유지한다.

## 현재 구성과 보존 항목

- `repository/`: internal `BookmarkRepositoryImpl`, `CafeRepositoryImpl`, `TwitchRepositoryImpl`, `YoutubeRepositoryImpl`. DAO와 network RemoteDataSource를 기존 방식으로 조합하며 DTO → model 변환도 기존 본문 그대로 유지한다.
- Twitch·YouTube의 공개 `getChannels()`는 기존 Firestore 링크 조회 → API 조회를 순서대로 실행한다. 기존 `getChannelLinkData()`와 채널 정보 변환 함수는 private helper로 유지하고 쿼리·필드·호출 인자·예외 전파를 바꾸지 않는다. 반환 타입은 Twitch가 `List<Channel>`, YouTube가 `List<Channel?>`다.
- `repository/dataSource/`: internal `CafePostPagingSource`, `MainPlaylistPagingSource`, `SubPlaylistPagingSource`, `YoutubePagingSource`. Paging key·refresh key·종료 조건·오류 처리를 유지한다. Pager의 page size는 Cafe·YouTube 영상·main playlist가 10, sub playlist가 5로 기존과 같다.
- `mapper/BookmarkMapper.kt`: internal `BookmarkEntity.toDomain()`·`Bookmark.toEntity()`. YouTube PagingSource의 북마크 변환과 repository의 저장 변환에서 사용한다. Room 스키마·DAO API는 변경하지 않는다.
- `di/DataModule.kt`: 네 repository 구현체를 각각 대응하는 domain interface에 `@Binds`로 연결한다. 기존 repository 생성자 주입과 동일하게 scope annotation을 추가하지 않는다.
- `di/FirestoreModule.kt`: `FirebaseFirestore.getInstance()`를 기존처럼 `@Singleton`으로 제공한다. Firestore 접근은 YouTube·Twitch repository 구현 내부에 둔다. `YoutubeLink`, `TwitchLink` 컬렉션과 필드·쿼리, Twitch의 `whereNotEqualTo("explain", "")`, 변환·await 동작을 유지한다. 공유 `goodsElement` 상수도 변경하지 않는다.
- Jsoup은 `CafeRepositoryImpl.getCafeInfo()`와 기존 `CafePostPagingSource.connectJsoup()`에 둔다. URL·selector·User-Agent·header와 기존 private helper의 사용 여부를 유지한다. `CafeRepositoryImpl`은 common의 `@IoDispatcher CoroutineDispatcher`를 주입받아 기존 `CoroutineScope(ioDispatcher).async { ... }.await()` 구조를 유지한다. production dispatcher는 기존과 같은 `Dispatchers.IO`다.

`sg.android.library`, `sg.android.hilt` convention을 적용한다. Paging 구현은 `paging-common`으로 충분하며 Android Paging runtime은 사용하지 않는다. coroutines-core·coroutines-play-services·Firestore·Jsoup은 구현 의존성이다. google-services plugin과 설정 파일은 app에 유지한다.

app ViewModel은 domain repository 계약을 직접 사용하고 data는 Hilt 그래프 조립을 위해 의존한다. app의 직접 database·network 의존성과 OkHttp·Retrofit·Gson converter·Firestore·Jsoup·coroutines-play-services·Paging runtime 의존성은 제거했다. Room 직접 의존성은 Step 5에서 이미 제거됐다. UI의 paging-compose와 navigation 인자 직렬화에 사용하는 Gson 직접 의존성은 유지한다. 기존 converter가 제공하던 Gson 2.13.1과 같은 버전을 사용한다.

## 구현과 검증 참고

- [현재 소스](../../../core/data/src/main/java/com/sghore/chimtubeworld/core/data)
- [domain 계약](domain.md)
- 참고 템플릿: `.ai-work/reference/AndroidComposeTemplate/core/data`의 repository 구현·mapper·DataModule·Gradle 설정.
- 컴파일·조립 검증: `./gradlew :core:domain:compileDebugKotlin :core:data:compileDebugKotlin :app:assembleDebug`.

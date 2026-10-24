# :core:domain

repository contract 4개를 제공하며 현재 usecase는 없다. namespace와 package는 `com.sghore.chimtubeworld.core.domain`이다. 호환성 기준은 [기준선](../../migration/baseline.md)을 따른다.

## 역할

domain이 필요로 하는 repository contract를 둔다. usecase는 여러 repository 조합이나 독립된 재사용 비즈니스 규칙이 필요할 때 추가한다.

## 포함 대상

- domain이 요구하는 repository contract.
- 여러 repository 조합, 재사용 비즈니스 흐름, validation·정렬·필터링·상태 계산을 수행하는 usecase(현재 없음).
- domain model을 입력과 반환으로 사용하는 비즈니스 규칙.

## 제외 대상

- repository 구현체, DTO, Entity, Retrofit, Room, DataStore, Hilt binding module.
- Android, Compose, Navigation 의존 코드와 화면 전용 UI state 가공.

## 공개 경계와 의존성

- repository contract는 data와 feature가 함께 사용하므로 공개한다.
- repository는 DTO·Entity·DAO·API를 노출하지 않고 `:core:model`의 순수 모델을 사용한다.
- 단순 repository 호출 후 반환하거나 UI용 오류 래핑만 수행하는 usecase는 만들지 않는다. 데이터 소스 조회·조합은 repository 구현이, 오류를 화면 상태로 변환하는 일은 ViewModel이 담당한다.
- repository contract는 순수 모델 또는 `Flow<PagingData<...>>`를 반환하고 `Resource`를 노출하지 않는다. suspend 조회·저장 중 발생한 예외는 호출자에게 전파한다. PagingSource의 기존 `LoadResult.Error` 처리는 유지한다.
- 프로젝트 모듈 의존성은 공개 모델을 제공하는 `:core:model`의 `api`뿐이며 common·data·network·database·feature·app에 의존하지 않는다.
- 기존 Paging 반환 계약 보존을 위한 예외로 **순수 Kotlin `androidx.paging:paging-common`**을 허용한다. `CafeRepository`와 `YoutubeRepository`는 `Flow<PagingData<Post|Video|Playlist>>`를 반환한다. `paging-common`과 `kotlinx-coroutines-core`는 공개 타입이므로 `api`다. Paging runtime·paging-compose·Android UI 의존성은 포함하지 않는다.
- `sg.android.library` convention을 적용하되 production 소스는 Android 타입을 사용하지 않는다. 생성자 주입 대상이 없어 `javax.inject` 의존성은 없으며 Hilt plugin·binding module도 추가하지 않는다.

## 현재 공개 계약

| repository | 메서드 |
| --- | --- |
| `BookmarkRepository` | `suspend addBookmark(Bookmark): Unit`, `suspend deleteBookmark(Bookmark): Unit`, `suspend getItemId(Bookmark): Int` |
| `CafeRepository` | `getCafePosts(categoryId: Int): Flow<PagingData<Post>>`, `suspend getCafeInfo(): Channel`, `suspend readPost(articleId: Int): Unit` |
| `TwitchRepository` | `suspend getChannels(): List<Channel>` |
| `YoutubeRepository` | `getVideos(channelId: String): Flow<PagingData<Video>>`, `getPagingMainPlaylist(channelId: String?, playlistId: List<String>): Flow<PagingData<Playlist>>`, `getPagingSubPlaylist(playlistId: List<String>): Flow<PagingData<Playlist>>`, `suspend getChannels(): List<Channel?>`, `suspend getVideo(url: String): Video` |

ViewModel이 사용하는 메서드만 계약으로 노출한다. 채널 조회의 Firestore 링크 조회 → API 호출은 data 구현의 private helper로 감춘다. DTO를 반환하는 `getPlaylist`도 data 구현의 private PagingSource callback으로 유지한다. YouTube 채널 목록의 기존 원소 nullable 계약을 유지한다.

ViewModel은 repository를 직접 주입받는다. Cafe·Chzzk·YouTube·Bookmark의 suspend 조회는 `suspendRunCatching`으로 호출하고 `isLoading`·`hasLoadError`로 표현한다. 화면은 feature 문자열 리소스를 선택한다. 카페 읽음 기록 저장 예외는 ViewModel에서 출력 후 삼키는 기존 정책을 유지한다.

## 구현과 검증 참고

- [repository contract](../../../core/domain/src/main/java/com/sghore/chimtubeworld/core/domain/repository)
- 빌드 설정: [build.gradle.kts](../../../core/domain/build.gradle.kts). 구현 예제는 위 현재 소스와 모듈 경계 규칙을 따른다.
- 컴파일·조립 검증: `./gradlew :core:domain:compileDebugKotlin :core:data:compileDebugKotlin :app:assembleDebug`.

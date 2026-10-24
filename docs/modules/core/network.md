# :core:network

## 역할과 현재 상태

Retrofit 서비스·DTO·OkHttp/Retrofit DI와 API별 RemoteDataSource를 소유한다. namespace와 패키지는 `com.sghore.chimtubeworld.core.network`다.

- `retrofit/`: 기존 `RetrofitService`(YouTube), `NaverRetrofitService`(Cafe·Chzzk)의 endpoint 정의.
- `dto/`: `youtubeAPI`, `naver_cafeAPI`, `naver_chzzkAPI`, `naver_storeAPI` 응답 DTO. 필드 이름·기본값·직렬화 매핑을 유지한다.
- `datasource/`: API별 공개 contract와 internal 구현체.
- `di/`: 네트워크 객체 제공, Naver 서비스 qualifier, datasource Hilt binding.

템플릿의 Moshi 대신 기존 **Gson과 `GsonConverterFactory.create()`**를 유지한다. Moshi codegen이나 새 JSON 변환 정책은 도입하지 않는다.

## API별 서비스와 datasource

| API | base URL | internal 서비스 | 공개 datasource와 메서드 |
| --- | --- | --- | --- |
| YouTube | `https://www.googleapis.com/youtube/v3/` | `RetrofitService` | `YoutubeRemoteDataSource`: `getYChannelInfo`, `getYPlaylistItems`, `getYVideos`, `getYPlaylists` |
| Naver Cafe | `https://apis.naver.com/` | `@NaverCafe NaverRetrofitService` | `CafeRemoteDataSource`: `getCafePosts` |
| Chzzk | `https://api.chzzk.naver.com/service/` | `@Chzzk NaverRetrofitService` | `ChzzkRemoteDataSource`: `getChannelInfo`, `getChannelMoreInfo` |

`NetworkModule`은 API별로 별도의 Retrofit을 생성해 준비된 singleton 서비스를 제공한다. 공유 mutable `Retrofit.Builder`는 노출하지 않는다. base URL은 다른 프로젝트 모듈에 의존하지 않도록 이 모듈 내부에서 관리하며, 기존 `Constants`의 URL과 동일하게 유지한다.

세 서비스는 하나의 singleton OkHttpClient를 공유한다. 기존과 동일하게 `readTimeout(1, TimeUnit.MINUTES)`와 요청의 `User-Agent`를 `Mozilla/5.0 (Windows NT 10.0; Win64; x64)`로 설정하는 interceptor를 사용하며, 그 외 timeout은 기본값을 유지한다. 로깅 interceptor는 추가하지 않는다.

`RemoteDataSourceModule`이 각 contract를 구현체에 Hilt binding한다. datasource는 요청 인자를 그대로 전달하고 DTO를 반환한다. 기존 `Call` 반환 메서드인 `getYPlaylistItems`·`getYVideos`의 `await()`만 구현체 내부로 이동했다. 예외를 잡거나 변환하지 않으며 기존 repository·PagingSource의 오류 처리와 paging 정책을 유지한다.

## API 키와 빌드 설정

- `sg.android.library`, `sg.android.hilt` convention을 적용한다.
- `buildFeatures.buildConfig = true`를 켜고 root `local.properties`의 `youtube_api_key`를 `Properties.load`·`buildConfigField("String", "YOUTUBE_API_KEY", ...)` 방식으로 읽는다. 속성 값은 큰따옴표를 포함한 Java 문자열 표현식이어야 한다.
- YouTube `@GET` 문자열은 network 자체의 `BuildConfig.YOUTUBE_API_KEY`를 참조한다. 생성된 Java 상수이므로 annotation의 컴파일 타임 상수 조건을 유지한다. 키를 출력하거나 로그에 남기지 않는다.
- API 키는 network에서만 생성·사용한다. app은 BuildConfig를 생성하지 않는다.

## 공개 경계와 의존성

- 공개 API는 RemoteDataSource contract와 DTO다. Retrofit API·datasource 구현체·DI 모듈·qualifier는 `internal`이다.
- network는 다른 프로젝트 모듈에 의존하지 않는다. Retrofit·OkHttp·Gson converter는 구현 의존성이다.
- `:core:data`의 repository·PagingSource가 datasource를 소비하고 DTO → model을 변환한다. domain과 feature에 DTO를 노출하지 않는다.
- domain model, mapper, repository 정책, Room, Firestore, Jsoup, UI state는 포함하지 않는다. Firestore provider와 Jsoup 접근은 `:core:data`에 둔다.

## 참고 구현과 검증

- [현재 구현](../../../core/network/src/main/java/com/sghore/chimtubeworld/core/network).
- 빌드 설정: [build.gradle.kts](../../../core/network/build.gradle.kts). 구현 예제는 위 현재 소스와 모듈 경계 규칙을 따른다.
- 컴파일·조립 검증: `./gradlew :core:network:compileDebugKotlin :app:assembleDebug`.
- 기존 동작 보존 기준은 [마이그레이션 기준선](../../migration/baseline.md)을 따른다.

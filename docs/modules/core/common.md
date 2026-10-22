# :core:common

## 역할

여러 계층에서 공유하는 Kotlin 공통 기능과 coroutine dispatcher DI를 제공한다. namespace와 package는 `com.sghore.chimtubeworld.core.common`이다. 유틸 함수는 Android 타입에 의존하지 않으며 dispatcher 제공은 Hilt module이 담당한다.

## 현재 포함된 구현

- `Resource.kt`: 성공·오류·로딩을 표현하는 공통 결과 래퍼. domain 데이터 모델이 아닌 계층 공통 타입으로 둔다.
- `Util.kt`: YouTube URL의 영상 ID 추출 함수 `getVideoId`. 파일 머리에 템플릿의 common 유틸 포함·제외 기준 주석을 둔다. 기존 함수 본문은 유지한다.
- `VideoParse.kt`: 팔로워·조회수·영상 길이·업로드 시점·북마크 위치 문자열 포맷 함수.
- `Constants.kt`: API URL, 외부 앱 package·scheme, Firestore 컬렉션 이름 등 기존 공유 상수.
- `ResultExt.kt`: `suspendRunCatching`은 suspend block의 결과를 `Result`로 반환한다. `CancellationException`은 즉시 다시 던지고 그 외 `Throwable`은 실패로 반환한다.
- `di/DispatcherModule.kt`: 공개 `DefaultDispatcher`, `IoDispatcher`, `MainDispatcher` qualifier와 Hilt provider. qualifier는 `DispatcherModule` 안에 선언하며 `BINARY` retention을 사용한다. `SingletonComponent`에서 각각 `Dispatchers.Default`, `Dispatchers.IO`, `Dispatchers.Main`을 제공한다.

Step 4에서는 package와 소비 코드의 import만 바꾸며 함수 동작과 상수 값을 유지한다.
Step 7에서 템플릿의 `ResultExt`, dispatcher DI와 Util 지침 주석을 추가했다. 기존 usecase에서 ViewModel로 옮긴 오류 래핑은 `suspendRunCatching`을 사용한다. 일반 예외의 성공·오류 방출과 출력은 유지하고 취소 예외는 오류로 변환하거나 삼키지 않는다. 기존 `catch (Exception)` 범위를 보존하기 위해 ViewModel의 `onFailure`에서는 `Exception` 외 오류를 다시 던진다.

## 포함·제외 기준

- 순수 Kotlin 문자열, 숫자, 날짜 포맷, 안전 변환, collection 확장처럼 Android에 묶이지 않는 가벼운 공통 기능을 포함한다.
- Android `Context` 의존 유틸, Compose/UI 유틸, Room·Retrofit 전용 유틸, feature 전용 로직은 제외한다.
- 화면 state, navigation 정책, data 변환 규칙은 제외한다.
- `OpenOtherApp`은 `Context`와 외부 앱 실행에 의존하므로 common에서 제외한다. Step 12에서 공유 Android UI helper로 `:core:ui`에 이동했다.

## 공개 경계와 의존성

- 공유 함수, `Resource`, `Constants`, dispatcher qualifier는 cross-module 사용을 위해 기본 공개 범위를 유지한다. provider는 템플릿과 같은 공개 `DispatcherModule`에 둔다.
- common은 다른 프로젝트 모듈에 의존하지 않는다.
- 템플릿과 같은 `sg.android.library`, `sg.android.hilt` convention과 `javax.inject`, `kotlinx-coroutines-android` 구현 의존성을 적용한다. Compose·Room·네트워크 의존성은 추가하지 않는다.
- dispatcher는 블로킹 작업을 main-safe하게 만드는 data 계층에서 주입받는다. 현재 Jsoup 블로킹 호출이 있는 `CafeRepositoryImpl`이 `@IoDispatcher CoroutineDispatcher`를 주입받으며, production 값은 `Dispatchers.IO`다. 테스트에서 직접 생성할 때는 같은 scheduler의 test dispatcher를 전달한다.
- ViewModel은 dispatcher를 주입받지 않고 `viewModelScope`의 기본 dispatcher를 사용한다. suspend DAO와 suspend repository 계약은 main-safe하므로 ViewModel에서 IO로 전환하지 않는다.

## 구현과 템플릿 참고

- [현재 소스](../../../core/common/src/main/java/com/sghore/chimtubeworld/core/common)
- 참고 템플릿: `core/common/build.gradle.kts`, `core/common/src/main/AndroidManifest.xml`, `core/common/src/main/java/com/sg/androidcomposetemplate/core/common`.

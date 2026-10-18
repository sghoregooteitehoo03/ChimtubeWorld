# :core:common

## 역할

여러 계층에서 공유하는 Android 비의존 Kotlin 공통 기능을 제공한다. namespace와 package는 `com.sghore.chimtubeworld.core.common`이다.

## 현재 포함된 구현

- `Resource.kt`: 성공·오류·로딩을 표현하는 공통 결과 래퍼. domain 데이터 모델이 아닌 계층 공통 타입으로 둔다.
- `Util.kt`: YouTube URL의 영상 ID 추출 함수 `getVideoId`.
- `VideoParse.kt`: 팔로워·조회수·영상 길이·업로드 시점·북마크 위치 문자열 포맷 함수.
- `Constants.kt`: API URL, 외부 앱 package·scheme, Firestore 컬렉션 이름 등 기존 공유 상수.

Step 4에서는 package와 소비 코드의 import만 바꾸며 함수 동작과 상수 값을 유지한다.

## 포함·제외 기준

- 순수 Kotlin 문자열, 숫자, 날짜 포맷, 안전 변환, collection 확장처럼 Android에 묶이지 않는 가벼운 공통 기능을 포함한다.
- Android `Context` 의존 유틸, Compose/UI 유틸, Room·Retrofit 전용 유틸, feature 전용 로직은 제외한다.
- 화면 state, navigation 정책, data 변환 규칙은 제외한다.
- `OpenOtherApp`은 `Context`와 외부 앱 실행에 의존하므로 현재 `:app`에 유지하고 Step 9에서 위치를 결정한다.

## 공개 경계와 의존성

- 공유 함수, `Resource`, `Constants`는 cross-module 사용을 위해 기본 공개 범위를 유지한다.
- common은 다른 프로젝트 모듈에 의존하지 않는다.
- `sg.android.library` convention만 적용한다. 현재 production 코드는 Kotlin/JDK만 사용하므로 Hilt·Compose·Room·네트워크 의존성을 추가하지 않는다.
- 템플릿의 `suspendRunCatching`과 dispatcher qualifier·Hilt binding은 아직 도입하지 않았다. 향후 도입 시 `suspendRunCatching`은 `CancellationException`을 다시 던지고, dispatcher qualifier는 공개하며 제공 구현은 `DispatcherModule`에 둔다.

## 구현과 템플릿 참고

- [현재 소스](../../../core/common/src/main/java/com/sghore/chimtubeworld/core/common)
- 참고 템플릿: `core/common/build.gradle.kts`, `core/common/src/main/AndroidManifest.xml`, `core/common/src/main/java/com/sg/androidcomposetemplate/core/common`.

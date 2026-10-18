# 멀티모듈 리팩터링 기준선

플레이스토어에 배포된 앱을 [AndroidComposeTemplate](https://github.com/sghoregooteitehoo03/AndroidComposeTemplate) 구조로 옮기기 전의 상태를 기록한다. 리팩터링 중에는 아래 "불변 항목"이 바뀌면 안 되고, 바뀌었다면 의도한 변경인지 확인한다.

## 브랜치와 태그

| 항목 | 값 |
| --- | --- |
| 기준 브랜치 | `release` |
| 작업 브랜치 | `refactor/multimodule` |
| 기준 태그 | `baseline/v1.4.5-vc10` (로컬 태그, 아직 push하지 않음) |
| 기준 커밋 | `0f43269` refactor: 라이브러리 및 gradle 버전 업그레이드 (2025-12-24) |

- 태그는 `versionName 1.4.5` / `versionCode 10`을 가진 마지막 커밋에 붙였다. 이 커밋이 스토어에 올라간 빌드와 같은지는 저장소만으로 확인할 수 없으므로 직접 확인한다.
- 기준 빌드가 필요하면 `git worktree add ../ChimtubeWorld-baseline baseline/v1.4.5-vc10`으로 별도 폴더에 꺼내 쓴다.

## 빌드 환경 (기준선)

| 항목 | 값 |
| --- | --- |
| Gradle / AGP | 8.13 / 8.13.2 |
| Kotlin / KSP | 2.0.21 / 2.0.21-1.0.27 |
| Hilt | 2.57.2 |
| Navigation | Navigation Compose 2.9.6 (문자열 route) + safe-args 플러그인 |
| Compose | `androidx.compose.material`(Material 2) 1.10.0, 컴파일러 확장 1.5.2 설정 잔존 |
| SDK | compileSdk 36 / targetSdk 36 / minSdk 24 |
| 빌드 스크립트 | Groovy (`build.gradle`), 버전 카탈로그 없음 |
| JSON | Gson (Retrofit converter) |

## 불변 항목 (바꾸면 업데이트·데이터에 영향)

| 항목 | 값 | 비고 |
| --- | --- | --- |
| `applicationId` | `com.sghore.chimtubeworld` | `google-services.json`의 `package_name`과 일치. Firebase project `chimtubeworld` |
| Room DB 파일 이름 | `ChimtubeWorld` | `AppModule`의 `Room.databaseBuilder` |
| Room 버전 / identityHash | `2` / `0ec1ca5bb6af49a984efc80f7c16b426` | [room-schema/AppDatabase-v2.json](room-schema/AppDatabase-v2.json) |
| Room 테이블 | `ReadHistory`, `Bookmark` | 컬럼은 스키마 JSON 참고 |
| Migration | 없음 (`addMigrations` 미사용) | 스키마가 달라지면 기존 사용자는 크래시. 아래 검증 절차 참고 |
| Firestore 컬렉션 | `YoutubeLink`, `TwitchLink`, `goodsElement` | `other/Constants.kt` |
| Manifest 컴포넌트 | `SplashActivity`(LAUNCHER), `MainActivity`(singleTask, `ACTION_SEND` `text/*`, portrait), `MainApplication` | 패키지 이동 시 `android:name` 갱신 필요 |
| Manifest `queries` | `com.nhn.android.webtoon` | 네이버 웹툰 앱 존재 확인용 |
| 외부 앱 연동 | YouTube 앱(`com.google.android.youtube`), Twitch(`twitch://`), 치지직(`navergameapp://chzzk/`), 네이버 웹툰(`webtoonkr://`) | `other/Constants.kt`, `other/OpenOtherApp.kt` |
| BuildConfig | `YOUTUBE_API_KEY` ← `local.properties`의 `youtube_api_key` | 키가 없으면 빌드 실패. 새 구조에서도 같은 방식 필요 |
| 릴리스 설정 | `minifyEnabled false`, `proguard-rules.pro`에 실제 규칙 없음, gradle에 `signingConfig` 없음 | 서명은 Play App Signing / 업로드 키로 별도 처리하는 것으로 보임 — 확인 필요 |
| 버전 | `versionCode 10` / `versionName "1.4.5"` | 리팩터링 릴리스는 `versionCode` 11 이상 |

## 구조상 알아둘 점

- 단일 모듈 `:app`. 테스트는 템플릿 기본 `ExampleUnitTest` / `ExampleInstrumentedTest`뿐이라 자동화된 안전망이 없다. 대신 [smoke-checklist.md](smoke-checklist.md)로 확인한다.
- `Bookmark`, `ReadHistory`는 Room `@Entity`이면서 화면에서도 그대로 쓰는 모델이다. 모듈 분리 시 database entity와 domain model 분리가 필요하다.
- `GlobalViewModel`이 Activity 범위에서 TopAppBar 액션 이벤트(도움말, URL 복사, 북마크 삭제)와 북마크 결과 전달을 맡는다. navigation 전환 시 대체 설계가 필요하다.
- Firestore는 `TwitchRepository`, `YoutubeRepository`에서 사용한다. 템플릿에는 대응하는 core 모듈이 없다.
- `app/libs/YouTubeAndroidPlayerApi.jar`는 `build.gradle`에서 참조하지 않는다(사용되지 않는 것으로 보임).
- `app/release/app-release.aab`가 git에 추적되고 있다. 리팩터링 커밋에 섞이지 않게 주의하고, 별도로 추적 해제할지 정한다.

## Room 스키마 검증 절차

기준 스키마는 `exportSchema`를 임시로 켜서 한 번 추출해 저장했다(원본 설정은 `exportSchema = false`). 리팩터링 후 DB 모듈을 옮길 때마다 아래를 확인한다.

1. `@Database`의 `entities`, `version = 2`, 테이블·컬럼 이름이 [AppDatabase-v2.json](room-schema/AppDatabase-v2.json)과 같다.
2. 임시로 `exportSchema = true` + `ksp { arg("room.schemaLocation", "<경로>") }`로 다시 추출해 `identityHash`가 `0ec1ca5bb6af49a984efc80f7c16b426`인지 비교한다.
3. 최종 확인은 기준 빌드에서 북마크·읽음 기록을 만든 뒤 리팩터링 빌드를 **덮어 설치**해 데이터가 유지되는지 본다.

## 기준선 빌드 확인

| 확인 | 결과 |
| --- | --- |
| `./gradlew :app:kspDebugKotlin` | 성공 (Room 스키마 추출 중 실행) |
| `./gradlew :app:assembleDebug` | 성공 (`app-debug.apk` 생성, 2026-10-18) |
| `./gradlew :app:bundleRelease` | 미수행 |
| 수동 스모크 테스트 | 미수행 — [smoke-checklist.md](smoke-checklist.md)의 "기준선" 열을 직접 채운다 |

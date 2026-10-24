# 멀티모듈 리팩터링 기준선

플레이스토어에 배포된 앱을 [AndroidComposeTemplate](https://github.com/sghoregooteitehoo03/AndroidComposeTemplate) 구조로 옮기기 전의 상태를 기록한다. 업데이트 호환성은 아래 불변 항목으로 확인한다. 기준선의 과거 구조와 완료된 구조는 구분하며 최종 구조는 [프로젝트 명세](../architecture/spec.md)를 따른다.

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
| Room DB 파일 이름 | `ChimtubeWorld` | 현재 `core:database`의 `DatabaseModule` |
| Room 버전 / identityHash | `2` / `0ec1ca5bb6af49a984efc80f7c16b426` | [room-schema/AppDatabase-v2.json](room-schema/AppDatabase-v2.json) |
| Room 테이블 | `ReadHistory`, `Bookmark` | 컬럼은 스키마 JSON 참고 |
| Migration | 없음 (`addMigrations` 미사용) | 스키마가 달라지면 기존 사용자는 크래시. 아래 검증 절차 참고 |
| Firestore 컬렉션 | `YoutubeLink`, `TwitchLink`, `goodsElement` | 현재 `core:common`의 `Constants.kt` |
| Manifest 컴포넌트 | `SplashActivity`(LAUNCHER), `MainActivity`(singleTask, `ACTION_SEND` `text/*`, portrait), `MainApplication` | 패키지 이동 시 `android:name` 갱신 필요 |
| Manifest `queries` | `com.nhn.android.webtoon` | 네이버 웹툰 앱 존재 확인용 |
| 외부 앱 연동 | YouTube 앱(`com.google.android.youtube`), Twitch(`twitch://`), 치지직(`navergameapp://chzzk/`), 네이버 웹툰(`webtoonkr://`) | 현재 `core:common/Constants.kt`, `core:ui/OpenOtherApp.kt` |
| BuildConfig | `YOUTUBE_API_KEY` ← `local.properties`의 `youtube_api_key` | 현재 core:network에서만 같은 방식으로 생성·사용. app 생성은 제거 |
| 릴리스 설정 | `minifyEnabled false`, `proguard-rules.pro`에 실제 규칙 없음, gradle에 `signingConfig` 없음 | 서명은 Play App Signing / 업로드 키로 별도 처리하는 것으로 보임 — 확인 필요 |
| 릴리스 버전 | `versionCode 11` / `versionName "1.5.0"` | 기준선은 10 / 1.4.5이며 릴리스는 버전을 올린다 |

## 기준선의 과거 구조

기준선은 단일 `:app`, 화면과 공유하는 Room Entity, Activity 범위 GlobalViewModel·문자열 route를 사용했다. 자동 테스트는 기본 예제뿐이었다. 이 과거 구조는 현재 구현 규칙이 아니며, 배포 비교는 [smoke-checklist.md](smoke-checklist.md)를 따른다.

## 마이그레이션 완료

- 최종 모듈은 `:app`, core 10개(common·model·database·network·domain·data·testing·designsystem·ui·navigation), feature 6개(youtube·chzzk·webtoon·cafe·store·bookmark)의 API·impl이다. 세부 경계는 [모듈 지도](../architecture/spec.md#모듈-지도)를 따른다. app은 조립을 담당하고 모든 feature는 Material 3·Navigation 3·MVI를 사용한다.
- API는 모든 NavKey·이동 함수·result key를 공개하고 각 impl은 단일 `<name>Entry`로 화면을 등록한다. 화면 상태는 feature가, navigation·외부 실행·Toast·클립보드 side effect는 EntryProvider가 처리한다.
- 의도된 탭 정책 변경: 현재 탭 재선택은 하위 stack을 비우고 root를 표시한다. 탭 root의 뒤로가기는 이전 방문 탭으로 이동하고 시작 탭(YouTube) root에서는 앱을 종료한다. 다른 탭의 하위 stack은 유지하며 시작 탭 선택 시 방문 이력만 초기화한다.
- 의도된 오류 표시 변경: cafe·chzzk·youtube·bookmark는 조회 실패 시 기존에 본문에서 보이지 않던 오류 문구를 표시한다. Bookmark 조회 실패 Toast는 유지한다. 이 변경과 탭 정책은 [스모크 체크리스트](smoke-checklist.md)로 확인한다.
- Twitch 북마크 URL 복사 시 잘못된 `$` 접두사를 제거한다.
- 모듈 전환 완료는 릴리스 검증 통과를 뜻하지 않는다. 로컬에서 `./gradlew clean test :app:assembleDebug :app:bundleRelease`, Room identityHash 비교·덮어 설치, 기기 스모크를 완료하고 결과를 기록한다. 릴리스 minify는 비활성화한다.

## Room 스키마 검증 절차

기준 스키마는 `exportSchema`를 임시로 켜서 한 번 추출해 저장했다(원본 설정은 `exportSchema = false`). DB 선언·빌드 설정을 변경하거나 릴리스할 때 아래를 확인한다.

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

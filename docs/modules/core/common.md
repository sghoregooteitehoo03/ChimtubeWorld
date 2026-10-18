# :core:common

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

여러 계층에서 공유하는 가벼운 Kotlin 공통 기능과 공통 coroutine dispatcher Hilt binding을 제공한다.

## 포함 대상

- `suspendRunCatching`처럼 suspend 블록의 예외 처리를 공통화하는 확장 함수.
- 순수 Kotlin 문자열, 숫자, 날짜 포맷, 안전 변환, collection 확장처럼 Android나 특정 계층에 묶이지 않는 유틸.
- `DefaultDispatcher`, `IoDispatcher`, `MainDispatcher` qualifier와 Dispatcher Hilt 제공 모듈.

## 제외 대상

- Android `Context` 의존 유틸, Compose/UI 유틸, Room·Retrofit 전용 유틸, feature 전용 로직.
- 화면 state, navigation 정책, data 변환 규칙.

## 공개 경계와 의존성

- `suspendRunCatching`은 `CancellationException`을 결과로 감싸지 않고 다시 던진다.
- dispatcher qualifier는 주입받는 모듈이 참조하므로 공개한다. 제공 구현은 `DispatcherModule` 안에 둔다.
- common은 feature나 app에 의존하지 않는다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/util). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

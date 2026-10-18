# :core:database

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

## 역할

Room의 Entity, DAO, AppDatabase와 Room 객체를 제공하는 Hilt module을 담당한다.

## 포함 대상

- 저장 전용 Entity와 table schema.
- Entity에 대한 SQL 실행을 담당하는 DAO.
- Entity와 DAO를 등록하는 Room Database.
- Database와 DAO를 singleton graph에 제공하는 Hilt module.

## 제외 대상

- DTO, Retrofit API, domain model, mapper, repository 구현, feature UI state.
- 비즈니스 규칙과 remote/local 선택 정책.

## 공개 경계와 의존성

- DAO는 data repository가 직접 주입받아 사용한다.
- Entity와 DAO는 저장 기술 경계의 타입이다. domain 또는 feature API로 노출하지 않는다.
- Database 생성과 Hilt 제공 module은 database 내부 세부사항으로 `internal`을 유지한다.
- database는 model, domain, data, feature, app에 의존하지 않는다.

## 마이그레이션 주의

현재 app의 Room/KSP 설정을 유지하며 `sg.android.room`은 적용하지 않는다. 이 convention은 `room.incremental`과 `room.schemaLocation`을 추가한다. schema export 설정은 이번 단계에서 바꾸지 않는다. DB 호환성 값과 스키마 재추출 절차는 [기준선](../../migration/baseline.md#room-스키마-검증-절차)을 따른다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/data/db). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

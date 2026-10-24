# :core:database

## 역할

Room의 Entity, DAO, AppDatabase와 Room 객체를 제공하는 Hilt module을 담당한다. namespace와 package는 `com.sghore.chimtubeworld.core.database`다.

## 현재 포함된 구현

- `entity/BookmarkEntity.kt`: 기존 `Bookmark` 테이블. 모델의 동등성 로직은 포함하지 않는다.
- `entity/ReadHistoryEntity.kt`: 기존 `ReadHistory` 테이블. 카페 데이터 계층에서 저장·존재 여부 확인에만 사용하므로 별도 순수 모델은 없다.
- `dao/Dao.kt`: 기존 조회 SQL, insert 충돌 정책, delete 동작을 유지한다.
- `AppDatabase.kt`: 위 두 Entity를 등록하는 version 2 Database.
- `di/DatabaseModule.kt`: 기존 이름 `ChimtubeWorld`로 Database를 생성하고 Database·DAO를 각각 `@Singleton`으로 제공한다. migration이나 destructive fallback은 추가하지 않는다.

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
- DAO 소비자는 `:core:data`의 repository와 PagingSource다. `BookmarkEntity` ↔ `:core:model`의 `Bookmark` 변환은 data의 `mapper/BookmarkMapper.kt`에 `internal`로 둔다.
- `ReadHistoryEntity`는 카페 repository·PagingSource 내부에만 머문다. 외부에는 기존처럼 `Post.isRead`와 읽음 처리 함수만 제공한다.

## 빌드 설정과 스키마 보존

- `sg.android.library`, `sg.android.hilt`, `sg.android.room`을 적용한다. Room convention이 runtime·KTX·compiler 의존성과 `room.incremental=true`, `room.schemaLocation=core/database/schemas`를 설정한다. app의 직접 Room 의존성과 Room compiler 설정은 제거했다.
- 기존 `exportSchema = false`를 유지한다. 따라서 일반 빌드에서 schema JSON은 생성하지 않는다.
- [기준 스키마](../../migration/room-schema/AppDatabase-v2.json)와의 스키마 동일성은 필수다. 테이블 이름을 `@Entity(tableName = "Bookmark")`·`@Entity(tableName = "ReadHistory")`로 고정하고 필드 선언 순서, column name, affinity, nullability, autoGenerate PK, SQL default 부재를 유지한다. Kotlin의 `id = null`은 SQL DEFAULT 선언이 아니다.
- DB 파일 이름은 `ChimtubeWorld`, version은 `2`, identityHash는 `0ec1ca5bb6af49a984efc80f7c16b426`이어야 한다. 클래스·패키지 이동을 이유로 이 값을 변경해서는 안 된다.

검증은 [기준선의 Room 검증 절차](../../migration/baseline.md#room-스키마-검증-절차)를 따른다.

1. Entity 선언을 기준 JSON의 `fields` 순서·속성·`primaryKey`·`createSql`과 대조한다. `@Database`의 Entity 순서도 `ReadHistoryEntity`, `BookmarkEntity`를 유지한다.
2. `./gradlew :core:database:compileDebugKotlin :app:assembleDebug`를 실행한다. 새로 생성된 `core/database/build/generated/ksp/debug` 아래 `AppDatabase_Impl`에서 identity hash가 위 값과 같은지 확인하고 `CREATE TABLE` SQL도 비교한다. 이전 app의 생성물을 검증 근거로 사용하지 않는다.
3. JSON 재추출이 필요하면 `exportSchema = true`로 임시 변경해 빌드한다. convention이 설정한 `core/database/schemas/com.sghore.chimtubeworld.core.database.AppDatabase/2.json` 전체 내용과 identityHash를 기준 JSON과 비교한 뒤 `exportSchema = false`로 복원한다. 기준 JSON 자체는 수정하지 않는다.
4. 기준 빌드에서 북마크·읽음 기록을 만든 뒤 새 빌드를 덮어 설치하여 데이터 유지와 조회·수정·삭제를 확인한다. 정적 대조만으로 빌드·덮어 설치 검증을 대체하지 않는다.

## 구현 참고

- [현재 소스](../../../core/database/src/main/java/com/sghore/chimtubeworld/core/database)
- [현재 mapper](../../../core/data/src/main/java/com/sghore/chimtubeworld/core/data/mapper/BookmarkMapper.kt)
- 빌드 설정: [build.gradle.kts](../../../core/database/build.gradle.kts). 구현 예제는 위 현재 소스와 모듈 경계 규칙을 따른다.

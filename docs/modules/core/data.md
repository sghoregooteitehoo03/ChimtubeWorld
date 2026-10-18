# :core:data

**마이그레이션 진행 중 — 대상 모듈 생성 후 적용**. 현재 구현은 `:app`에 있다. [마이그레이션 기준선](../../migration/baseline.md)을 확인한다.

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

- Retrofit endpoint, DTO 정의, Room Entity·DAO·Database 생성, 별도 저장소 key와 인스턴스 생성.
- feature UI state, Compose UI, navigation, domain repository interface.

## 공개 경계와 의존성

- repository 구현체와 mapper는 data 내부 구현으로 `internal`을 유지한다.
- 외부 계층에는 DTO와 Entity를 노출하지 않고 `:core:model` 타입으로 변환한다.
- data는 `:core:domain`, `:core:model`, `:core:network`, `:core:database`에 의존한다. app과 feature에는 의존하지 않는다.
- 실제 저장소 조합과 fallback 정책은 repository에 둔다. 아직 정의되지 않은 fallback을 임의로 추가하지 않는다.

## 현재 구현 참고

- [app의 기존 구현](../../../app/src/main/java/com/sghore/chimtubeworld/data/repository). 목표 모듈로 이동하기 전의 위치이며 새 모듈 규칙을 모두 만족한다는 뜻은 아니다.

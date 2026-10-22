# 작업 문서 안내

이 문서는 작업 종류에 따라 읽어야 할 규칙을 안내한다. 상세 규칙은 링크된 문서에만 두고, 같은 내용을 여러 문서에 중복하지 않는다.

현재는 `:app`과 추출된 common·model·database·network·domain·data·testing·designsystem·ui·navigation core 모듈로 구성된다. Material 3와 Navigation 3 전환까지 적용했으며 feature 분리와 MVI 전환은 후속 단계다. **마이그레이션 진행 중 — 대상 모듈 생성 후 적용** 표시는 후속 구조의 규칙을 뜻한다. 현재 구현·불변 항목은 [기준선](migration/baseline.md), 수동 검증은 [스모크 체크리스트](migration/smoke-checklist.md)를 확인한다.

## 문서 디렉터리

| 디렉터리 | 포함하는 문서 |
| --- | --- |
| `architecture/` | [프로젝트 명세](architecture/spec.md): 전체 구조, 기술 스택, 모듈 의존성 방향 |
| `modules/` | [app](modules/app.md), [feature](modules/feature.md), 아래 Core 문서: 모듈별 책임과 공개 경계 |
| `development/` | [프로젝트 시작](development/project-setup.md), [workflow](development/workflow.md), [build logic](development/build-logic.md), [테스트 작성](development/testing.md), [품질·검증](development/quality.md), [코드 리뷰](development/code-review.md), [커밋 규칙](development/commit.md), [PR 규칙](development/pull-request.md) |
| `migration/` | [기준선](migration/baseline.md)·[스모크 체크리스트](migration/smoke-checklist.md)·Room 기준 스키마 |
| `plans/` | [작업 계획 안내](plans/README.md). 향후 작업 계획·설계 검토 기록을 둘 공간 |

새 문서는 목적에 맞는 디렉터리에 추가하고 이 안내에서 연결한다. core 모듈 문서는 `modules/core/<module>.md`에 둔다. 공통 개발 규칙과 개별 작업 기록은 각 디렉터리에서 관리한다.

## 문서 읽기 순서

1. 아래 표에서 작업에 해당하는 문서를 선택한다. 여러 영역을 변경하면 해당 영역의 문서를 함께 확인한다.
2. 코드·테스트·빌드 설정을 변경할 때는 [quality.md](development/quality.md)를 공통으로 확인한다. 문서만 수정할 때는 변경한 규칙의 일관성과 링크·참고 경로를 확인한다.
3. 대상 문서와 관련 코드를 확인한 뒤, 변경 영향이나 판단에 필요한 내용에 따라 조건부 문서를 추가로 읽는다. 의존 모듈 문서의 확인 범위는 [quality.md의 구현 전 기준](development/quality.md#구현-전)을 따른다.

## 작업별 문서

| 작업 | 먼저 확인할 문서 |
| --- | --- |
| 프로젝트 구조, 기술 스택, 모듈 관계 파악 | [spec.md](architecture/spec.md) |
| 빌드 환경과 로컬 키·Firebase 설정 확인 | [project-setup.md](development/project-setup.md) |
| 여러 모듈에 걸친 기능 구현 | [workflow.md](development/workflow.md), 변경 대상 영역 문서 |
| feature API 또는 impl 추가·변경 | [feature.md](modules/feature.md). 새 feature는 [새 feature 추가 절차](modules/feature.md#새-feature-추가-절차)를 따른다. 모듈 추가 또는 Gradle·의존성·convention 변경 시 [build-logic.md](development/build-logic.md)를 추가로 확인 |
| app 조립, bottom navigation, `NavDisplay` 변경 | [app.md](modules/app.md), [core/navigation.md](modules/core/navigation.md) |
| repository 구현, mapper, remote/local 조합 | [core/data.md](modules/core/data.md), 변경 대상에 따라 [core/domain.md](modules/core/domain.md)·[core/network.md](modules/core/network.md)·[core/database.md](modules/core/database.md) |
| Retrofit API, DTO, RemoteDataSource | [core/network.md](modules/core/network.md), 필요 시 [core/data.md](modules/core/data.md) |
| Room Entity, DAO, Database | [core/database.md](modules/core/database.md), 필요 시 [core/data.md](modules/core/data.md) |
| domain model, repository contract, usecase | [core/model.md](modules/core/model.md), [core/domain.md](modules/core/domain.md) |
| 공통 Kotlin 기능, coroutine dispatcher | [core/common.md](modules/core/common.md) |
| theme, 공통 component, drawable | [core/designsystem.md](modules/core/designsystem.md) |
| 공통 Compose View, MVI base contract | [core/ui.md](modules/core/ui.md), 필요 시 [core/designsystem.md](modules/core/designsystem.md) |
| back stack, navigation result, 공통 Navigator | [core/navigation.md](modules/core/navigation.md), 필요 시 [feature.md](modules/feature.md)·[app.md](modules/app.md) |
| 모듈 추가, Gradle, convention plugin, 의존성 변경 | [build-logic.md](development/build-logic.md), [spec.md](architecture/spec.md) |
| 테스트 작성 | [testing.md](development/testing.md) |
| 코드 리뷰 | [code-review.md](development/code-review.md), 검토 대상 영역 문서 |
| 공통 Fake, fixture, Main dispatcher 테스트 도구 | [core/testing.md](modules/core/testing.md) |
| 커밋 메시지 작성, 변경 묶음 정리 | [commit.md](development/commit.md) |
| PR 제목·본문 작성, 제출 | [pull-request.md](development/pull-request.md), [PR 템플릿](../.github/pull_request_template.md) |

## Core 문서

- [common.md](modules/core/common.md)
- [data.md](modules/core/data.md)
- [database.md](modules/core/database.md)
- [designsystem.md](modules/core/designsystem.md)
- [domain.md](modules/core/domain.md)
- [model.md](modules/core/model.md)
- [navigation.md](modules/core/navigation.md)
- [network.md](modules/core/network.md)
- [testing.md](modules/core/testing.md)
- [ui.md](modules/core/ui.md)

# ChimtubeWorld 작업 지침

## 시작 지점

- 작업을 시작할 때 [docs/README.md](docs/README.md)의 읽기 순서와 작업별 안내에 따라 필요한 문서를 확인한다.
- 전체 구조나 기술 선택이 필요한 경우 [docs/architecture/spec.md](docs/architecture/spec.md)를 먼저 읽는다.
- 세부 모듈 규칙은 `docs/modules`의 app·feature 문서와 `docs/modules/core`의 해당 모듈 문서를 기준으로 판단한다.

## 현재 상태와 구현 규칙

- 프로젝트는 [프로젝트 명세](docs/architecture/spec.md)와 `docs/modules`의 모듈 규칙을 따른다. 멀티모듈 전환은 완료했으며 릴리스 호환성은 [기준선](docs/migration/baseline.md)을 따른다.

- 현재 프로젝트의 상태를 확인할 때는 Gradle 설정과 Kotlin 타입 선언, 실제 구현, 코드 주석, `docs` 순서로 확인한다.
- 문서와 구현이 다르면 현재 상태는 구현을 기준으로 판단하고, 작업 범위에 포함되면 문서도 실제 구조에 맞게 갱신한다.
- 새 코드를 작성하거나 기존 코드를 변경할 때는 `docs`에 정의된 architecture, 모듈 책임, 의존성 방향, 공개 API, navigation, 상태 관리, 품질 규칙을 따른다.
- 기존 코드가 문서 규칙을 위반하더라도 그 패턴을 새 코드에 반복하지 않는다.
- 하나의 기존 구현을 프로젝트 전체 규칙으로 일반화하지 않는다. 관련 문서와 반복되는 구현에서도 근거를 확인하고, 기준이 불명확하면 임의로 새 규칙을 만들지 말고 필요한 경우 사용자에게 확인한다.

## 문서 갱신

- 모듈의 책임, 의존성, 공개 API, navigation 정책, architecture·상태 관리·테스트 convention, convention plugin, 공통 workflow를 변경하면 관련 문서를 함께 갱신한다.
- 새 core 모듈을 추가하면 `docs/modules/core`에 역할, 포함·제외 대상, 공개 경계와 의존성, 참고 템플릿을 설명하는 문서를 추가한다.
- 새 feature를 추가하면 `docs/modules/feature.md`의 구조·규칙과 새 feature 추가 절차를 따르고, 기존 규칙을 바꾸는 경우에만 문서를 갱신한다.

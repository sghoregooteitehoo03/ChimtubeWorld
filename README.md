# ChimtubeWorld

YouTube, 치지직, 웹툰, 카페, 스토어 콘텐츠와 북마크를 제공하는 Android Compose 앱이다.

현재 단일 `:app`에서 멀티모듈 구조로 마이그레이션 중이다. build-logic과 문서를 도입했으며, core·feature 추출과 Material 3·Navigation 3·MVI 전환은 후속 단계다. 현재 상태와 목표 구조는 [프로젝트 명세](docs/architecture/spec.md)를 따른다.

## 빠른 시작

로컬 설정은 [프로젝트 시작 가이드](docs/development/project-setup.md)를 따른다.

```shell
./gradlew :app:assembleDebug
```

## 문서

- [작업 문서 안내](docs/README.md): 작업별 규칙과 읽기 순서.
- [마이그레이션 기준선](docs/migration/baseline.md): 배포 호환성과 데이터 보존 기준.
- [Feature 작업 규칙](docs/modules/feature.md): 향후 feature 구조와 추가 절차.
- [AGENTS.md](AGENTS.md): 에이전트 작업 지침. Claude Code는 `CLAUDE.md`에서 불러온다.

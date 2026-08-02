# SQL change files

DB 스키마 또는 데이터 변경 요청이 있을 때만 이 디렉터리에 검토용 SQL을 추가한다.

## Naming

```text
YYYYMMDD_short_description.sql
```

예시:

```text
20260728_add_project_status.sql
```

## Rules

- 실제 DB에는 연결하거나 SQL을 실행하지 않는다.
- 운영 데이터, 개인 포트폴리오 콘텐츠, 비밀번호와 DB dump를 포함하지 않는다.
- 가능한 경우 SQL을 멱등하게 작성하고, 파괴적인 변경에는 영향 범위와 복구 쿼리를 주석으로 남긴다.
- 실행 순서, 사전 조건과 예상 결과를 파일 상단 주석에 기록한다.
- 작성된 SQL의 실행은 사용자 또는 명시적으로 승인된 배포 시스템만 담당한다.

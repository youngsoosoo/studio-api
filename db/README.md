# Database changes

이 저장소는 실제 DB에 직접 연결하거나 쿼리를 실행하지 않는다.

스키마 또는 데이터 변경이 필요하면 실행 가능한 동작 대신 검토용 SQL을
`db/changes/YYYYMMDD_description.sql` 형식으로 작성한다. SQL 실행은 사용자 또는 별도로
승인된 배포 시스템이 담당한다.

운영 초기 데이터, 개인 포트폴리오 콘텐츠, DB dump와 비밀번호는 이 저장소에 보관하지 않는다.
애플리케이션 시작 시 데이터를 자동으로 입력하는 seeder도 사용하지 않는다.

세부 작성 규칙은 [`changes/README.md`](changes/README.md)를 따른다.

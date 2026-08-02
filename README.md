# studio-api

studio 포트폴리오 백엔드 API. **Spring Boot 3.5 + Java 17 + Gradle + JPA + PostgreSQL** 스택으로 구성되어 있다.
포트폴리오 콘텐츠는 PostgreSQL에 저장되며, 애플리케이션은 기존 스키마와 데이터를 읽기만 하고
시작 시 초기 데이터나 스키마를 자동 생성하지 않는다.

## 요구 사항

- Java 17 (Gradle 툴체인이 강제)
- PostgreSQL 14 이상 (`bootRun` 시에만 필요. 테스트는 H2 사용)
  - 로컬은 Docker로 띄우는 것을 권장한다 (아래 참고)

## 데이터베이스 (Docker Compose)

`docker-compose.yml` 이 앱이 기대하는 Postgres를 정의한다. 접속 정보는 `.env` 로 주입한다
(로컬·EC2 동일 파일 사용).

```bash
cp .env.example .env      # 값 채우기 (특히 DB_PASSWORD와 ADMIN_KEY)
docker compose up -d
docker compose ps         # healthy 확인
```

`.env` 변수:

| 변수          | 예제값                                           | 비고                                             |
|---------------|--------------------------------------------------|--------------------------------------------------|
| `DB_NAME`     | `portfolio`                                      | PostgreSQL 데이터베이스명                        |
| `DB_USER`     | `portfolio_user`                                 | PostgreSQL 사용자명                              |
| `DB_PASSWORD` | _(빈 값)_                                        | 필수. 강한 랜덤 값을 입력해야 Compose/앱 기동 가능 |
| `DB_PORT`     | `5432`                                           | 호스트 바인딩 포트. 충돌 시 `5433` 등으로 변경   |
| `DB_URL`      | `jdbc:postgresql://localhost:5432/portfolio`     | 애플리케이션 JDBC URL                            |

> 🔒 포트는 **`127.0.0.1` 에만 바인딩**된다. 외부에서 직접 붙지 못하며, 원격에서는 SSH 터널로
> 접근한다(아래 "EC2 배포" 참고). 보안 그룹/방화벽에서 5432 를 열지 말 것.

> ⚠️ 애플리케이션은 `ddl-auto: validate`로 기존 스키마를 검증할 뿐 생성하거나 수정하지 않는다.
> DB 변경이 필요하면 [`db/README.md`](db/README.md)의 규칙에 따라 검토용 SQL을 작성하고,
> 사용자 또는 별도로 승인된 배포 시스템이 실행한다. 이 저장소에는 운영 초기 데이터와 DB dump를
> 보관하지 않는다.

### Public GitHub 저장소의 시크릿 관리

- `.env.example`에는 변수명과 비밀이 아닌 예제값만 커밋한다.
- 실제 비밀번호가 들어 있는 `.env`, `.env.production` 등은 `.gitignore`로 커밋을 차단한다.
- 로컬에서는 `.env`를 프로젝트 루트에 두면 Docker Compose와 Spring Boot가 같은 파일을 읽는다.
- GitHub Actions에서는 저장소 또는 배포 Environment의 `Secrets and variables > Actions`에
  `DB_PASSWORD`, `ADMIN_KEY` 등을 등록하고 `${{ secrets.DB_PASSWORD }}` 형태로 주입한다.
- EC2에는 `.env`를 서버에서 직접 만들고 `chmod 600 .env`로 권한을 제한한다. 자동 배포 단계에서는
  장기적으로 AWS Secrets Manager 또는 SSM Parameter Store에서 런타임에 주입하는 방식을 권장한다.
- 시크릿이 한 번이라도 Git 이력이나 공개 화면에 노출됐다면 파일 삭제만으로 끝내지 말고 해당 값을
  즉시 폐기·재발급한다.

GitHub public 저장소는 secret scanning이 자동 적용되며, 사용자 push protection도 공개 저장소로
시크릿을 푸시하는 실수를 막아준다. 차단이 발생하면 우회하지 말고 값을 제거한 뒤 다시 커밋한다.

## EC2 배포 (1단계: DB만)

API는 아직 로컬에서 실행하고, **DB만 EC2로 옮기는** 단계다. EC2에는 소스코드가 필요 없고
`docker-compose.yml` 과 `.env` 두 파일만 있으면 된다.

### 1. 인스턴스 / 보안 그룹

| 항목      | 권장                                                          |
|-----------|---------------------------------------------------------------|
| 타입      | t3.small(2GB) 이상 — 2단계에서 JVM이 올라가므로               |
| OS        | Amazon Linux 2023 또는 Ubuntu 22.04/24.04                     |
| 스토리지  | 20GB                                                          |
| 네트워크  | 퍼블릭 서브넷 + Elastic IP(재시작해도 IP 유지)                |

보안 그룹 인바운드: **22(SSH)는 내 IP만**, **5432는 열지 않는다**(SSH 터널로 접근).

### 2. Docker 설치 (최초 1회)

**Amazon Linux 2023**
```bash
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable --now docker
sudo usermod -aG docker ec2-user
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
exit   # 재접속해야 docker 그룹 권한 적용
```

**Ubuntu** (compose plugin 포함)
```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu
exit   # 재접속
```

확인: `docker version && docker compose version`

### 3. 파일 전송 후 기동

```bash
# 로컬에서
ssh -i key.pem ec2-user@<EIP> "mkdir -p ~/studio"
scp -i key.pem docker-compose.yml ec2-user@<EIP>:~/studio/
scp -i key.pem .env               ec2-user@<EIP>:~/studio/   # 비밀번호 포함 — git에 올리지 않음

# EC2에서
ssh -i key.pem ec2-user@<EIP>
cd ~/studio
chmod 600 .env
docker compose up -d && docker compose ps
```

### 4. 로컬 앱 → EC2 DB 연결 (SSH 터널)

터널을 열어둔다(이 창은 유지):
```bash
ssh -i key.pem -N -L 5432:localhost:5432 ec2-user@<EIP>
```

DB 스키마가 승인된 절차로 준비된 뒤 다른 창에서 앱을 실행한다:
```powershell
$env:DB_URL      = "jdbc:postgresql://localhost:5432/portfolio"   # 터널 경유
$env:DB_USER     = "portfolio_user"
$env:DB_PASSWORD = "<.env 에 넣은 값>"
.\gradlew.bat bootRun
```

### 5. 확인

```bash
# 로컬: API 응답 확인
curl http://localhost:8080/api/portfolio
```

외부 노출 점검(**실패해야 정상**): 로컬에서 `Test-NetConnection <EIP> -Port 5432`

> 인스턴스를 **terminate** 하면 named volume(EBS)도 삭제된다. stop/start 는 안전하다.
> 운영 백업과 복구는 저장소의 애플리케이션 코드와 분리된 승인 절차로 관리한다.

## 환경 설정

런타임 설정은 환경 변수로 주입한다. 기본값은 `src/main/resources/application.yml` 에
정의되어 있고, 위 Docker 접속 정보와 일치한다.

| 변수                  | 기본값                                            | 비고                                          |
|-----------------------|---------------------------------------------------|-----------------------------------------------|
| `DB_URL`              | `jdbc:postgresql://localhost:5432/portfolio`      | JDBC URL                                      |
| `DB_USER`             | `portfolio_user`                                  |                                               |
| `DB_PASSWORD`         | **_(필수 · 기본값 없음)_**                        | 미설정 시 **기동 실패**. 설정 파일에 비밀번호를 두지 않는다 |
| `SERVER_PORT`         | `8080`                                            |                                               |
| `APP_PUBLIC_BASE_URL` | `http://localhost:8080`                           | 업로드 이미지 URL 프리픽스                    |
| `ADMIN_KEY`           | _(빈 값)_                                         | 비어 있으면 이미지 업로드 API 비활성(fail closed) |
| `UPLOAD_DIR`          | `./uploads`                                       | 업로드 파일 저장 경로. OneDrive 밖 권장       |

> 🔐 `DB_PASSWORD` 는 **의도적으로 기본값이 없다.** 비어 있으면
> `DataSourceSecretCheck`가 명확한 오류와 함께 기동을 중단한다.
> 커밋된 설정의 비밀번호로 애플리케이션이 조용히 동작하는 상황을 막기 위함이다.

로컬 설정은 프로젝트 루트의 `.env`에서 읽으며 운영체제 환경 변수가 `.env`보다 우선한다.
추가 오버라이드가 필요하면 `application.yml` 옆에 **커밋하지 않는**
`application-local.yml`을 만들고 `--spring.profiles.active=local`로 활성화한다.

## 실행

```bash
docker compose up -d                 # 로컬 Postgres 기동
./gradlew test                       # 테스트 프로파일(H2). DB_PASSWORD 불필요
./gradlew build                      # 테스트 포함 전체 빌드
```

`.env.example`을 복사해 실제 값을 채웠다면 별도의 PowerShell 환경 변수 설정 없이 기동할 수 있다:

```powershell
.\gradlew.bat bootRun                # 준비된 DB 스키마를 검증한 뒤 :8080 기동
```

셸 환경 변수로 직접 주입할 수도 있다:

```powershell
$env:DB_PASSWORD = "<비밀번호>"
.\gradlew.bat bootRun
```

> 완전히 새 DB에는 먼저 검토·승인된 SQL로 스키마를 준비해야 한다. 애플리케이션은 스키마나
> 초기 콘텐츠를 자동으로 생성하지 않는다.

## 엔드포인트

읽기 API는 모두 `{status, data, error}` 봉투로 감싼다.

| 메서드 | 경로                                | 설명                                        |
|--------|-------------------------------------|---------------------------------------------|
| GET    | `/api/health`                       | 라이브니스 응답, 인증 불필요                |
| GET    | `/api/portfolio`                    | 전체 포트폴리오 집계 (프론트가 사용)        |
| GET    | `/api/portfolio/{section}`          | 섹션별 조회 (profile, about, projects 등)   |
| GET    | `/api/portfolio/projects/{slug}`    | 프로젝트 상세(케이스 스터디). 없으면 404    |
| POST   | `/api/admin/images`                 | 이미지 업로드 (multipart, `X-Admin-Key` 필요) |

정적 파일: 업로드된 이미지는 `GET /files/{stored_name}` 로 서빙된다.

### 이미지 업로드

`X-Admin-Key` 헤더(= `ADMIN_KEY`)로 보호된다. `target` 으로 업로드와 동시에 연결한다.

```bash
# 프로필 사진
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@me.jpg" "http://localhost:8080/api/admin/images?target=avatar"

# 프로젝트 카드 썸네일
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@thumb.png" "http://localhost:8080/api/admin/images?target=thumbnail&project=sample-project"

# 프로젝트 상세 이미지(아키텍처/스크린샷)
curl -X POST -H "X-Admin-Key: $ADMIN_KEY" \
  -F "file=@arch.png" "http://localhost:8080/api/admin/images?target=project-image&project=sample-project&alt=구조도"
```

허용 형식: `jpg`, `jpeg`, `png`, `webp` (확장자 + Content-Type + 파일 시그니처 검사), 최대 5MB.
실행 가능한 콘텐츠를 포함할 수 있는 SVG 업로드는 허용하지 않는다.
업로드 응답의 `data.url` 은 절대 URL(`{APP_PUBLIC_BASE_URL}/files/...`)이다.

## 패키지 구조

```
com.studio.api
├── StudioApiApplication.java   Spring Boot 진입점
├── common/                     크로스컷팅 타입
│   ├── ApiResponse.java        제네릭 {status, data, error} 응답 봉투
│   ├── ErrorPayload.java
│   ├── GlobalExceptionHandler.java  예외 → 에러 봉투 (404/401/400/413/500)
│   ├── ImageUrlResolver.java   ImageEntity → 절대 /files/ URL
│   ├── NotFoundException.java  UnauthorizedException.java
├── config/
│   ├── WebCorsConfig.java      localhost:5173 CORS
│   ├── WebConfig.java          /files/** 정적 서빙 + admin 인터셉터 등록
│   └── AdminKeyInterceptor.java  /api/admin/** X-Admin-Key 검사
├── health/
│   └── HealthController.java   GET /api/health
├── image/                      이미지 업로드 모듈
│   ├── AdminImageController.java  POST /api/admin/images
│   ├── ImageService.java       저장 + target 연결 (트랜잭션)
│   └── FileStorageService.java 디스크 저장, UUID 파일명, allowlist
└── portfolio/
    ├── controller/PortfolioController.java
    ├── dto/                    응답 record (Profile, ProjectDetailResponse 등)
    ├── entity/                 JPA 엔티티 (*Entity)
    ├── repository/             Spring Data 리포지토리
    └── service/
        ├── PortfolioReader.java         읽기 인터페이스
        ├── DatabasePortfolioService.java  DB 조회
        └── PortfolioMapper.java         엔티티 → DTO
```

## 데이터베이스 스키마

- 슬러그(`focus`, `t-2024-join` 등)를 공개 `id` 로 유지하되 내부 PK는 대리키(BIGINT IDENTITY).
- `List<String>` 필드는 자식 테이블 + `sort_order`(0부터 연속, `@OrderColumn`)로 정규화.
- 이미지는 소유측 FK(`profile.avatar_image_id`, `project.thumbnail_image_id`,
  `project_image.image_id`)로 `image` 테이블을 참조.
- JPA 엔티티(`portfolio/entity/`)는 기존 DB 스키마와의 매핑을 정의하며 기동 시 매핑을 검증한다.

## 테스트 전략

- `test` 프로파일은 외부 DB와 연결하지 않고 H2(`MODE=PostgreSQL`)에 격리된 테스트 스키마를 만든다.
- `PortfolioControllerTest`는 개인 정보가 없는 mock 응답으로 집계·프로젝트 상세(200/404)를 검증한다.
- `AdminImageControllerTest`는 합성 테스트 데이터로 인증(401)·업로드(201)·타입 검증(400)·연결을 검증한다.

## 컨벤션

- 브랜치: `feature/{TICKET_ID}-{short-description}` 형식으로 `dev` 에서 분기
- 커밋: Conventional Commits (`chore:`, `feat:`, `fix:`, `docs:`, `test:`)
- 협업 워크플로는 [studio-docs](https://github.com/youngsoosoo/studio-docs)
  의 문서를 새 티켓 시작 전에 먼저 확인할 것

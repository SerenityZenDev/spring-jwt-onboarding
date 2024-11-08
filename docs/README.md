## Secure Backend Project - JWT Authentication

---

### Overview

본 프로젝트는 Spring Boot와 JWT를 이용한 인증 시스템 구축 과제입니다. 회원가입, 로그인, 토큰 발급 및 검증, 권한 관리 등의 기능을 구현하였으며, AWS EC2에 배포하여 API 접근과 인증 과정을 검증할 수 있습니다.

---

### Features

- JWT 기반 인증/인가: Access/Refresh Token을 이용한 사용자 인증 및 권한 관리
- Spring Security: 필터를 통한 접근 제어와 세분화된 권한 부여
- 테스트 코드 작성: JUnit을 사용한 유닛 테스트 및 통합 테스트 적용
- AWS EC2 배포: EC2 환경에서 서버를 배포하여 외부 접근 및 테스트 가능
- Swagger UI: API 문서 자동화 및 UI를 통한 테스트 제공

---

### Tech Stack

- Backend: Java, Spring Boot, Spring Security, JWT
- Testing: JUnit, MockMvc
- Deployment: AWS EC2
- Documentation: Swagger

---

### Getting Started

#### Prerequisites
- Java 17
- Gradle
- AWS EC2 인스턴스 (배포용)

#### Installation
Repository 클론:

```bash
git clone https://github.com/SerenityZenDev/spring-jwt-onboarding.git
```

[//]: # (환경 설정:)

[//]: # ()
[//]: # (application.yml 파일을 생성하여 JWT 비밀 키, 데이터베이스 설정 등을 포함합니다.)

[//]: # (src/main/resources 경로에 위치한 application.yml 파일 예시는 다음과 같습니다:)

[//]: # (yaml)

[//]: # (코드 복사)

[//]: # (spring:)

[//]: # (security:)

[//]: # (jwt:)

[//]: # (secret: "your-secret-key")

[//]: # (의존성 설치 및 빌드:)

[//]: # ()
[//]: # (bash)

[//]: # (코드 복사)

[//]: # (./gradlew build)

[//]: # (로컬 실행:)

[//]: # ()
[//]: # (bash)

[//]: # (코드 복사)

[//]: # (./gradlew bootRun)

---

### Endpoints

---

### Testing

---

### Deployment

---

### Commit Convention

---

### Additional Notes

---
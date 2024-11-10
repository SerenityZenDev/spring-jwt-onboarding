# Jwt Onboading

## Overview

해당 프로젝트는 Spring Security를 활용하여 JWT 인증을 구현하고, AWS EC2에 배포하여 API를 통해 회원가입, 로그인, 토큰 발행 및 유효성 확인 기능을 제공합니다. 과제 요구사항에 따라 작성한 코드로 JUnit을 이용한 테스트와 PR 개선 과정을 포함하고 있습니다.

## 프로젝트 기능 요구사항

1. **Spring Security와 JWT 이해 및 구현**
    - Filter 및 Interceptor 개념 이해
    - Access / Refresh Token 발행 및 검증
    - Spring Security의 역할 및 필터 체인 설정
2. **테스트 코드 작성**
    - JUnit을 이용한 JWT 관련 유닛 테스트
    - Spring Security Controller 유닛 테스트
3. **배포**
    - AWS EC2에 배포하여 API 접근이 가능하도록 설정
    - Swagger UI로 API 접근 및 확인 가능하게 구성
4. **PR 및 코드 리뷰**
    - PR 작성 및 리뷰 과정을 거쳐 코드 개선

---

## 시작하기 (Getting Started)

### Prerequisites

- Java 17+
- Spring Boot
- AWS 계정 (EC2 인스턴스 생성 및 관리)
- Git

### 설치 및 실행

1. **Clone the Repository**

   ```bash
   git clone https://github.com/SerenityZenDev/spring-jwt-onboarding.git
   cd spring-jwt-onboading
   ```

2. **프로젝트 실행**

   ```bash
   ./mvnw spring-boot:run
   ```

3. **Swagger UI 접근**
    - 로컬 환경에서 http://localhost:8080/swagger-ui.html 로 접속하여 API 문서를 확인할 수 있습니다.

---

## API 문서 (API Documentation)

### 회원가입 - `/signup`

- **Request**

  ```json
  {
    "username": "JIN HO",
    "password": "12341234",
    "nickname": "Mentos"
  }
  ```

- **Response**

  ```json
  {
    "username": "JIN HO",
    "nickname": "Mentos",
    "authorities": [
      {
        "authorityName": "ROLE_USER"
      }
    ]
  }
  ```

### 로그인 - `/sign`

- **Request**

  ```json
  {
    "username": "JIN HO",
    "password": "12341234"
  }
  ```

- **Response**

  ```json
  {
    "token": "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL"
  }
  ```

### 유저정보확인 - `/profile`

- **Request**

  ```json
  Authorization : {로그인에서 반환된 토큰 값}
  No Request
  ```

- **Response**

  ```json
  User profile for username: aaa (user ID:  1)
  ```
---

## 테스트

- **JWT 유닛 테스트**
    - JUnit을 이용하여 JWT 발행 및 검증 기능을 테스트합니다.
- **Filter 테스트**
    - Spring Security의 Filter 레벨에서 인증과 권한 테스트를 수행합니다.

## 배포 (Deployment)

1. **AWS EC2 설정**
    - EC2 인스턴스를 생성하고 필요한 포트 및 보안 그룹을 설정합니다.

2. **CI/CD**
    - PR을 통해 코드 리뷰 및 피드백을 반영하여 지속적인 코드 개선을 진행합니다.

3. **AWS EC2에 재배포**
    - 최종 버전을 EC2에 배포하여 API를 검증합니다.
4. **배포 링크**
    -  http://ec2-15-164-235-60.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html

---

## 기술 스택

- **Backend:** Java, Spring Boot, Spring Security, JWT, Caffeine Cache
- **Testing:** JUnit
- **Deployment:** AWS EC2, Swagger UI
- **Database:** H2 DB

---

## 코드 개선 및 피드백 반영

- PR과 코드 리뷰를 통해 지속적으로 개선된 코드를 배포합니다.
- Git 커밋 메시지 작성법을 참고하여 명확하게 기록하였습니다.

---


## 기여 (Contribution)

- AI 코드를 통해 코드 리뷰를 진행하고 피드백을 반영하여 코드의 품질을 개선했습니다.
  - llamapreview[bot]을 이용한 AI PR 리뷰를 적용


# 1. 베이스 이미지 선택
FROM openjdk:17-jdk-slim AS build

# 2. 작업 디렉터리 설정
WORKDIR /app

# 3. Gradle 빌드 파일 및 소스 코드 복사
# gradlew 및 gradle 디렉터리 복사
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/
COPY src /app/src

# 4. Gradle을 사용해 애플리케이션 빌드
# 종속성 다운로드 및 빌드 작업을 수행하여 JAR 파일을 생성합니다.
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 5. 런타임 이미지 생성
FROM openjdk:17-jdk-slim

# 6. 작업 디렉터리 설정
WORKDIR /app

# 7. 빌드 단계에서 생성된 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

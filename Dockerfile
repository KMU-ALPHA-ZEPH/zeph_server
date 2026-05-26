# ---- build stage: gradlew로 부트 jar 생성 ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 의존성 캐시 레이어 (소스 바뀌어도 의존성은 재다운로드 안 하도록 분리)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드 (테스트는 빌드 시 제외)
COPY . .
RUN ./gradlew clean bootJar --no-daemon -x test

# ---- run stage: JRE만 담은 가벼운 실행 이미지 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# -plain.jar 말고 실행 가능한 부트 jar만 복사
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

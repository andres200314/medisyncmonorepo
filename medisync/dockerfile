# ===== BUILD =====
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

ENV GRADLE_USER_HOME=/tmp/gradle

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon --stacktrace

# ===== RUN =====
FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]  
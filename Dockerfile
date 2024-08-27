FROM openjdk:17-alpine AS builder
# or
# FROM openjdk:8-jdk-alpine
# FROM openjdk:11-jdk-alpine

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true
# or Maven
# CMD ["./mvnw", "clean", "package"]



FROM openjdk:17-alpine

COPY --from=builder build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
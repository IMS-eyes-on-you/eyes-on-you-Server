FROM openjdk:17-alpine AS builder

COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true
# or Maven
# CMD ["./mvnw", "clean", "package"]
COPY . .
RUN gradle clean bootJar

FROM openjdk:17-alpine
COPY --from=builder build/libs/*.jar app.jar

EXPOSE 8080
USER nobody

ENTRYPOINT ["java","-jar","/app.jar"]
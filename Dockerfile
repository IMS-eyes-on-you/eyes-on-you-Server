FROM jdk:17-alpine AS builder

WORKDIR /home/gradle/src/

COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src

COPY . .
RUN gradle clean bootJar

FROM openjdk:17-alpine
COPY --from=builder home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
USER nobody

ENTRYPOINT ["java","-jar","/app.jar"]
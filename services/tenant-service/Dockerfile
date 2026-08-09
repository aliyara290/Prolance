FROM maven:3.9.13-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY infrastructure/config-server/pom.xml infrastructure/config-server/
COPY infrastructure/discovery-service/pom.xml infrastructure/discovery-service/
COPY services/tenant-service/pom.xml services/tenant-service/
COPY services/crm-service/pom.xml services/crm-service/
COPY services/project-service/pom.xml services/project-service/
COPY services/task-service/pom.xml services/task-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services/attachment-service/pom.xml services/attachment-service/

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -pl services/tenant-service -am -B

COPY services/tenant-service/src ./services/tenant-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn package -pl services/tenant-service -am -Dmaven.test.skip=true -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /build/services/tenant-service/target/*.jar app.jar
USER spring
ENTRYPOINT ["java","-jar","/app/app.jar"]

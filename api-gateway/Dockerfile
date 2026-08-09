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

RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl api-gateway -am -B -q

COPY api-gateway/src ./api-gateway/src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl api-gateway -am -Dmaven.test.skip=true -B -q

FROM  eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

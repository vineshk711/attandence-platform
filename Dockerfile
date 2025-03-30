FROM eclipse-temurin:17 AS build
WORKDIR /workspace/app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
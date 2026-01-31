# build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

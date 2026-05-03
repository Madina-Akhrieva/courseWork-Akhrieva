FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package dependency:copy-dependencies -DskipTests -DincludeScope=runtime

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/courseWork-2026-Marina-*.jar app.jar
COPY --from=builder /app/target/dependency ./lib
EXPOSE 5050
ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "org.example.coursework2026marina.server.ServerLauncher"]

# Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/Multi-Vendor-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=5454
EXPOSE 5454
ENTRYPOINT ["java", "-Xmx400m", "-Xms256m", "-jar", "app.jar"]

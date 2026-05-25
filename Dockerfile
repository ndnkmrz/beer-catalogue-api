FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Run as a non-root user (defense in depth; matches runAsNonRoot in the Helm chart).
# UID 1000 aligns with podSecurityContext.runAsUser in values.yaml.
RUN addgroup -S -g 1000 spring && adduser -S -u 1000 -G spring spring
USER 1000

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
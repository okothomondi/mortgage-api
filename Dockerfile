FROM ubuntu:latest
LABEL authors="platform-engineer"

ENTRYPOINT ["top", "-b"]

# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

# Copy build files
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY src ./src

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Create lightweight runtime image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built application from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create a non-root user and switch to it
RUN useradd -m appuser && chown -R appuser /app
USER appuser

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
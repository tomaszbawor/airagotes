# ---- Stage 1: Build the application ----
FROM gradle:8.13-jdk21 AS builder

# Copy everything and build
WORKDIR /app
COPY . .

# Build the app using Gradle wrapper
RUN ./gradlew clean bootJar --no-daemon

# ---- Stage 2: Create minimal runtime image ----
FROM eclipse-temurin:21-jre-alpine

# Set environment variables
ENV JAVA_OPTS=""
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

# Set working directory
WORKDIR /app

# Copy the built jar from previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

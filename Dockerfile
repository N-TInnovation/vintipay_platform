# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Install dependencies required for build
RUN apk add --no-cache maven

# Copy maven configuration files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application and skip tests
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add wait-for-it script to handle database dependencies
RUN apk add --no-cache bash
COPY --from=build /app/target/*.jar app.jar

# Expose port (match this with SERVER_PORT in environment)
EXPOSE 8080

# Set entrypoint with appropriate memory settings and connection retry parameters
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
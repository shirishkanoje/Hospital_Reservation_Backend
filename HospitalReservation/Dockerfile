# Use official OpenJDK image as base
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to cache dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this step gets cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the project source
COPY src src

# Package the Spring Boot app (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the built JAR file
ENTRYPOINT ["java","-jar","target/HospitalReservation-0.0.1-SNAPSHOT.jar"]

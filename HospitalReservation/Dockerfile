FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline -B

# Copy source
COPY src ./src

# Package the app
RUN mvn clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/HospitalReservation-0.0.1-SNAPSHOT.jar"]

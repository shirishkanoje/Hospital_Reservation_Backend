FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR from target to /app inside the container
COPY target/HospitalReservation-0.0.1-SNAPSHOT.jar /app/HospitalReservation-0.0.1-SNAPSHOT.jar

EXPOSE 8081

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/HospitalReservation-0.0.1-SNAPSHOT.jar"]

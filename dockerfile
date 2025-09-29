# Use Eclipse Temurin JRE 17 as base image
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory
COPY target/student-management-*.jar app.jar

# Expose the application port
EXPOSE 8089

# Set the Spring profile to production
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

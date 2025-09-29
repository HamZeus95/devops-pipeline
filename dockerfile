# Dockerfile
FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar

# Copy jar produced by your build step
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

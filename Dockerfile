# 1. Start with a lightweight, secure Java 17 Runtime Environment
FROM eclipse-temurin:17-jre

# 2. Create a clean working directory inside the container
WORKDIR /app

# 3. Copy your compiled Spring Boot application JAR file into the container folder
COPY target/*.jar app.jar

# 4. Tell the container to execute your Java app when it boots up
ENTRYPOINT ["java", "-jar", "app.jar"]
# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Fix Maven configuration path
ENV MAVEN_CONFIG=/root/.m2

# Make the mvnw script executable
RUN chmod +x ./mvnw

# Build the application without running tests
RUN mvnw clean package -DskipTests

# Use a minimal Java runtime image to run the app
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

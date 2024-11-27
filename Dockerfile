# Stage 1: Build the application
FROM maven:latest AS build
WORKDIR /app
ARG CONTAINER_PORT
ENV MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-jdk
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${CONTAINER_PORT:-8080}
ENTRYPOINT ["java","-jar","app.jar"]

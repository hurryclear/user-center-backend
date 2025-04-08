FROM maven:3.5-jdk-8-alpine as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact
RUN mvn package -DskipTests # or COPY target/user-center-backend-0.0.1-SNAPSHOT.jar ./target

CMD ["java", "-jar", "/app/target/user-center-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]

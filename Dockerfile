# Etapa 1: compilar el jar con Maven y JDK 17.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
# Los tests se corren aparte (.\mvnw.cmd clean verify); aqui solo se empaqueta.
RUN mvn -B -q clean package -DskipTests

# Etapa 2: imagen final, solo con el JRE 17 y el jar.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
RUN groupadd --system app && useradd --system --gid app app
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

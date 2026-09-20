FROM openjdk:17
COPY "./target/prueba-1.jar" "app.jar"
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

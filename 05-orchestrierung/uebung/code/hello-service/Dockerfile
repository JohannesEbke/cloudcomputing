FROM azul/zulu-openjdk:17-latest

COPY target/hello-service-*.jar /app.jar
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]

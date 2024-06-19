FROM azul/zulu-openjdk-alpine:17-latest
COPY target/bookstore-*.jar bookstore.jar
ENTRYPOINT [ "java", "-jar", "/bookstore.jar" ]

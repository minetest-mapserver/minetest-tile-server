FROM openjdk:10

COPY target/docker/tileserver.jar /
EXPOSE 8080

CMD ["java", "-jar", "/tileserver.jar"]


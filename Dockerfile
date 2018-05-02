FROM openjdk:10

COPY target/docker/tileserver.jar /

CMD ["java", "-jar", "/tileserver.jar"]


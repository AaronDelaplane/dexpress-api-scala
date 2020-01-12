FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine-slim

RUN mkdir /opt/app

COPY target/scala-2.13/dexpress-api-scala-assembly.jar /opt/app

CMD ["java", "-jar", "/opt/app/dexpress-api-scala-assembly.jar"]
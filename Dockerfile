FROM openjdk:17.0.2-jdk

USER root

WORKDIR /app

RUN mkdir -p /app/
RUN cd /app/

COPY src/ /app/src/
COPY pom.xml /app/
COPY .mvn/ /app/.mvn
COPY mvnw /app/
COPY mvnw.cmd /app/

CMD chmod 777 /app/mvnw

ENTRYPOINT ./mvnw spring-boot:run

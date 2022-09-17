FROM ubuntu:20.04

USER root

WORKDIR /app

ENV DEBIAN_FRONTEND="noninteractive" TZ="Europe/Rome"
RUN apt update
RUN apt-get install wget openjdk-11-jdk fonts-liberation libcairo2 libcurl3-gnutls libgbm1 libgtk-3-0 xdg-utils -y
RUN wget -q http://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_99.0.4844.74-1_amd64.deb
RUN dpkg -i google-chrome-stable_99.0.4844.74-1_amd64.deb


RUN mkdir -p /app/
RUN cd /app/

COPY src/ /app/src/
COPY pom.xml /app/
COPY .mvn/ /app/.mvn
COPY mvnw /app/
COPY mvnw.cmd /app/
COPY Driver/ /app/Driver/

RUN mkdir -p /app/html
RUN mkdir -p /app/img

RUN chmod 777 /app/mvnw

ENTRYPOINT ./mvnw clean spring-boot:run

FROM openjdk:11-jre
MAINTAINER "gbouzazi@gmail.com"
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8083

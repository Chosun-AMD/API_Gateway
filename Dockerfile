FROM openjdk:11-jdk
ARG JAR_FILE=./build/libs/apigateway-0.0.1-SNAPSHOT.jar
RUN apt update -y && apt upgrade -y && apt install -y vim && apt install -y curl && apt install -y net-tools && apt install -y iputils-ping
EXPOSE 8000
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

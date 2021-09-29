FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=build/*.jar
COPY ./target/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]

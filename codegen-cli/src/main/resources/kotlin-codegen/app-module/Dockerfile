FROM openjdk:8-alpine
ARG JAR_FILE=triber-application/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=development"]
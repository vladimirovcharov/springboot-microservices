FROM openjdk:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} identity-service.jar
ENTRYPOINT ["java", "-jar", "/identity-service.jar"]
EXPOSE 8084
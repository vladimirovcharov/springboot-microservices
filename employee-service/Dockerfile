FROM openjdk:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} employee-service.jar
ENTRYPOINT ["java", "-jar", "/employee-service.jar"]
EXPOSE 8082
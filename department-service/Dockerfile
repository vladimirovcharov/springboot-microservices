FROM openjdk:19
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} department-service.jar
ENTRYPOINT ["java", "-jar", "/department-service.jar"]
EXPOSE 8081
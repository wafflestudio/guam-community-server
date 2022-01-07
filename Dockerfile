FROM amazoncorretto:11
ARG JAR_FILE=api/build/libs/api.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
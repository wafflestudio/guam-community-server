FROM amazoncorretto:11
ARG JAR_FILE=app/build/libs/app.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
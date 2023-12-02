FROM openjdk:17
ENV TZ=Asia/Seoul
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} ./app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "./app.jar"]
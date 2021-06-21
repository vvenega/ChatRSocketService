FROM adoptopenjdk/openjdk11
WORKDIR /
ARG ChatRSocketService-0.0.1-SNAPSHOT.jar
ADD ChatRSocketService-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8761
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
FROM eclipse-temurin:21-jre

COPY application/build/libs/application-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
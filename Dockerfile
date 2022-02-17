FROM gradle:7.3.3-jdk17-alpine AS buildToJar
COPY . .

FROM eclipse-temurin:17-jdk-focal

COPY --from=buildToJar /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

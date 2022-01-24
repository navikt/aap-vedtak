FROM gradle:7.3.3-jdk17-alpine AS buildToJar
COPY . .
RUN gradle test app:shadowJar --no-daemon --no-build-cache

# Hentet fra "gradle:7.3.3-jdk17-alpine"
FROM eclipse-temurin:17-jdk-alpine
COPY --from=buildToJar /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

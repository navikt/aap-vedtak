# Use docker image with prebuilt dependencies
# This docker layer will also be cached in GHA
FROM ghcr.io/navikt/aap-vedtak-cache AS build-cache
COPY . .
RUN gradle app:shadowJar --no-daemon --no-build-cache

# Bundle the runnable in a distroless image
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build-cache /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

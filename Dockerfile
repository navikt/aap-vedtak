FROM gradle:7.3.3-jdk17-alpine AS buildToJar
COPY . .

FROM eclipse-temurin:17-jdk-alpine

RUN apk update && apk add --no-cache libc6-compat
RUN ln -s /lib64/ld-linux-x86-64.so.2 /lib/ld-linux-x86-64.so.2

COPY --from=buildToJar /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

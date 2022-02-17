FROM gradle:7.3.3-jdk17-alpine AS buildToJar
COPY . .

FROM eclipse-temurin:17-jdk-alpine

RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.34-r0/glibc-2.34-r0.apk
RUN apk add glibc-2.34-r0.apk

COPY --from=buildToJar /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

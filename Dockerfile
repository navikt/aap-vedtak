FROM gradle:7.3.3-jdk17-alpine AS buildToJar
COPY . .

# RocksDB (kafka streams KTable) bruker noen gclibs som ikke er inkludert i alpine.
# Alpine bruker noe som heter 'musl libc' i steden for 'gclib' som vi trenger, og vi får derfor ikke
# lagt inn pakken manuelt. Vi mangler libc6-compat, som finnes i 17-jdk-focal som er ubunt-basert.
FROM eclipse-temurin:17-jdk-focal

COPY --from=buildToJar /home/gradle/app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

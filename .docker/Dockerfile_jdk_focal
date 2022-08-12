# RocksDB (kafka streams KTable) bruker noen gclibs som ikke er inkludert i alpine.
# Alpine bruker noe som heter 'musl libc' i steden for 'gclib' som vi trenger, og vi får derfor ikke
# lagt inn pakken manuelt. Vi mangler libc6-compat, som finnes i 17-jdk-focal som er ubunt-basert.
FROM eclipse-temurin:18-jdk-focal

COPY /app/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y locales

RUN sed -i -e 's/# nb_NO.UTF-8 UTF-8/nb_NO.UTF-8 UTF-8/' /etc/locale.gen && locale-gen
ENV LC_ALL="nb_NO.UTF-8"
ENV LANG="nb_NO.UTF-8"
ENV TZ="Europe/Oslo"

CMD ["java", "-Xmx4G", "-Xms2G", "-XX:+UseParallelGC", "-jar", "app.jar"]

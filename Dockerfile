FROM openjdk:12
COPY ./dist /usr/src/app
WORKDIR /usr/src/app
EXPOSE 12322 12321
ENV TZ="Europe/Copenhagen"
ENTRYPOINT ["java", "-Xmx500m", "-Xms300m", "-jar", "Kepler-Server-all.jar"]

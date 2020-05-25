FROM openjdk:12
COPY ./dist /usr/src/app
WORKDIR /usr/src/app
CMD ["java", "-jar", "Kepler-Server-all.jar"]
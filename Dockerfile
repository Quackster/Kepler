FROM openjdk:12
COPY ./dist /usr/src/app
WORKDIR /usr/src/app
EXPOSE 12322 12321
CMD ["java", "-jar", "Kepler-Server-all.jar"]
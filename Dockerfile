FROM alpine:3.20 AS base

RUN apk add openjdk17=17.0.12_p7-r0

WORKDIR /kepler

FROM base AS build

RUN apk add unzip=6.0-r14

COPY . .

# Convert CRLF to LF (failing build for Windows without this)
RUN sed -i 's/\r$//' ./gradlew

RUN ./gradlew distZip

RUN unzip -qq ./Kepler-Server/build/distributions/Kepler-Server.zip -d ./release

RUN rm -rf ./release/Kepler-Server/bin/* && \
    rm -rf ./release/Kepler-Server/bin && \
    mkdir ./Kepler && \
    mkdir ./Kepler/lib && \
    mv ./release/Kepler-Server/lib/Kepler-Server.jar ./Kepler/kepler.jar && \
    mv ./release/Kepler-Server/lib/* ./Kepler/lib && \
    cp tools/scripts/run.sh ./Kepler/

FROM base AS production

COPY --from=build /kepler/Kepler .
COPY --from=build /kepler/entrypoint.sh .

# Convert CRLF to LF (failing build for Windows without this)
RUN sed -i 's/\r$//' entrypoint.sh
RUN sed -i 's/\r$//' run.sh

RUN chmod +x entrypoint.sh
RUN chmod +x run.sh

ENTRYPOINT [ "sh", "entrypoint.sh" ]

CMD [ "sh", "run.sh" ]
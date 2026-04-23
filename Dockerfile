###############
# BUILD STAGE #
###############

FROM alpine:3.23 AS build

RUN apk add --no-cache openjdk25 unzip

WORKDIR /kepler

# Copy only build configuration first for better layer caching
COPY gradlew settings.gradle ./
COPY gradle/ gradle/
COPY Kepler-Server/build.gradle Kepler-Server/build.gradle

# Fix line endings, make executable, and cache Gradle dependencies
RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew && \
    ./gradlew --no-daemon dependencies

# Copy the rest of the source code
COPY . .

# Build and prepare the distribution
RUN sed -i 's/\r$//' tools/scripts/run.sh && \
    ./gradlew --no-daemon distZip && \
    unzip -qq ./Kepler-Server/build/distributions/Kepler-Server.zip -d ./release && \
    rm -rf ./release/Kepler-Server/bin && \
    mkdir -p ./build/lib && \
    mv ./release/Kepler-Server/lib/Kepler-Server.jar ./build/kepler.jar && \
    mv ./release/Kepler-Server/lib/* ./build/lib && \
    cp tools/scripts/run.sh ./build/

####################
# PRODUCTION STAGE #
####################

FROM alpine:3.23

RUN apk add --no-cache openjdk25-jre-headless && \
    addgroup -S kepler && adduser -S kepler -G kepler && \
    mkdir /kepler && chown kepler:kepler /kepler

WORKDIR /kepler

COPY --from=build --chown=kepler:kepler /kepler/build ./

USER kepler

HEALTHCHECK --interval=10s --timeout=5s --start-period=15s --retries=3 \
  CMD cat /proc/net/tcp /proc/net/tcp6 2>/dev/null | grep -q ":$(printf '%04X' ${SERVER_PORT:-12321}) "

CMD ["sh", "run.sh"]

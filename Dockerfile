FROM ubuntu:18.04
LABEL maintainer="ewout@freedom.nl"

RUN apt update && apt install -y \
    gosu \
    crudini \
    make \
    cmake \
    libuv1-dev \
    sqlite3 \
    libsqlite3-dev \
    libsodium-dev \
    valgrind \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir /usr/src/kepler

COPY src/ /usr/src/kepler/src/
COPY data/ /usr/src/kepler/data/
COPY CMakeLists.txt /usr/src/kepler
COPY kepler.sql /usr/src/kepler

RUN cd /usr/src/kepler && \
    rm -f CMakeCache.txt && \
    rm -f data/Kepler.db && \
    cmake . && \
    make && \
    mv Kepler /usr/bin/kepler && \
    cd

RUN groupadd -g 1000 kepler && \
    useradd -r -u 1000 -g kepler kepler && \
    chown -R kepler:kepler /usr/src/kepler

RUN touch /usr/src/kepler/config.ini && \
    crudini --set /usr/src/kepler/config.ini Server server.ip.address 0.0.0.0 && \
    crudini --set /usr/src/kepler/config.ini Server server.port 12321 && \
    crudini --set /usr/src/kepler/config.ini Rcon rcon.ip.address 0.0.0.0 && \
    crudini --set /usr/src/kepler/config.ini Rcon rcon.port 12309 && \
    crudini --set /usr/src/kepler/config.ini Database database.filename db/Kepler.db && \
    crudini --set /usr/src/kepler/config.ini Game sso.tickets.enabled true && \
    crudini --set /usr/src/kepler/config.ini Game roller.tick.default 6 && \
    crudini --set /usr/src/kepler/config.ini Console debug true && \
    mv /usr/src/kepler/config.ini /usr/src/kepler/tmp.ini && \
    cat /usr/src/kepler/tmp.ini | tr -d "[:blank:]" > /usr/src/kepler/config.ini && \
    cat /usr/src/kepler/config.ini

WORKDIR /usr/src/kepler

VOLUME /usr/src/kepler/db

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

EXPOSE 12321
EXPOSE 12309

CMD ["kepler"]

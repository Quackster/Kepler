# Kepler

A small TCP server written in Java powered by Netty, an asynchronous networking library. It powers the Habbo Hotel version 18 client from 2008 era. The aim is to fully replicate this version.

## Cloning this repository

```
$ git clone --recursive https://github.com/Quackster/Kepler
```

**or**

```
$ git clone https://github.com/Quackster/Kepler
$ git submodule update --init --recursive
```

## Compile Kepler

*This is temporarily here*

Now to build the docker container you'd use;

./gradlew jibDockerBuild --image quackster/kepler 

docker tag quackster/kepler:latest quackster/kepler:$(git rev-parse --short HEAD)

And then update docker-compose.yml to make kepler point to the new quackster/kepler:$(git rev-parse --short HEAD) image
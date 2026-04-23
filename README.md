# Kepler

Kepler is a Habbo Hotel emulator that is designed to fully emulate the v14 version from 2007 era. 
The server is written in Java and using various libraries, such as Netty, an asynchronous networking library, Log4j and the Apache commons libraries.

It is quite possibly the most complete v14 Habbo Hotel released to date, and has been in development since January 2018.

# Screenshots

(Hotel view)

![https://i.imgur.com/8eFvtdA.png](https://i.imgur.com/8eFvtdA.png)

(Automatic rare cycler)

![https://i.imgur.com/8RTFFqD.png](https://i.imgur.com/8RTFFqD.png)

(Camera)

![https://i.imgur.com/emseVbU.png](https://i.imgur.com/emseVbU.png)

(BattleBall)

![https://i.imgur.com/a3MgkzU.png](https://i.imgur.com/a3MgkzU.png)

![https://i.imgur.com/eUGmcwR.png](https://i.imgur.com/eUGmcwR.png)

(Chess)

![https://i.imgur.com/xundc8M.png](https://i.imgur.com/xundc8M.png)

(Tic Tac Toe)

![https://i.imgur.com/tTG5SVE.png](https://i.imgur.com/tTG5SVE.png)

# Download

Download the latest development build from the [releases page](https://github.com/Quackster/Kepler/releases).

### Requirements

To be honest, this server doesn't require much. I'd argue that the MariaDB server is more resource demanding than the emulator itself. 

- JDK >= 25
- MariaDB server

# Installation

Install MariaDB server, connect to the database server and import Kepler.sql (located in /tools/Kepler.sql).

Download the latest development build from the [releases page](https://github.com/Quackster/Kepler/releases) and rename the files to remove the short build hash version, for convenience. 

Install any JDK version that is equal or above >= 25 to run the jar files.

Open run.sh (Linux) or run.bat (Windows) to start Kepler.

❗ Once registered as an admin, make yourself admin by setting your ``rank`` to 7 in the ``users`` table.

As for the client, you can find version 14 DCRs: https://github.com/Quackster/Kepler-www/

Setup the loader files on a web server, and once Kepler is started, ensure the loader is connecting to the correct IP and ports for both the standard connection and MUS connection. The MUS connection is used for the camera.

# Docker installation

Install [Docker](https://docs.docker.com/engine/install/) and [git](https://git-scm.com/downloads) (optional) on your device.

### 1. Clone repository

```shell
git clone https://github.com/Quackster/Kepler.git
```

_You can also [download](https://github.com/Quackster/Kepler/archive/refs/heads/master.zip) this repository and unzip it._

### 2. Configure variables

Copy `.env.example` file to `.env` :

```shell
cp .env.example .env
```

You can now configure all variables in `.env` file with values needed.

_Don't change `MYSQL_HOST` except if you change the name of the service `mariadb` in Docker compose file._

_You neither should change `MYSQL_PORT`._

### 3. Start Kepler

You just need to run Docker compose inside of Kepler directory :

```shell
docker compose up -d
```

To stop Kepler :

```shell
docker compose down
```

### Docker FAQ

#### Reset MariaDB database

You need to first stop Kepler, then remove MariaDB volume :

```shell
docker compose down && docker volume rm kepler-mariadb
```

You can now start Kepler again, database will be wiped out !

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.


## Cloning this repository

```
$ git clone --recursive https://github.com/Quackster/Kepler
```

**or**

```
$ git clone https://github.com/Quackster/Kepler
$ git submodule update --init --recursive
```

# Thanks to

* Hoshiko
* ThuGie
* Ascii
* Lightbulb
* Raptosaur
* Romuald
* Glaceon
* Nillus
* Holo Team
* wackfx
* Meth0d
* office.boy
* Leon Hartley
* Alito
* wackfx

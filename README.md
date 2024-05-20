# Odin

Odin is fork of (https://github.com/quackster/kepler) a Habbo Hotel emulator that is designed to fully emulate a <strike>v14</strike> custom version from the 2006 era. The server is written in Java and using various libraries, such as Netty, an asynchronous networking library, Log4j and the Apache commons libraries.

The server is as mentioned designed to run a custom version of Habbo Hotel client. At the time of writing it supports:

- Messenger > v14 (Go to same room, multiple friend requests). It does not support lower versions of the Messenger.
- Windows and Landscapes
- Valentines Love "roulette" Sofa

# Screenshots

(Hotel view)

![https://i.imgur.com/8eFvtdA.png](https://i.imgur.com/8eFvtdA.png)

(Camera)

![https://i.imgur.com/emseVbU.png](https://i.imgur.com/emseVbU.png)

(BattleBall)

![https://i.imgur.com/a3MgkzU.png](https://i.imgur.com/a3MgkzU.png)

![https://i.imgur.com/eUGmcwR.png](https://i.imgur.com/eUGmcwR.png)

(Chess)

![https://i.imgur.com/xundc8M.png](https://i.imgur.com/xundc8M.png)

(Tic Tac Toe)

![https://i.imgur.com/tTG5SVE.png](https://i.imgur.com/tTG5SVE.png)

## Basic usage instructions

### Pre-requisites

- Download web browser with Flash/Shockwave built-in [here](http://forum.ragezone.com/f353/portable-browser-flash-shockwave-basilisk-1192727/)
- Download and install the latest version of MariaDB
- Download and install Java JDK 11+
- <strike>Habbo Hotel v14 DCR pack [here](https://web.archive.org/web/20220724030154/https://raw.githubusercontent.com/Quackster/Kepler/master/tools/Quackster_v14.zip)</strike> TBA

To be honest, this server doesn't require much. I'd argue that the MariaDB server is more resource demanding than the emulator itself.

- JDK >= 17
- MariaDB server

# Installation

Install MariaDB server, connect to the database server and import Kepler.sql (located in /tools/Kepler.sql).

Download the latest development build from the [releases page](https://github.com/Quackster/Kepler/releases) and rename the files to remove the short build hash version, for convenience.

Install any JDK version that is equal or above >= 17 to run the jar files.

Open run.sh (Linux) or run.bat (Windows) to start Kepler.

I highly recommend [this browser](https://forum.oldskooler.org/threads/portable-browser-with-flash-shockwave-basilisk.70/) to be able to play Adobe Shockwave movies in the present day.

❗ Once registered as an admin, make yourself admin by setting your `rank` to 7 in the `users` table.

As for the client, you can find version 14 DCRs [here](https://web.archive.org/web/20220724030154/https://raw.githubusercontent.com/Quackster/Kepler/master/tools/Quackster_v14.zip).

Setup the loader files on a web server, and once Kepler is started, ensure the loader is connecting to the correct IP and ports for both the standard connection and MUS connection. The MUS connection is used for the camera.

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

### Custom Hotel Views

For archival purposes, there's a zip folder containing a bunch of iconic custom hotel views in /tools/

## Cloning this repository

```
$ git clone --recursive https://github.com/Quackster/Kepler
```

**or**

```
$ git clone https://github.com/Quackster/Kepler
$ git submodule update --init --recursive
```

## Development

IntelliJ config:
![intellijconfig.PNG](intellijconfig.PNG)

Use gradle wrapper config and Java 12.0.

# Thanks to

- Hoshiko
- ThuGie
- Ascii
- Lightbulb
- Raptosaur
- Romuald
- Glaceon
- Nillus
- Holo Team
- Meth0d
- office.boy
- Leon Hartley

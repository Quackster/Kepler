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

- JDK >= 17
- MariaDB server

# Installation

Install MariaDB server, connect to the database server and import Kepler.sql (located in /tools/Kepler.sql).

Download the latest development build from the [releases page](https://github.com/Quackster/Kepler/releases) and rename the files to remove the short build hash version, for convenience. 

Install any JDK version that is equal or above >= 17 to run the jar files.

Open Kepler-Server.jar via

```
java -jar Kepler-Server.jar
```

Your server should be up and running and accessible via http://localhost/

I highly recommend [this browser](https://forum.ragezone.com/f353/portable-browser-with-flash-shockwave-1192727/) to be able to play Adobe Shockwave movies in the present day.

❗ Once registered as an admin, make yourself admin by setting your ``rank`` to 7 in the ``users`` table.

As for the client, you can find version 14 DCRs [here](https://web.archive.org/web/20220724030154/https://raw.githubusercontent.com/Quackster/Kepler/master/tools/Quackster_v14.zip).

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
* Meth0d
* office.boy

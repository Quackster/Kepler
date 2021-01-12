# Kepler

A small TCP server written in Java powered by Netty, an asynchronous networking library. Kepler is a Habbo Hotel emulator that is designed to fully emulate the v14 version from 2007 era, and upwards. The server is written in Java (JDK 11) and it's using various libraries, which means it's multiplatform, as in supports a wide range of operating systems. Windows, Linux distros, etc.

The server has many features added, and a lot of configuration settings. Most of the configuration settings will be explained below. This server supports version v9 through to v26, and different clients can connect at once, by the server configuration generated. Its support of v9 and v26 are completely experiental, but the full support is of v14, v15 and v21.

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

## Basic usage instructions

### Pre-requisites

- Download web browser with Flash/Shockwave built-in [here](http://forum.ragezone.com/f353/portable-browser-flash-shockwave-basilisk-1192727/)
- Download and install the latest version of MariaDB
- Download and install Java JDK 12+
- Download and install PHP 7+

### Prepare the emulator

- Download the latest release of this repo
- Extract the archive into a folder named **Kepler**
- Download a v14 DCR pack that you can find in [this topic](http://forum.ragezone.com/f353/kepler-java-v14-server-snowstorm-1155773/)
- Extract the archive in the same **Kepler** folder

### Prepare the database

- Create a database named `kepler`
- Create a user named `kepler` using the password `verysecret`
- Import the `keplerdb.sql` file into the newly created database

### Running the emulator

- Edit the file `run.bat` from the **Kepler** folder and update the `java.exe` path to the one in your 
- Execute the file `run.bat` (it will start the server and create a `server.ini` file that you can modify later on, if needed)

### Running the server

- Create a PHP webserver in the **Kepler** folder by using the following command: `php -S localhost:80`

### Play in the emulator

- Run the downloaded web-browser and navigate to http://localhost/v14

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

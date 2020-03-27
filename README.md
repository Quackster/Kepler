# Kepler

A small TCP server written in Java powered by Netty, an asynchronous networking library. Kepler is a Habbo Hotel emulator that is designed to fully emulate the v14 version from 2007 era, and upwards. The server is written in Java (JDK 11) and it's using various libraries, which means it's multiplatform, as in supports a wide range of operating systems. Windows, Linux distros, etc.

The server has many features added, and a lot of configuration settings. Most of the configuration settings will be explained below. This server supports version v9 through to v26, and different clients can connect at once, by the server configuration generated. Its support of v9 and v26 are completely experiental, but the full support is of v14, v15 and v21.

# Features

* User

* Login by SSO ticket
* Load fuserights
* Load credits
* In-game register


* Navigator

* Lists all public rooms 
* Lists all private rooms
* Navigator category management with rank checking for private rooms
* Navigator category management with rank checking for public rooms
* Show recent private rooms created in the categories even if the room owner wasn't online
* Create private rooms using the navigator
* Show own rooms
* Hide room owner name
* Edit room settings
* Delete room


* Habbo Club

* Purchase Habbo club
* Expiry of Habbo club
* Show days left
* Habbo club gifts


* Messenger

* Search users on console
* Send user a friend request
* Accept friend request
* Reject friend request
* Send friend message
* Delete friend
* Change messenger motto
* Mark messages are read
* Show offline messages
* Follow friend
* Automatic update friends list


* Private room

* Walking
* Walk to door
* Chat (and message gets worse quality if you're further away from someone in public rooms)
* Shout
* Whisper
* Password protect room
* Use room doorbell
* Room voting (with expiry)


* Public Room

* All possible public rooms added (some may be missing)
* All public rooms are fully furnished to what official Habbo had
* Sitting on furniture in public rooms
* Walk between public rooms


* Lido and Diving Deck

* Change clothes working (with curtain closing)
* Pool lift door closes and opens depending if a user is inside or not
* Buying tickets work for self and other players
* Diving
* Swimming
* Queue works (line up on first tile and the user automatically walks when there is a free spot)
* Lido voting


* Item

* Show own hand (inventory) with items in it
* Place room items
* Move and rotate room items
* Pickup room item
* Place wall items
* Pickup wall items
* Stack items

* Apply room decorations


* Completed Item Interactions

* Dice
* Bottles
* Teleporters
* Rollers
* Scoreboard
* Lert
* Camera
* Gate
* Trax
* Jukebox


* Catalogue

* Show catalogue pages
* Show catalogue items and deals (aka packages)
* Purchase items and packages
* Purchase items with credits
* Recycler


* Purse

* Redeem vouchers to get credits
* Redeem vouchers to get credits and items
* Redeem vouchers to get items


* Games

* Chess
* Tic-Tac-Toe
* BattleShips
* Wobble Squabble


* BattleBall

* Join games
* Cancel games
* Spectate games
* All power ups
* Flood fill


* Ranked features

* Add badge automatically if they are a certain rank
* Command registration checking


* Commands

* :about
* :help
* :reload items/settings/models
* :pickall
* :whosonline/usersonline
* :givedrink <target>
* :afk/idle
* :motto
* :givebadge
* :setprice <sale code> <price>
* :setconfig <config entry in settings table> <new value>
* :hotelalert <msg>
* :talk <msg> 

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
* Alito
* Ascii
* Lightbulb
* Raptosaur
* Romuald
* Glaceon
* Nillus
* Holo Team
* Meth0d
* office.boy

package org.alexdev.kepler.game.games.gamehalls;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.gamehalls.utils.GameShip;
import org.alexdev.kepler.game.games.gamehalls.utils.GameShipMove;
import org.alexdev.kepler.game.games.gamehalls.utils.GameShipMoveResult;
import org.alexdev.kepler.game.games.gamehalls.utils.GameShipType;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameBattleShip extends GamehallGame {
    private Map<GameShip, Integer> shipsPlaced;
    private Map<Integer, List<GameShipMove>> playerListMap;
    private Player[] players;
    private int nextTurn;
    private boolean isTurnUsed;
    private boolean gameStarted;
    private boolean gameEnded;

    public GameBattleShip(List<int[]> kvp) {
        super(kvp);
    }

    @Override
    public void gameStart() {
        this.shipsPlaced = new HashMap<>();
        this.playerListMap = new HashMap<>();
        this.players = new Player[2];
        this.nextTurn = 0;
        this.isTurnUsed = false;
        this.gameStarted = false;
        this.gameEnded = false;
    }

    @Override
    public void gameStop() {
        this.shipsPlaced = new HashMap<>();
        this.playerListMap = new HashMap<>();
        this.players = new Player[2];
        this.nextTurn = 0;
        this.isTurnUsed = false;
        this.gameStarted = false;
        this.gameEnded = false;
    }

    @Override
    public void joinGame(Player player) {
        if (!this.gameStarted) {
            return;
        }

        if (this.getPlayerNum(player) == -1) {
            if (this.players[0] == null) {
                this.players[0] = player;
            } else {
                if (this.players[1] == null) {
                    this.players[1] = player;
                }
            }
        }

        String[] opponentData = new String[2];

        int i = 0;
        for (Player p : this.players) {
            opponentData[i] = this.getPlayerNum(p) + " " + this.players[i].getDetails().getName();
            i++;
        }

        this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "OPPONENTS", String.join(Character.toString((char) 13), opponentData)}));
        this.sendMarkedMap();
    }

    @Override
    public void leaveGame(Player player) {
        if (this.nextTurn == getPlayerNum(player)) {
            rotateTurn();
        }

        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i] == player) {
                this.players[i] = null;
            }
        }
    }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        if (command.equals("PLACESHIP")) {
            if (this.gameStarted) {
                return;
            }

            int shipId = Integer.parseInt(args[0]);
            int startX = Integer.parseInt(args[1]);
            int startY = Integer.parseInt(args[2]);

            int endX = Integer.parseInt(args[3]);
            int endY = Integer.parseInt(args[4]);

            GameShipType shipType = GameShipType.getById(shipId);

            if (shipType == null) {
                return;
            }

            if (this.getPlayerNum(player) == -1) {
                if (this.players[0] == null) {
                    this.players[0] = player;
                } else {
                    if (this.players[1] == null) {
                        this.players[1] = player;
                    }
                }
            }

            if (this.countShips(shipType, getPlayerNum(player)) >= shipType.getMaxAllowed()) {
                return;
            }

            if (!this.playerListMap.containsKey(getPlayerNum(player))) {
                this.playerListMap.put(getPlayerNum(player), new ArrayList<>());
            }

            boolean isHorizontal = (startY == endY);

            this.shipsPlaced.put(new GameShip(this, shipType, new Position(startX, startY), getPlayerNum(player), isHorizontal), getPlayerNum(player));
            boolean isEveryoneFinsihed = this.hasEveryoneFinished();

            if (isEveryoneFinsihed) {
                String[] opponentData = new String[2];

                int i = 0;
                for (Player p : this.players) {
                    opponentData[i] = this.getPlayerNum(p) + " " + this.players[i].getDetails().getName();
                    i++;
                }

                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "OPPONENTS", String.join(Character.toString((char) 13), opponentData)}));
                this.rotateTurn();
                this.gameStarted = true;
            }
        }

        if (command.equals("SHOOT")) {
            if (this.nextTurn != getPlayerNum(player)) {
                return;
            }

            if (this.isTurnUsed) {
                return;
            }

            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            GameShipMoveResult moveResult = GameShipMoveResult.MISS;

            if (this.isHit(x, y, getPlayerNum(player))) {
                moveResult = GameShipMoveResult.HIT;
            }

            var oppositePlayer = this.getOppositePlayer(player);

            if (oppositePlayer == null) {
                return;
            }

            GameShip gameShip = this.getShipPlaced(x, y, getPlayerNum(player));
            this.playerListMap.get(getPlayerNum(player)).add(new GameShipMove(player, x, y, moveResult, gameShip));

            this.sendMarkedMap();

            if (gameShip != null) {
                if (gameShip.isDestroyed()) {
                    this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "SINK" }));
                } else if (gameShip.isHitTwice()) {
                    this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "HITTWICE" }));
                } else {
                    this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "HIT" }));
                }
            } else {
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "MISS" }));
            }

            //this.isTurnUsed = true;
            GameShipMoveResult finalMoveResult = moveResult;

            //GameScheduler.getInstance().getService().schedule(() -> {
            if (finalMoveResult == GameShipMoveResult.MISS) {
                this.isTurnUsed = true;
                GameScheduler.getInstance().getService().schedule(this::rotateTurn, 2, TimeUnit.SECONDS);
            } else {
                this.isTurnUsed = false;
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "TURN", String.valueOf(this.nextTurn)}));
            }
            //}, 5, TimeUnit.SECONDS);

            if (this.isGameOver(oppositePlayer)) {
                this.gameEnded = true;
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "GAMEEND", player.getDetails().getName()}));
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "GAMEOVER"}));
                this.isTurnUsed = true;
                //GameManager.getInstance().giveRandomCredits(player, true);
                //GameManager.getInstance().giveRandomCredits(this.getOppositePlayer(player), false);

            }

            player.getRoomUser().getTimerManager().resetRoomTimer();
            //GameScheduler.getInstance().getService().schedule(this::rotateTurn, 5, TimeUnit.SECONDS);
            //GameScheduler.getInstance().getService().schedule(this::sendMarkedMap, 5, TimeUnit.SECONDS);

        }

        if (command.equals("CLOSE")) {
            trigger.onEntityLeave(player, player.getRoomUser(), item);
            return;
        }
    }

    private void rotateTurn() {
        this.nextTurn = getOppositePlayerNum(this.nextTurn);
        this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "TURN", String.valueOf(this.nextTurn) }));
        this.isTurnUsed = false;
    }

    private void sendMarkedMap() {
        for (Player p : this.players) {
            var opponent = getOppositePlayer(p);

            if (opponent == null) {
                return;
            }

            if (getPlayerNum(p) == 0) {
                p.send(new ITEMMSG(new String[]{this.getGameId(), "SITUATION",
                        "", generateHitGrid(getPlayerNum(p)),
                        "", generateHitGrid(getPlayerNum(opponent)),
                }));
            } else {
                p.send(new ITEMMSG(new String[]{this.getGameId(), "SITUATION",
                        "", generateHitGrid(getPlayerNum(opponent)),
                        "", generateHitGrid(getPlayerNum(p)),
                }));
            }
        }
    }


    private String generateHitGrid(int player) {
        StringBuilder map = new StringBuilder();

        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 13; x++) {
                Position position = new Position(x, y);

                GameShipMove shipMove = this.playerListMap.get(this.getOppositePlayerNum(player)).stream().filter(move -> move.getX() == position.getX() && move.getY() == position.getY()).findFirst().orElse(null);

                if (shipMove == null) {
                    map.append("-");
                } else {
                    if (shipMove.getShip() != null && shipMove.getShip().isDestroyed()) {
                        map.append(GameShipMoveResult.SINK.getSymbol());
                    } else {
                        map.append(shipMove.getMoveResult().getSymbol());
                    }
                }
            }
        }

        return map.toString();
    }

    private boolean isHit(int selectX, int selectY, int player) {
        return getShipPlaced(selectX, selectY, player) != null;
    }

    private GameShip getShipPlaced(int x, int y, int player) {
        List<GameShip> opponentShips = this.shipsPlaced.keySet().stream().filter(ship -> ship.getPlayer() == getOppositePlayerNum(player)).collect(Collectors.toList());

        for (GameShip gameShip : opponentShips) {
            for (int i = 0; i < gameShip.getShipType().getLength(); i++) {
                int shipX = gameShip.getPosition().getX() + (gameShip.isHorizontal() ? i : 0);
                int shipY = gameShip.getPosition().getY() + (gameShip.isHorizontal() ? 0 : i);

                if (x == shipX && y == shipY) {
                    return gameShip;
                }
            }
        }

        return null;
    }

    /**
     * Get the player number for the player.
     *
     * @param player the player number
     * @return the number
     */
    public int getPlayerNum(Player player) {
        var playerList = Arrays.asList(this.players);
        return playerList.contains(player) ? playerList.indexOf(player) : - 1;
    }

    private boolean isGameOver(Player player) {
        for (var kvp : this.shipsPlaced.entrySet()) {
            if (kvp.getValue() != getPlayerNum(player)) {
                continue;
            }

            if (!kvp.getKey().isDestroyed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the opposite player playing
     *
     * @param player the player
     * @return the opponent
     */
    public Player getOppositePlayer(Player player) {
        for (Player p : this.players) {
            if (p != player) {
                return p;
            }
        }

        return null;
    }


    public Integer getOppositePlayerNum(int player) {
        return player == 0 ? 1 : 0;
    }

    /**
     * Gets if both players have finished placing their pieces.
     *
     * @return true, if successful
     */
    private boolean hasEveryoneFinished() {
        if (this.countShips(null, 0) != 10) {
            return false;
        }

        if (this.countShips(null, 1) != 10) {
            return false;
        }

        return true;
    }

    /**
     * Count the ships placed on the map.
     *
     * @param shipType the ship type (optional)
     * @param player the player (optional)
     * @return the count of the ships
     */
    private int countShips(GameShipType shipType, int player) {
        List<GameShip> gameShipTypes = null;

        if (shipType != null) {
            if (player != -1) {
                gameShipTypes = this.shipsPlaced.keySet().stream().filter(ship -> ship.getShipType() == shipType && ship.getPlayer() == player).collect(Collectors.toList());
            } else {
                gameShipTypes = this.shipsPlaced.keySet().stream().filter(ship -> ship.getShipType() == shipType).collect(Collectors.toList());
            }
        } else {
            if (player != -1) {
                gameShipTypes = this.shipsPlaced.keySet().stream().filter(ship -> ship.getPlayer() == player).collect(Collectors.toList());
            } else {
                gameShipTypes = new ArrayList<>(this.shipsPlaced.keySet());
            }
        }

        return gameShipTypes.size();
    }

    @Override
    public int getMaximumPeopleRequired() {
        return 2;
    }

    @Override
    public int getMinimumPeopleRequired() {
        return 1;
    }

    @Override
    public String getGameFuseType() {
        return "BattleShip";
    }

    public Map<Integer, List<GameShipMove>> getPlayerListMap() {
        return playerListMap;
    }
}
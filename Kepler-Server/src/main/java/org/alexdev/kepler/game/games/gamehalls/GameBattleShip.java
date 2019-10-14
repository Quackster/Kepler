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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameBattleShip extends org.alexdev.kepler.game.games.gamehalls.GamehallGame {
    private Map<GameShip, Player> shipsPlaced;
    private Map<Player, List<GameShipMove>> playerListMap;
    private Player[] players;
    private Player nextTurn;
    private boolean isTurnUsed;
    private boolean gameEnded;

    public GameBattleShip(List<int[]> kvp) {
        super(kvp);
    }

    @Override
    public void gameStart() {
        this.shipsPlaced = new HashMap<>();
        this.playerListMap = new HashMap<>();
        this.players = new Player[2];
        this.nextTurn = null;
        this.isTurnUsed = false;
        this.gameEnded = false;
    }

    @Override
    public void gameStop() { }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        if (command.equals("PLACESHIP")) {
            int shipId = Integer.parseInt(args[0]);
            int startX = Integer.parseInt(args[1]);
            int startY = Integer.parseInt(args[2]);

            int endX = Integer.parseInt(args[3]);
            int endY = Integer.parseInt(args[4]);

            GameShipType shipType = GameShipType.getById(shipId);

            if (shipType == null) {
                return;
            }

            if (this.countShips(shipType, player) >= shipType.getMaxAllowed()) {
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

            if (!this.playerListMap.containsKey(player)) {
                this.playerListMap.put(player, new ArrayList<>());
            }

            boolean isHorizontal = (startY == endY);

            int shipX = startX;
            int shipY = startY;

            this.shipsPlaced.put(new GameShip(this, shipType, new Position(shipX, shipY), player, isHorizontal), player);
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
            }
        }

        if (command.equals("SHOOT")) {
            if (this.nextTurn != player) {
                return;
            }

            if (this.isTurnUsed) {
                return;
            }

            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            GameShipMoveResult moveResult = GameShipMoveResult.MISS;

            if (this.isHit(x, y, player)) {
                moveResult = GameShipMoveResult.HIT;
            }

            var oppositePlayer = this.getOppositePlayer(player);

            if (oppositePlayer == null) {
                return;
            }

            GameShip gameShip = this.getShipPlaced(x, y, player);
            this.playerListMap.get(player).add(new GameShipMove(player, x, y, moveResult, gameShip));
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
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "TURN", String.valueOf(this.getPlayerNum(this.nextTurn))}));
            }
            //}, 5, TimeUnit.SECONDS);

            if (this.isGameOver(oppositePlayer)) {
                this.gameEnded = true;
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "GAMEEND", player.getDetails().getName()}));
                this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "GAMEOVER"}));
                this.isTurnUsed = true;

            }

            //GameScheduler.getInstance().getService().schedule(this::rotateTurn, 5, TimeUnit.SECONDS);
            //GameScheduler.getInstance().getService().schedule(this::sendMarkedMap, 5, TimeUnit.SECONDS);

        }

        if (command.equals("CLOSE")) {
            trigger.onEntityLeave(player, player.getRoomUser(), item);
            return;
        }
    }

    private void rotateTurn() {
        if (this.nextTurn == null) {
            this.nextTurn = this.players[0];
        } else {
            if (this.nextTurn == this.players[0]) {
                this.nextTurn = this.players[1];
            } else {
                this.nextTurn = this.players[0];
            }
        }

        this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "TURN", String.valueOf(this.getPlayerNum(this.nextTurn)) }));
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
                        "", generateHitGrid(p),
                        "", generateHitGrid(opponent),
                }));
            } else {
                p.send(new ITEMMSG(new String[]{this.getGameId(), "SITUATION",
                        "", generateHitGrid(opponent),
                        "", generateHitGrid(p)
                }));
            }
        }
    }


    private String generateHitGrid(Player player) {
        StringBuilder map = new StringBuilder();

        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 13; x++) {
                Position position = new Position(x, y);

                GameShipMove shipMove = this.playerListMap.get(this.getOppositePlayer(player)).stream().filter(move -> move.getX() == position.getX() && move.getY() == position.getY()).findFirst().orElse(null);

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

    private boolean isHit(int selectX, int selectY, Player player) {
        return getShipPlaced(selectX, selectY, player) != null;
    }

    private GameShip getShipPlaced(int x, int y, Player player) {
        Player oppositePlayer = (this.players[0] == player) ? this.players[1] : this.players[0];
        List<GameShip> opponentShips = this.shipsPlaced.keySet().stream().filter(ship -> ship.getPlayer() == oppositePlayer).collect(Collectors.toList());

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
    private int getPlayerNum(Player player) {
        int i = 0;
        for (Player p : this.players) {
            if (p == player) {
                return i;
            } else {
                i++;
            }
        }

        return -1;
    }

    private boolean isGameOver(Player player) {
        for (var kvp : this.shipsPlaced.entrySet()) {
            if (kvp.getValue() != player) {
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

    /**
     * Gets if both players have finished placing their pieces.
     *
     * @return true, if successful
     */
    private boolean hasEveryoneFinished() {
        for (Player player : this.players) {
            if (player == null) {
                return false;
            }

            if (this.countShips(null, player) != 10) {
                return false;
            }
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
    private int countShips(GameShipType shipType, Player player) {
        List<GameShip> gameShipType = null;

        if (shipType != null) {
            if (player != null) {
                gameShipType = this.shipsPlaced.keySet().stream().filter(ship -> ship.getShipType() == shipType && ship.getPlayer() == player).collect(Collectors.toList());
            } else {
                gameShipType = this.shipsPlaced.keySet().stream().filter(ship -> ship.getShipType() == shipType).collect(Collectors.toList());
            }
        } else {
            if (player != null) {
                gameShipType = this.shipsPlaced.keySet().stream().filter(ship -> ship.getPlayer() == player).collect(Collectors.toList());
            } else {
                gameShipType = new ArrayList<>(this.shipsPlaced.keySet());
            }
        }

        return gameShipType.size();
    }

    @Override
    public int getMaximumPeopleRequired() {
        return 2;
    }

    @Override
    public int getMinimumPeopleRequired() {
        return 2;
    }

    @Override
    public String getGameFuseType() {
        return "BattleShip";
    }

    public Map<GameShip, Player> getShipsPlaced() {
        return shipsPlaced;
    }

    public Map<Player, List<GameShipMove>> getPlayerListMap() {
        return playerListMap;
    }
}
package org.alexdev.kepler.game.games.battleball.powerups;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.battleball.BattleBallTile;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallColourState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallTileState;
import org.alexdev.kepler.game.games.battleball.events.PlayerMoveEvent;
import org.alexdev.kepler.game.games.battleball.objects.PlayerUpdateObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.utils.PowerUpUtil;
import org.alexdev.kepler.game.games.utils.TileUtil;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CannonHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        gamePlayer.getPlayer().getRoomUser().stopWalking();
        gamePlayer.getPlayer().getRoomUser().setWalkingAllowed(false);

        Position firstPosition = gamePlayer.getPlayer().getRoomUser().getPosition();

        Position nextPosition = firstPosition.copy();
        int rotation = nextPosition.getRotation();

        LinkedList<BattleBallTile> tilesToUpdate = new LinkedList<>();
        List<Pair<GamePlayer, Position>> stunnedPlayers = new ArrayList<>();

        while (TileUtil.isValidGameTile(gamePlayer, (BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()), false)) {
            nextPosition = nextPosition.getSquareInFront();

            if (!TileUtil.isValidGameTile(gamePlayer, (BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()), false)) {
                break;
            }

            BattleBallTile battleballTile = (BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY());

            tilesToUpdate.add(battleballTile);

            for (GamePlayer p : battleballTile.getPlayers(gamePlayer)) {
                if (p == gamePlayer) {
                    continue;
                }

                stunnedPlayers.add(Pair.of(p, nextPosition));
            }
        }

        if (tilesToUpdate.isEmpty()) {
            nextPosition = gamePlayer.getPlayer().getRoomUser().getPosition();
            tilesToUpdate.add((BattleBallTile) game.getTile(nextPosition.getX(), nextPosition.getY()));
        }

        // Stun players in direction of cannon and make them move out of the way
        GameScheduler.getInstance().getSchedulerService().schedule(() -> {
            for (var kvp : stunnedPlayers) {
                try {
                    // TODO: Move player out of the way of user using cannon https://www.youtube.com/watch?v=YX1UZky5pg0&feature=youtu.be&t=98
                    GamePlayer stunnedPlayer = kvp.getKey();
                    Position pushedFrom = kvp.getValue().copy();
                    pushedFrom.setRotation(rotation);

                    List<Position> pushedTo = new ArrayList<>();
                    pushedTo.add(pushedFrom.getSquareRight());
                    pushedTo.add(pushedFrom.getSquareLeft());

                    Position setPosition = null;

                    // Find best position to move player to
                    for (Position position : pushedTo) {
                        if (TileUtil.isValidGameTile(stunnedPlayer, (BattleBallTile) game.getTile(position.getX(), position.getY()), true)) {
                            setPosition = position;
                            break;
                        }
                    }

                    if (setPosition != null) {
                        game.addPlayerMove(new PlayerMoveEvent(stunnedPlayer, setPosition));
                    }

                    // Stun player
                    PowerUpUtil.stunPlayer(game, stunnedPlayer, BattleBallPlayerState.STUNNED);

                    // Set player at teir new spot
                    if (setPosition != null) {
                        setPosition.setRotation(stunnedPlayer.getPlayer().getRoomUser().getPosition().getRotation());
                        stunnedPlayer.getPlayer().getRoomUser().setPosition(setPosition);
                        stunnedPlayer.getPlayer().getRoomUser().warp(setPosition, false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 200, TimeUnit.MILLISECONDS);


        for (BattleBallTile tile : tilesToUpdate) {
            if (tile.getColour() == BattleBallColourState.DISABLED) {
                continue;
            }

            if (tile.getState() == BattleBallTileState.SEALED && tile.getColour().getColourId() == gamePlayer.getTeam().getId()) {
                continue;
            }


            BattleBallTileState state = tile.getState();
            BattleBallColourState colour = tile.getColour();

            BattleBallTileState newState = BattleBallTileState.SEALED;
            BattleBallColourState newColour = BattleBallColourState.getColourById(gamePlayer.getTeam().getId());

            BattleBallTile.getNewPoints(gamePlayer, state, colour, newState, newColour);

            tile.setColour(newColour);
            tile.setState(newState);

            BattleBallTile.checkFill(gamePlayer, tile, game.getFillTilesQueue());
            game.getUpdateTilesQueue().add(tile);
        }

        BattleBallTile lastTile = tilesToUpdate.getLast();

        Position lastPosition = lastTile.getPosition().copy();
        lastPosition.setRotation(rotation);

        gamePlayer.getPlayer().getRoomUser().setPosition(firstPosition);
        gamePlayer.setPlayerState(BattleBallPlayerState.FLYING_THROUGH_AIR);

        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        game.addPlayerMove(new PlayerMoveEvent(gamePlayer, lastPosition));

        GameScheduler.getInstance().getSchedulerService().schedule(() -> {
            gamePlayer.getPlayer().getRoomUser().setPosition(lastPosition);
            PowerUpUtil.stunPlayer(game, gamePlayer, BattleBallPlayerState.STUNNED);
        }, 800, TimeUnit.MILLISECONDS);

        gamePlayer.getPlayer().getRoomUser().warp(lastTile.getPosition(), false);

    }
}

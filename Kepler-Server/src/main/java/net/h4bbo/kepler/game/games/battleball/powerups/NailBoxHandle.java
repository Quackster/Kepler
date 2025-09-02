package net.h4bbo.kepler.game.games.battleball.powerups;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallColourState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallTileState;
import net.h4bbo.kepler.game.games.battleball.events.DespawnObjectEvent;
import net.h4bbo.kepler.game.games.battleball.events.PinSpawnEvent;
import net.h4bbo.kepler.game.games.battleball.objects.PinObject;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.utils.PowerUpUtil;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NailBoxHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        //gamePlayer.getPlayer().getRoomUser().stopWalking();

        List<GamePlayer> dizzyPlayers = new ArrayList<>();
        List<GameObject> pins = new ArrayList<>();

        Position tilePosition = gamePlayer.getPlayer().getRoomUser().getPosition()
                .getSquareInFront()
                .getSquareInFront()
                .getSquareInFront()
                .getSquareInFront()
                .getSquareInFront()
                //.getSquareInFront()
                .getSquareInFront();

        /*
        var tile2 = (BattleBallTile) game.getTile(tilePosition.getX(), tilePosition.getY());
        BattleBallTileState state = tile2.getState();
        BattleBallColourState colour = tile2.getColour();

        BattleBallTileState newState = BattleBallTileState.CLICKED;
        BattleBallColourState newColour = BattleBallColourState.getColourById(gamePlayer.getTeam().getId());

        tile2.setColour(newColour);
        tile2.setState(newState);

        game.getUpdateTilesQueue().add(tile2);*/

        int maxPins = ThreadLocalRandom.current().nextInt(8, 15 + 1);
        List<Position> selectedPositions = new ArrayList<>();
        List<Position> circlePositions = tilePosition.getCircle(3);

        Collections.shuffle(circlePositions);

        for (Position circlePos : circlePositions) {
            if (circlePos.equals(gamePlayer.getPlayer().getRoomUser().getPosition())) {
                continue;
            }

            BattleBallTile tile = (BattleBallTile) game.getTile(circlePos.getX(), circlePos.getY());

            if (tile == null || room.getMapping().getTile(circlePos) == null) {
                continue;
            }

            if (circlePos.equals(gamePlayer.getPlayer().getRoomUser().getPosition()) ||
                    circlePos.equals(gamePlayer.getPlayer().getRoomUser().getNextPosition())) {
                continue;
            }

            circlePos.setZ(tile.getPosition().getZ());

            if (selectedPositions.size() < maxPins) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    PinObject pin = new PinObject(game.createObjectId(), circlePos);
                    pins.add(pin);

                    game.getEventsQueue().add(new PinSpawnEvent(pin.getId(), pin.getPosition()));
                    selectedPositions.add(circlePos);

                    if (room.getMapping().getTile(circlePos).getEntities().size() > 0) {
                        for (Entity entity : room.getMapping().getTile(circlePos).getEntities()) {
                            if (entity.getType() != EntityType.PLAYER) {
                                continue;
                            }

                            Player player = (Player) entity;
                            GamePlayer gameUser = player.getRoomUser().getGamePlayer();

                            if (gameUser.isSpectator()) {
                                continue;
                            }

                            dizzyPlayers.add(player.getRoomUser().getGamePlayer());
                        }
                    }
                }
            }
        }

        game.getObjects().addAll(pins);

        // Make all affected players dizzy
        for (GamePlayer dizzyPlayer : dizzyPlayers) {
            PowerUpUtil.stunPlayer(game, dizzyPlayer, BattleBallPlayerState.BALL_BROKEN);
        }


        // Despawn all pins at their irregular intervals, as seen: https://www.youtube.com/watch?v=yw0MigOIloI&feature=youtu.be&t=94
        for (GameObject pinObject : pins) {
            GameScheduler.getInstance().getService().schedule(() -> {
                game.getEventsQueue().add(new DespawnObjectEvent(pinObject.getId()));
                game.getObjects().remove(pinObject);

            }, ThreadLocalRandom.current().nextInt(12, 16+1), TimeUnit.SECONDS);
        }
    }

    public static boolean checkNailTile(GamePlayer gamePlayer) {
        for (GameObject gameObject : gamePlayer.getGame().getObjects()) {
            if (!(gameObject instanceof PinObject)) {
                continue;
            }

            PinObject pinObject = (PinObject) gameObject;

            if (gamePlayer.getPlayer().getRoomUser().getPosition().equals(pinObject.getPosition())) {
                gamePlayer.getGame().getEventsQueue().add(new DespawnObjectEvent(pinObject.getId()));
                gamePlayer.getGame().getObjects().remove(gameObject);

                PowerUpUtil.stunPlayer(gamePlayer.getGame(), gamePlayer, BattleBallPlayerState.BALL_BROKEN);
                return true;
            }
        }

        return false;
    }
}

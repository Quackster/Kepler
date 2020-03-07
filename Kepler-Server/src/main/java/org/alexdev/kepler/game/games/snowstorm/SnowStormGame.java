package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.GameTile;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.snowstorm.object.SnowStormAvatarObject;
import org.alexdev.kepler.game.games.snowstorm.tasks.SnowStormUpdateTask;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.models.RoomModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowStormGame extends Game {
    private int gameLengthChoice;
    private AtomicInteger objectId;
    private List<GameObject> executingEvents;

    public SnowStormGame(int id, int mapId, String name, int teamAmount, Player gameCreator, int gameLengthChoice, boolean privateGame) {
        super(id, mapId, GameType.SNOWSTORM, name, teamAmount, gameCreator);
        this.gameLengthChoice = gameLengthChoice;
        this.objectId = new AtomicInteger(0);
        this.executingEvents = new CopyOnWriteArrayList<>();
    }

    @Override
    public void initialise() {
        var model = new RoomModel("snowwar_arena_0", "snowwar_arena_0", 0, 0, 0, 0, "00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|00000000000000000000000000000000000000000000000000|", null);
        int seconds = 0;

        if (this.gameLengthChoice == 1) {
            seconds = (int) TimeUnit.MINUTES.toSeconds(2);
        }

        if (this.gameLengthChoice == 2) {
            seconds = (int) TimeUnit.MINUTES.toSeconds(3);
        }

        if (this.gameLengthChoice == 3) {
            seconds = (int) TimeUnit.MINUTES.toSeconds(5);
        }


        super.initialise(999999, "SnowStorm Arena", model);
        //this.getTotalSecondsLeft().set(seconds); // Override with game length choice
    }

    @Override
    public void assignSpawnPoints() {
        this.getRoom().getMapping().regenerateCollisionMap();

        for (GameTeam team : this.getTeams().values()) {
            for (GamePlayer p : team.getPlayers()) {
                /*Position newPosition = this.getRoom().getMapping().getRandomWalkableBound(p.getPlayer());

                if (newPosition != null) {
                    p.getSpawnPosition().setX(newPosition.getX());
                    p.getSpawnPosition().setY(newPosition.getY());
                } else {*/
                    p.getSpawnPosition().setX(9);
                    p.getSpawnPosition().setY(22);
                //}

                if (p.getObjectId() == -1) {
                    p.setObjectId(this.createObjectId());
                }

                p.getPlayer().getRoomUser().setPosition(p.getSpawnPosition().copy());
                p.setGameObject(SnowStormGame.createAvatarObject(p));
                this.getObjects().add(p.getGameObject());

            }
        }
    }

    public static SnowStormObject createAvatarObject(GamePlayer p) {
        var avatarObject = new SnowStormAvatarObject(p);
        avatarObject.setX(p.getPlayer().getRoomUser().getPosition().getX());
        avatarObject.setY(p.getPlayer().getRoomUser().getPosition().getY());
        avatarObject.setBodyDirection(p.getPlayer().getRoomUser().getPosition().getRotation());

        avatarObject.setMoveTargetX(p.getPlayer().getRoomUser().getNextPosition() != null ? p.getPlayer().getRoomUser().getNextPosition().getX() : p.getPlayer().getRoomUser().getPosition().getX());
        avatarObject.setMoveTargetY(p.getPlayer().getRoomUser().getNextPosition() != null ? p.getPlayer().getRoomUser().getNextPosition().getY() : p.getPlayer().getRoomUser().getPosition().getY());

        avatarObject.setMoveNextX(p.getPlayer().getRoomUser().getNextPosition() != null ? p.getPlayer().getRoomUser().getNextPosition().getX() : p.getPlayer().getRoomUser().getPosition().getX());
        avatarObject.setMoveNextY(p.getPlayer().getRoomUser().getNextPosition() != null ? p.getPlayer().getRoomUser().getNextPosition().getY() : p.getPlayer().getRoomUser().getPosition().getY());
        return avatarObject;
    }

    @Override
    public void gameTick() {

    }

    @Override
    public boolean canTimerContinue() {
        return true;
    }

    @Override
    public GameTile[][] getTileMap() {
        return new GameTile[0][];
    }

    @Override
    public void buildMap() {

    }

    public int getGameLengthChoice() {
        return gameLengthChoice;
    }

    public static int convertToGameCoordinate(int num) {
        int pAccuracyFactor = 100;
        int pTileSize = 32;
        int tMultiplier = pTileSize * pAccuracyFactor;

        return num / tMultiplier;
    }

    public static int convertToWorldCoordinate(int num) {
        int pAccuracyFactor = 100;
        int pTileSize = 32;
        int tMultiplier = pTileSize * pAccuracyFactor;

        return num * tMultiplier;
    }

    public SnowStormUpdateTask getUpdateTask() {
        return (SnowStormUpdateTask) this.getRoom().getTaskManager().getTask("UpdateTask");
    }
}

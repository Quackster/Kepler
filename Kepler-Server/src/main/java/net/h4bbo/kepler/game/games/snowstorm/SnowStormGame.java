package net.h4bbo.kepler.game.games.snowstorm;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.GameTile;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.games.player.score.ScoreCalculator;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormHitEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormStunEvent;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormMap;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormMachineObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormSnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.tasks.SnowStormGameTask;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormPlayers;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormConstants;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.models.RoomModel;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SnowStormGame extends Game {
    public static final int MAX_QUICK_THROW_DISTANCE = 22;
    private int gameLengthChoice;
    private final ScoreCalculator scoreCalculator;

    public SnowStormGame(int id, int mapId, String name, int teamAmount, Player gameCreator, int gameLengthChoice, boolean privateGame) {
        super(id, mapId, GameType.SNOWSTORM, name, teamAmount, gameCreator);
        this.gameLengthChoice = gameLengthChoice;
        this.scoreCalculator = new SnowStormScoreCalculator();
    }

    @Override
    public boolean hasEnoughPlayers() {
        if (this.getTeamAmount() == 1) {
            return this.getActivePlayers().size() > 0;
        } else {
            int activeTeamCount = 0;

            for (int i = 0; i < this.getTeamAmount(); i++) {
                if (this.getTeams().get(i).getActivePlayers().size() > 0) {
                    activeTeamCount++;
                }
            }

            return activeTeamCount > 0;
        }
    }

    @Override
    public void initialise() {
        var model = new RoomModel("snowwar_arena_0", "snowwar_arena_0", 0, 0, 0, 0,
                SnowStormMapsManager.getInstance().getHeightMap(this.getMapId()), null);

        int seconds = calculateGameLengthSeconds();
        super.initialise(seconds, "SnowStorm Arena", model);

        for (var snowballItem : this.getMap().getItems()) {
            if (snowballItem.isSnowballMachine()) {
                this.getObjects().add(new SnowStormMachineObject(
                        this.createObjectId(),
                        snowballItem.getX(),
                        snowballItem.getY(),
                        0));
            }
        }
    }

    private int calculateGameLengthSeconds() {
        int configuredSeconds = GameManager.getInstance().getLifetimeSeconds(this.getGameType());
        if (configuredSeconds > 0) {
            return configuredSeconds;
        }

        return switch (this.gameLengthChoice) {
            case 2 -> (int) TimeUnit.MINUTES.toSeconds(3);
            case 3 -> (int) TimeUnit.MINUTES.toSeconds(5);
            default -> (int) TimeUnit.MINUTES.toSeconds(2);
        };
    }

    @Override
    public void gamePrepare() {
        super.gamePrepare();

        int ticketCharge = GameConfiguration.getInstance().getInteger("snowstorm.ticket.charge");

        if (ticketCharge > 0) {
            for (GamePlayer gamePlayer : this.getActivePlayers()) {
                CurrencyDao.decreaseTickets(gamePlayer.getPlayer().getDetails(), 2); // BattleBall costs 2 tickets
            }
        }
    }

    @Override
    public void finishGame() {
        for (GamePlayer p : this.getActivePlayers()) {
            var attributes = SnowStormPlayers.get(p);
            p.setScore(attributes.getScore().get());
            SnowStormPlayers.remove(p);
        }


        super.finishGame();
    }

    @Override
    public void assignSpawnPoints() {
        this.getRoom().getMapping().regenerateCollisionMap();

        for (GameTeam team : this.getTeams().values()) {
            for (GamePlayer p : team.getPlayers()) {
                p.setAssignedSpawn(false);
            }
        }

        for (GameTeam team : this.getTeams().values()) {
            for (GamePlayer p : team.getPlayers()) {
                var attributes = SnowStormPlayers.get(p);
                generateSpawn(p);
                attributes.setRotation(ThreadLocalRandom.current().nextInt(0, 7));
                attributes.setActivityState(SnowStormActivityState.ACTIVITY_STATE_NORMAL);

                attributes.setWalking(false);
                attributes.setCurrentPosition(p.getSpawnPosition().copy());
                attributes.setWalkGoal(null);
                attributes.setNextGoal(null);

                attributes.setImmunityExpiry(0);
                attributes.getScore().set(0);
                attributes.getSnowballs().set(SnowStormConstants.MAX_SNOWBALLS);
                attributes.getHealth().set(SnowStormConstants.INITIAL_HEALTH);
                attributes.getHealthToImplement().set(SnowStormConstants.INITIAL_HEALTH);
                attributes.setGoalWorldCoordinates(null);

                p.setObjectId(this.createObjectId());
                p.setScore(0);

                p.setGameObject(new SnowStormAvatarObject(p));
                this.getObjects().add(p.getGameObject());

            }
        }
    }

    private void generateSpawn(GamePlayer p) {
        if (this.getMap().getSpawnClusters().length == 0) {
            p.getSpawnPosition().setX(15);
            p.getSpawnPosition().setY(18);
            p.setAssignedSpawn(true);
            return;
        }

        try {
            SnowStormMap.SpawnCluster spawn = this.getMap().getSpawnClusters()[ThreadLocalRandom.current().nextInt(this.getMap().getSpawnClusters().length)];

            List<Position> potentialPositions = spawn.getPosition().getCircle(spawn.getRadius());
            Collections.shuffle(potentialPositions);

            Position candidate = potentialPositions.get(ThreadLocalRandom.current().nextInt(0, potentialPositions.size() - 1));

            for (GamePlayer gamePlayer : this.getActivePlayers()) {
                if (!gamePlayer.isAssignedSpawn()) {
                    continue;
                }

                int distance = gamePlayer.getSpawnPosition().getDistanceSquared(candidate);

                if (distance < spawn.getMinDistance()) {
                    generateSpawn(p);
                    return;
                }
            }

            if (this.getMap().getTile(candidate) == null || !this.getMap().getTile(candidate).isWalkable()) {
                generateSpawn(p);
                return;
            }

            p.getSpawnPosition().setX(candidate.getX());
            p.getSpawnPosition().setY(candidate.getY());
            p.setAssignedSpawn(true);
        } catch (Exception ex) {
            Log.getErrorLogger().error("Exception when assigning spawn point on map {}:", this.getMapId(), ex);

            p.getSpawnPosition().setX(15);
            p.getSpawnPosition().setY(18);
            p.setAssignedSpawn(true);
        }
    }

    public int getGameLength() {
        if (this.getGameState() == GameState.WAITING || this.getGameState() == GameState.ENDED) {
            return calculateGameLengthSeconds();
        }
        return this.getTotalSecondsLeft().get();
    }



    public boolean isOppositionPlayer(GamePlayer gamePlayer, GamePlayer player) {
        if (gamePlayer.getPlayer().getDetails().getId() == player.getPlayer().getDetails().getId()) {
            return false;
        }

        if (this.getTeamAmount() == 1) {
            return true;
        }

        return gamePlayer.getTeamId() != player.getTeamId();
    }

    public void removeSnowBall(SnowStormSnowballObject snowball){
        this.getUpdateTask().removeSnowball(snowball);
    }

    public void hitPlayer(SnowStormSnowballObject snowball, GamePlayer player){
        var lastTilePosition = new Position(snowball.getTargetX(), snowball.getTargetY());
        this.getUpdateTask().queueEvent( new SnowStormHitEvent(snowball.getThrower().getObjectId(), player.getObjectId(),
                Rotation.calculateWalkDirection(snowball.getFromX(), snowball.getFromY(), lastTilePosition.getX(), lastTilePosition.getY())));
        removeSnowBall(snowball);
    }


    public void stunPlayerHandler(SnowStormGame game, GamePlayer thrower, GamePlayer player, Position landedPosition, SnowStormSnowballObject snowball) {
        game.getUpdateTask().queueEvent( new SnowStormStunEvent(player.getObjectId(), thrower.getObjectId(),
                45 * Rotation.calculateWalkDirection(snowball.getFromX(), snowball.getFromY(), landedPosition.getX(), landedPosition.getY())));
        removeSnowBall(snowball);
    }

    public SnowStormGameTask getUpdateTask() {
        return (SnowStormGameTask) this.getRoom().getTaskManager().getTask("UpdateTask");
    }

    @Override
    public void gameTick() { }

    @Override
    public boolean canTimerContinue() { return true; }

    @Override
    public GameTile[][] getTileMap() {
        return new GameTile[0][];
    }

    @Override
    public void buildMap() { }

    @Override
    public ScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public SnowStormMap getMap() {
        return SnowStormMapsManager.getInstance().getMap(this.getMapId());
    }

    public int getGameLengthChoice() {
        return gameLengthChoice;
    }
}


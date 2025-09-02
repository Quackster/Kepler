package net.h4bbo.kepler.game.games.snowstorm;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.GameTile;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormDeleteObjectEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormHitEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormStunEvent;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormMap;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormMachineObject;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.tasks.SnowStormGameTask;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormSpawn;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.models.RoomModel;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SnowStormGame extends Game {
    public static final int MAX_QUICK_THROW_DISTANCE = 22;
    private int gameLengthChoice;
    private List<GameObject> executingEvents;
    private long gameStarted;

    public SnowStormGame(int id, int mapId, String name, int teamAmount, Player gameCreator, int gameLengthChoice, boolean privateGame) {
        super(id, mapId, GameType.SNOWSTORM, name, teamAmount, gameCreator);
        this.gameLengthChoice = gameLengthChoice;
        this.executingEvents = new CopyOnWriteArrayList<>();
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

        if (GameManager.getInstance().getLifetimeSeconds(this.getGameType()) > 0) {
            seconds = GameManager.getInstance().getLifetimeSeconds(this.getGameType());
        }

        super.initialise(seconds, "SnowStorm Arena", model);
        this.gameStarted = DateUtil.getCurrentTimeSeconds();

        for (var snowballItem : this.getMap().getItems()) {
            if (snowballItem.isSnowballMachine()) {
                this.getObjects().add(new SnowStormMachineObject(this.createObjectId(), snowballItem.getX(), snowballItem.getY(), 0));
            }
        }
        //this.getTotalSecondsLeft().set(seconds); // Override with game length choice
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
            p.setScore(p.getSnowStormAttributes().getScore().get());
        }

        for (GameTeam team : this.getTeams().values()) {
            team.calculateScore();
            // team.setScore(team.getPlayers().stream().mapToInt(GamePlayer::getScore).sum());
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
                generateSpawn(p);
                p.getSnowStormAttributes().setRotation(ThreadLocalRandom.current().nextInt(0, 7));
                //p.getPlayer().getBadgeManager().tryAddBadge("SS_BETA", null);
                p.getSnowStormAttributes().setActivityState(SnowStormActivityState.ACTIVITY_STATE_NORMAL);

                p.getSnowStormAttributes().setWalking(false);
                p.getSnowStormAttributes().setCurrentPosition(p.getSpawnPosition().copy());
                p.getSnowStormAttributes().setWalkGoal(null);
                p.getSnowStormAttributes().setNextGoal(null);

                p.getSnowStormAttributes().setImmunityExpiry(0);
                p.getSnowStormAttributes().getScore().set(0);
                p.getSnowStormAttributes().getSnowballs().set(5);
                p.getSnowStormAttributes().getHealth().set(4);
                p.getSnowStormAttributes().setGoalWorldCoordinates(null);

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
            SnowStormSpawn spawn = this.getMap().getSpawnClusters()[ThreadLocalRandom.current().nextInt(this.getMap().getSpawnClusters().length)];

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
            if (this.gameLengthChoice == 1) {
                return (int) TimeUnit.MINUTES.toSeconds(2);
            }

            if (this.gameLengthChoice == 2) {
                return (int) TimeUnit.MINUTES.toSeconds(3);
            }

            if (this.gameLengthChoice == 3) {
                return (int) TimeUnit.MINUTES.toSeconds(5);
            }
        }

        return this.getTotalSecondsLeft().get();
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

    public boolean isOppositionPlayer(GamePlayer gamePlayer, GamePlayer player) {
        if (gamePlayer.getPlayer().getDetails().getId() == player.getPlayer().getDetails().getId()) {
            return false;
        }

        if (this.getTeamAmount() == 1) {
            return true;
        }

        return gamePlayer.getTeamId() != player.getTeamId();
    }

    public void handleSnowballLanding(SnowballObject snowball, boolean deleteAfterHit) {
        var lastTilePosition = new Position(snowball.getTargetX(), snowball.getTargetY());
        var tile = this.getMap().getTile(lastTilePosition);

        if (tile == null) {
            return;
        }

        var player = this.getActivePlayers().stream().filter(p ->
                this.isOppositionPlayer(p, snowball.getThrower()) &&
                        (p.getSnowStormAttributes().getCurrentPosition().equals(lastTilePosition) ||
                        (p.getSnowStormAttributes().getNextGoal() != null && p.getSnowStormAttributes().getNextGoal().equals(lastTilePosition))) &&
                        p.getSnowStormAttributes().isDamageable())
                .findFirst().orElse(null);

        if (player != null && player.getSnowStormAttributes().getHealth().get() > 0) {
            snowball.getThrower().getSnowStormAttributes().getScore().incrementAndGet();

            this.getUpdateTask().sendQueue(0, 1, new SnowStormHitEvent(snowball.getThrower().getObjectId(), player.getObjectId(),
                    Rotation.calculateWalkDirection(snowball.getFromX(), snowball.getFromY(), lastTilePosition.getX(), lastTilePosition.getY())));

            if (deleteAfterHit) {
                this.getUpdateTask().sendQueue(0, 1, new SnowStormDeleteObjectEvent(snowball.getObjectId()));
            }

            //System.out.println("Player " + gamePlayer.getPlayer().getDetails().getName() + " hits " + player.getPlayer().getDetails().getName());

            if (player.getSnowStormAttributes().getHealth().decrementAndGet() == 0) {
                stunPlayerHandler(this, snowball.getThrower(), player, lastTilePosition, snowball);
            }
        } else {
            if (snowball.isBlocked()) {
                this.getUpdateTask().sendQueue(0, 1, new SnowStormDeleteObjectEvent(snowball.getObjectId()));
            }
        }
    }

    public static void stunPlayerHandler(SnowStormGame game, GamePlayer thrower, GamePlayer player, Position landedPosition, SnowballObject snowball) {
        game.getUpdateTask().sendQueue(0, 1, new SnowStormStunEvent(player.getObjectId(), thrower.getObjectId(),
                45 * Rotation.calculateWalkDirection(snowball.getFromX(), snowball.getFromY(), landedPosition.getX(), landedPosition.getY())));
        //System.out.println("Player " + thrower.getPlayer().getDetails().getName() + " hits " + player.getPlayer().getDetails().getName());

        thrower.getSnowStormAttributes().getScore().addAndGet(5);
        player.getSnowStormAttributes().getSnowballs().set(0);
        player.getSnowStormAttributes().getHealth().set(4);

        player.getSnowStormAttributes().setActivityState(SnowStormActivityState.ACTIVITY_STATE_STUNNED, () -> {
            player.getSnowStormAttributes().setActivityState(SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN);
            player.getSnowStormAttributes().setImmunityExpiry(System.currentTimeMillis() + SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN.getTimeInMS());
        });
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

    public SnowStormMap getMap() {
        return SnowStormMapsManager.getInstance().getMap(this.getMapId());
    }

    public int getGameLengthChoice() {
        return gameLengthChoice;
    }
}

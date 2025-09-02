package net.h4bbo.kepler.game.games.battleball;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.game.games.*;
import net.h4bbo.kepler.game.games.GameSpawn;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallColourState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPowerType;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallTileState;
import net.h4bbo.kepler.game.games.battleball.events.DespawnObjectEvent;
import net.h4bbo.kepler.game.games.battleball.events.PowerUpSpawnEvent;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerObject;
import net.h4bbo.kepler.game.games.enums.GameType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.mapping.RoomTileState;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BattleBallGame extends Game {
    private BattleBallTile[][] battleballTiles;

    private BlockingQueue<BattleBallTile> updateTilesQueue;
    private BlockingQueue<BattleBallTile> fillTilesQueue;

    private List<BattleBallTile> tiles;

    private List<Integer> allowedPowerUps;
    private List<BattleBallPowerUp> activePowers;

    private Map<GamePlayer, List<BattleBallPowerUp>> storedPowers;
    private boolean spawnedInitialPowers;

    public static final int MAX_POWERS_ACTIVE = 2;

    public BattleBallGame(int id, int mapId, GameType gameType, String name, int teamAmount, Player gameCreator, List<Integer> allowedPowerUps, boolean privateGame) {
        super(id, mapId, gameType, name, teamAmount, gameCreator);

        this.allowedPowerUps = allowedPowerUps;
        this.tiles = new ArrayList<>();

        if (this.allowedPowerUps.size() >= 2) {
            this.allowedPowerUps.add(BattleBallPowerType.QUESTION_MARK.getPowerUpId());
        }

        this.activePowers = new CopyOnWriteArrayList<>();
        this.storedPowers = new ConcurrentHashMap<>();

        this.updateTilesQueue = new LinkedBlockingQueue<>();
        this.fillTilesQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean hasEnoughPlayers() {
        int activeTeamCount = 0;

        for (int i = 0; i < this.getTeamAmount(); i++) {
            if (this.getTeams().get(i).getActivePlayers().size() > 0) {
                activeTeamCount++;
            }
        }

        return activeTeamCount > 0;
    }

    @Override
    public void startGame() {
        super.startGame();

        this.updateTilesQueue.clear();
        this.fillTilesQueue.clear();

        this.getEventsQueue().clear();
        this.getObjectsQueue().clear();
    }

    @Override
    public void gamePrepare() {
        super.gamePrepare();

        // Despawn all previous powers
        for (BattleBallPowerUp powerUp : this.activePowers) {
            this.getEventsQueue().add(new DespawnObjectEvent(powerUp.getId()));
        }

        this.spawnedInitialPowers = false;

        this.storedPowers.clear();
        this.activePowers.clear();

        int ticketCharge = GameConfiguration.getInstance().getInteger("battleball.ticket.charge");

        if (ticketCharge > 0) {
            for (GamePlayer gamePlayer : this.getActivePlayers()) {
                CurrencyDao.decreaseTickets(gamePlayer.getPlayer().getDetails(), 2); // BattleBall costs 2 tickets
            }
        }
    }

    @Override
    public void gamePrepareTick() {
        if (!this.spawnedInitialPowers) {
            if (MAX_POWERS_ACTIVE > 0) {
                int initialPowers = ThreadLocalRandom.current().nextInt(0, MAX_POWERS_ACTIVE + 1);

                for (int i = 0; i < initialPowers; i++) {
                    this.checkSpawnPower(false);
                }

                this.spawnedInitialPowers = true;
            }
        }
    }

    @Override
    public void gameStarted() {

    }

    @Override
    public void gameTick() {
        try {
            this.checkExpirePower();
            this.checkSpawnPower(true);
            this.checkStoredExpirePower();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkSpawnPower(boolean doPercentCheck) {
        if (this.allowedPowerUps.isEmpty() || (this.getMapId() == 5)) {
            return;
        }

        if (this.activePowers.size() >= MAX_POWERS_ACTIVE) { // There's already an active power so don't spawn another one
            return;
        }

        if (doPercentCheck) {
            if (!(Math.random() < 0.06)) {
                return;
            }
        }

        //int powersToSpawn = MAX_POWERS_ACTIVE - this.activePowers.size();

        //for (int i = 0; i < powersToSpawn; i++) {
            BattleBallPowerUp powerUp = new BattleBallPowerUp(this.createObjectId(), this, this.getRandomTile());
            this.getEventsQueue().add(new PowerUpSpawnEvent(powerUp));

            this.activePowers.add(powerUp);
            this.getObjects().add(powerUp.getObject());
    }


    private void checkExpirePower() {
        if (this.allowedPowerUps.isEmpty() || (this.getMapId() == 5)) {
            return;
        }

        for (BattleBallPowerUp powerUp : this.activePowers) {
            if (!expirePower(powerUp)) {
                continue;
            }

            this.activePowers.remove(powerUp);
            this.getObjects().remove(powerUp.getObject());
        }
    }

    private void checkStoredExpirePower() {
        for (var powers : this.storedPowers.values()) {
            List<BattleBallPowerUp> expiredPowers = new ArrayList<>();

            for (BattleBallPowerUp power : powers) {
                if (expirePower(power)) {
                    expiredPowers.add(power);
                }
            }

            powers.removeAll(expiredPowers);
        }
    }

    private boolean expirePower(BattleBallPowerUp powerUp) {
        if (powerUp.getTimeToDespawn().get() > 0) {
            if (powerUp.getTimeToDespawn().decrementAndGet() != 0) {
                return false;
            }
        }

        this.getEventsQueue().add(new DespawnObjectEvent(powerUp.getId()));
        return true;
    }

    @Override
    public void buildMap() {
        BattleBallMap tileMap = GameManager.getInstance().getBattleballTileMap(this.getMapId());

        this.battleballTiles = new BattleBallTile[this.getRoomModel().getMapSizeX()][this.getRoomModel().getMapSizeY()];
        this.tiles.clear();

        for (int y = 0; y < this.getRoomModel().getMapSizeY(); y++) {
            for (int x = 0; x < this.getRoomModel().getMapSizeX(); x++) {
                RoomTileState tileState = this.getRoomModel().getTileState(x, y);

                BattleBallTile tile = new BattleBallTile(new Position(x, y, this.getRoomModel().getTileHeight(x, y)));

                this.battleballTiles[x][y] = tile;
                tile.setState(BattleBallTileState.DEFAULT);

                if (tileState == RoomTileState.CLOSED) {
                    tile.setColour(BattleBallColourState.DISABLED);
                    continue;
                }

                if (!tileMap.isGameTile(x, y)) {
                    tile.setColour(BattleBallColourState.DISABLED);
                    continue;
                }

                tile.setColour(BattleBallColourState.DEFAULT);
                this.tiles.add(tile);
            }
        }
    }

    /**
     * Assign spawn points to all team members
     */
    @Override
    public void assignSpawnPoints() {
        for (GameTeam team : this.getTeams().values()) {
            List<GameSpawn> gameSpawns = GameManager.getInstance().getGameSpawns(this.getGameType(), this.getMapId(), team.getId());

            //if (gameSpawn == null) {
            //    continue;
            //}


            for (GamePlayer p : team.getPlayers()) {
                Position spawnPosition = null;

                for (GameSpawn gameSpawn : gameSpawns) {
                    if (!this.battleballTiles[gameSpawn.getX()][gameSpawn.getY()].isSpawnOccupied()) {
                        spawnPosition = gameSpawn.copy();
                        spawnPosition.setZ(this.getRoomModel().getTileHeight(gameSpawn.getX(), gameSpawn.getY()));
                        break;
                    }
                }

                if (spawnPosition == null || this.getTile(spawnPosition.getX(), spawnPosition.getY()) == null) {
                    continue;
                }

                p.setPlayerState(BattleBallPlayerState.NORMAL);
                p.setHarlequinPlayer(null);
                p.setGameObject(new PlayerObject(p));

                if (p.getObjectId() == -1) {
                    p.setObjectId(this.createObjectId());
                }

                this.getObjects().add(p.getGameObject());

                p.getSpawnPosition().setX(spawnPosition.getX());
                p.getSpawnPosition().setY(spawnPosition.getY());
                p.getSpawnPosition().setRotation(spawnPosition.getRotation());
                p.getSpawnPosition().setZ(spawnPosition.getZ());

                p.getPlayer().getRoomUser().setWalking(false);
                p.getPlayer().getRoomUser().setNextPosition(null);
                //p.getPlayer().getRoomUser().setPosition(p.getSpawnPosition().copy());

                // Don't allow anyone to spawn on this tile
                BattleBallTile tile = (BattleBallTile) this.getTile(spawnPosition.getX(), spawnPosition.getY());
                tile.setSpawnOccupied(true);

                if (tile.getColour() != BattleBallColourState.DISABLED) {
                    // Set spawn colour
                    tile.setColour(BattleBallColourState.getColourById(team.getId()));

                    if (this.getMapId() == 5) {
                        tile.setState(BattleBallTileState.TOUCHED);
                    } else {
                        tile.setState(BattleBallTileState.CLICKED);
                    }
                }
            }
        }
    }

    /**
     * Find a spawn with given coordinates.
     *
     * @param flip whether the integers should get incremented or decremented
     * @param spawnX the x coord
     * @param spawnY the y coord
     * @param spawnRotation the spawn rotation
     */
    private void findSpawn(boolean flip, AtomicInteger spawnX, AtomicInteger spawnY, AtomicInteger spawnRotation) {
        try {
            while (this.battleballTiles[spawnX.get()][spawnY.get()].isSpawnOccupied()) {
                if (spawnRotation.get() == 0) {
                    if (!flip) {
                        spawnX.decrementAndGet();// -= 1;
                    } else {
                        spawnX.incrementAndGet();// += 1;
                    }
                }

                if (spawnRotation.get() == 2) {
                    if (!flip) {
                        spawnY.incrementAndGet();// -= 1;
                    } else {
                        spawnY.decrementAndGet();// += 1;
                    }
                }

                if (spawnRotation.get() == 4) {
                    if (!flip) {
                        spawnX.incrementAndGet();// -= 1;
                    } else {
                        spawnX.decrementAndGet();// += 1;
                    }
                }

                if (spawnRotation.get() == 6) {
                    if (!flip) {
                        spawnY.decrementAndGet();// -= 1;
                    } else {
                        spawnY.incrementAndGet();// += 1;
                    }
                }
            }
            flip = (!flip);
        } catch (Exception ex) {
            flip = (!flip);
            findSpawn(flip, spawnX, spawnY, spawnRotation);
        }
    }


    /**
     * Get if the game still has free tiles to use.
     *
     * @return true, if successful
     */
    @Override
    public boolean canTimerContinue() {
        for (int y = 0; y < this.getRoomModel().getMapSizeY(); y++) {
            for (int x = 0; x < this.getRoomModel().getMapSizeX(); x++) {
                BattleBallTile tile = (BattleBallTile) this.getTile(x, y);

                if (tile == null || tile.getColour() == BattleBallColourState.DISABLED) {
                    continue;
                }

                if (tile.getState() != BattleBallTileState.SEALED) {
                    return true;
                }
            }
        }

        return false;
    }

    public BattleBallTile getRandomTile() {
        int mapSizeX = this.getRoomModel().getMapSizeX();
        int mapSizeY = this.getRoomModel().getMapSizeY();

        int x = ThreadLocalRandom.current().nextInt(0, mapSizeX);
        int y = ThreadLocalRandom.current().nextInt(0, mapSizeY);

        BattleBallTile battleballTile = (BattleBallTile) this.getTile(x, y);

        if (battleballTile == null || battleballTile.getColour() == BattleBallColourState.DISABLED) {
            return getRandomTile();
        }

        if (this.getRoom().getMapping().getTile(x, y).getEntities().size() > 0) {
            return getRandomTile();
        }

        for (BattleBallPowerUp powerUp : this.activePowers) {
            if (powerUp.getPosition().equals(new Position(x, y))) {
                return this.getRandomTile();
            }
        }

        return battleballTile;
    }

    @Override
    public GameTile[][] getTileMap() {
        return battleballTiles;
    }

    /**
     * Get the power ups allowed for this match.
     *
     * @return the power ups allowed
     */
    public List<Integer> getAllowedPowerUps() {
        return this.allowedPowerUps;
    }

    /**
     * Get the current active powers on the board
     *
     * @return the list of active powers
     */
    public List<BattleBallPowerUp> getActivePowers() {
        return activePowers;
    }

    /**
     * Get the list of powers stored by player
     *
     * @return the list of players currently held by player
     */
    public Map<GamePlayer, List<BattleBallPowerUp>> getStoredPowers() {
        return storedPowers;
    }

    /**
     * Get the queue for tiles to be updated, used by power ups
     *
     * @return the queue for updating tiles
     */
    public BlockingQueue<BattleBallTile> getUpdateTilesQueue() {
        return updateTilesQueue;
    }

    /**
     * Get the fill tiles queue, used by power ups
     *
     * @return the fill tiles queue
     */
    public BlockingQueue<BattleBallTile> getFillTilesQueue() {
        return fillTilesQueue;
    }

    /**
     * Return the list of tiles created for the game.
     *
     * @return the list of tiles
     */
    public List<BattleBallTile> getTiles() {
        return this.tiles;
    }
}

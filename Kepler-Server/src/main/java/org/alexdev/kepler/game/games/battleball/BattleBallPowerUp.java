package org.alexdev.kepler.game.games.battleball;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallColourState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallPowerType;
import org.alexdev.kepler.game.games.battleball.enums.BattleBallTileState;
import org.alexdev.kepler.game.games.battleball.events.AcquirePowerUpEvent;
import org.alexdev.kepler.game.games.battleball.objects.PowerObject;
import org.alexdev.kepler.game.games.battleball.objects.PowerUpUpdateObject;
import org.alexdev.kepler.game.games.battleball.powerups.*;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.utils.TileUtil;
import org.alexdev.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class BattleBallPowerUp {
    private final int id;
    private final PowerObject object;
    private final AtomicInteger timeToDespawn;
    private final BattleBallTile tile;
    private final Position position;
    private final BattleBallGame game;

    private GamePlayer playerHolding;
    private BattleBallPowerType powerType;

    public BattleBallPowerUp(int id, BattleBallGame game, BattleBallTile tile) {
        this.id = id;
        this.object = new PowerObject(this);
        this.tile = tile;
        this.game = game;
        this.position = this.tile.getPosition().copy();
        this.timeToDespawn = new AtomicInteger(ThreadLocalRandom.current().nextInt(20, 30 + 1));
        this.powerType = BattleBallPowerType.getById(game.getAllowedPowerUps().get(ThreadLocalRandom.current().nextInt(0, game.getAllowedPowerUps().size())));
    }

    /**
     * Called when a player decides to use the power they have collected
     *
     * @param gamePlayer the game player that uses it
     * @param position the position that the power up should be used at
     */
    public void usePower(GamePlayer gamePlayer, Position position) {
        if (this.powerType == BattleBallPowerType.BOX_OF_PINS) {
            NailBoxHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.FLASHLIGHT) {
            TorchHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.LIGHT_BLUB) {
            LightbulbHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.DRILL) {
            VacuumHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.SPRING) {
            SpringHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.HARLEQUIN) {
            HarlequinHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.CANNON) {
            CannonHandle.handle(this.game, gamePlayer, game.getRoom());
        }

        if (this.powerType == BattleBallPowerType.BOMB) {
            BombHandle.handle(this.game, gamePlayer, game.getRoom());
        }
    }

    /**
     * Get whether the user has bounced and uses a tile (used for vacuum and spring powers)
     *
     * @param tile the tile being bounced on
     * @param gamePlayer the game player bouncing on the tile
     * @param updateTiles the list of tiles to update
     * @param updateFillTiles the fill tile list to append
     *
     * @return true, if successful
     */
    public static boolean hasUsedPower(BattleBallTile tile, GamePlayer gamePlayer, List<BattleBallTile> updateTiles, List<BattleBallTile> updateFillTiles) {
        BattleBallColourState colour = tile.getColour();
        BattleBallTileState state = tile.getState();

        if (colour == BattleBallColourState.DISABLED) {
            return false;
        }

        GameTeam team = gamePlayer.getTeam();

        if (gamePlayer.getPlayerState() == BattleBallPlayerState.HIGH_JUMPS) {
            if (state == BattleBallTileState.SEALED) {
                return true;
            }


            tile.setColour(BattleBallColourState.getColourById(gamePlayer.getTeamId()));
            tile.setState(BattleBallTileState.SEALED);

            team.setSealedTileScore();
            BattleBallTile.checkFill(gamePlayer, tile, updateFillTiles);

            updateTiles.add(tile);
            return true;
        }

        if (gamePlayer.getPlayerState() == BattleBallPlayerState.CLEANING_TILES) {
            if (TileUtil.undoTileAttributes(tile, gamePlayer.getGame())) {
                updateTiles.add(tile);

            }
            return true;
        }

        return false;
    }

    /**
     * Get whether the user has walked into a power up.
     *
     * @param tile the tile being bounced on
     * @param gamePlayer the game player bouncing on the tile
     * @param objects the list of objects to update for the next loop
     * @param events the list of events to update for the next loop
     */
    public static void checkPowerUp(BattleBallTile tile, GamePlayer gamePlayer, List<GameObject> objects, List<GameEvent> events) {
        BattleBallGame game = (BattleBallGame) gamePlayer.getGame();
        BattleBallPowerUp powerUp = null;

        for (BattleBallPowerUp power : game.getActivePowers()) {
            if (power.getTile().getPosition().equals(tile.getPosition())) {
                powerUp = power;
                break;
            }
        }

        if (powerUp == null) {
            return;
        }

        if (!game.getStoredPowers().containsKey(gamePlayer)) {
            game.getStoredPowers().put(gamePlayer, new CopyOnWriteArrayList<>());
        }


        // Select random power up if it's a question mark
        if (powerUp.getPowerType() == BattleBallPowerType.QUESTION_MARK) {
            // Create a new list without the question mark
            List<Integer> powerUps = new ArrayList<>(game.getAllowedPowerUps());
            powerUps.remove(BattleBallPowerType.QUESTION_MARK.getPowerUpId());

            powerUp.setPowerType(BattleBallPowerType.getById(powerUps.get(ThreadLocalRandom.current().nextInt(0, powerUps.size()))));

        }

        game.getStoredPowers().get(gamePlayer).clear();
        game.getStoredPowers().get(gamePlayer).add(powerUp);

        game.getActivePowers().remove(powerUp);
        game.getObjects().remove(powerUp.getObject());

        powerUp.getTimeToDespawn().set(15);
        powerUp.setPlayerHolding(gamePlayer);

        events.add(new AcquirePowerUpEvent(gamePlayer, powerUp));
        objects.add(new PowerUpUpdateObject(powerUp));
    }


    /**
     * Get the game id of this power up
     * @return the game id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the power up type of this instance
     *
     * @return the power up type
     */
    public BattleBallPowerType getPowerType() {
        return powerType;
    }

    public void setPowerType(BattleBallPowerType powerType) {
        this.powerType = powerType;
    }

    /**
     * Get the current position of where this power up spawned
     *
     * @return the current position
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Get the tile were this power up spawned on
     *
     * @return the tile it spawned on
     */
    public BattleBallTile getTile() {
        return tile;
    }

    /**
     * Get the time in seconds before it despawns
     *
     * @return the time before it despawns
     */
    public AtomicInteger getTimeToDespawn() {
        return timeToDespawn;
    }

    /**
     * Set the current player holding this power up
     *
     * @param playerHolding the player holding the power up
     */
    public void setPlayerHolding(GamePlayer playerHolding) {
        this.playerHolding = playerHolding;
    }

    /**
     * Get the current player id holding this power up, -1 if none
     *
     * @return the player id holding this power up
     */
    public Integer getPlayerHolding() {
        if (this.playerHolding != null) {
            return this.playerHolding.getObjectId();
        }

        return -1;
    }

    public GameObject getObject() {
        return object;
    }
}

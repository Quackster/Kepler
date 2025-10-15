package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.entities.RoomPlayer;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.outgoing.rooms.items.MOVE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Keep track of fishing instances in a room.
 * Every player has their own instance.
 */
public class FishingInstance {

    /**
     * Variable in the client as "fishing_spot_max_distance".
     */
    private static final int FISHING_SPOT_MAX_DISTANCE = 4;

    private static final long MIN_MOVE_MS = TimeUnit.MINUTES.toMillis(3);
    private static final long MAX_MOVE_MS = TimeUnit.MINUTES.toMillis(6);
    private static final double MIN_MOVE_PROB = 0.20;
    private static final double MAX_MOVE_PROB = 1.00;

    private final FishingConfiguration fishingConfiguration;
    private final Player player;
    private final RoomPlayer roomPlayer;
    private final HashMap<Integer, Item> pools;
    private final long startTime;

    private Position lastFishPosition;
    private Position lastFishPlayerPosition;
    private long lastPoolSpawn;
    private int itemId;

    public FishingInstance(FishingConfiguration fishingConfiguration, Player player) {
        this.fishingConfiguration = fishingConfiguration;
        this.player = player;
        this.roomPlayer = player.getRoomUser();
        this.pools = new HashMap<>();
        this.startTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1);
        this.lastPoolSpawn = 0;
        this.itemId = 80000;
    }

    public void initialize() {
        this.spawnSign(this.fishingConfiguration.getFishSign());
    }

    /**
     * Called every second.
     */
    public void tick() {
        final long now = System.currentTimeMillis();

        if (this.checkMovePools(now) && now > this.startTime) {
            this.spawnOrMovePools();
        }

        if (!this.roomPlayer.containsStatus(StatusType.FISHING)) {
            return;
        }

        // Check if they moved.
        if (!this.roomPlayer.getPosition().equals(this.lastFishPlayerPosition)) {
            this.roomPlayer.removeStatus(StatusType.FISHING);
            this.roomPlayer.setNeedsUpdate(true);

            this.player.getLogger().info("Stopped fishing due to moving");
            return;
        }

        // We are fishing.
    }

    /**
     * Player clicked on a fishing pool, and the client decided
     * they were close enough to start fishing.
     */
    public void startFishing(int itemId) {
        final Item pool = this.pools.get(itemId);
        if (pool == null) {
            return;
        }

        final Position playerPos = this.roomPlayer.getPosition().copy();
        final Position spotPos = pool.getPosition().copy();

        final int distance = playerPos.getDistanceSquared(spotPos);
        if (distance > FISHING_SPOT_MAX_DISTANCE) {
            return;
        }

        player.getLogger().info("May start fishing at pool {}, distance {}", itemId, distance);

        this.roomPlayer.look(spotPos, true);
        this.roomPlayer.setStatus(StatusType.FISHING, FishingInstance.createFishingStatus(spotPos, 0));
        this.roomPlayer.setNeedsUpdate(true);

        this.lastFishPosition = pool.getPosition().copy();
        this.lastFishPlayerPosition = playerPos;
    }

    /**
     * Check if we need to move the pools.
     */
    private boolean checkMovePools(long now) {
        if (this.lastPoolSpawn == 0) {
            return true;
        }

        final long elapsed = now - this.lastPoolSpawn;

        if (elapsed < MIN_MOVE_MS) return false;
        if (elapsed >= MAX_MOVE_MS) return true;

        final double t = (double) (elapsed - MIN_MOVE_MS) / (double) (MAX_MOVE_MS - MIN_MOVE_MS);
        final double p = MIN_MOVE_PROB + (MAX_MOVE_PROB - MIN_MOVE_PROB) * (t * t);

        return ThreadLocalRandom.current().nextDouble() < p;
    }

    private void spawnSign(Position position) {
        final Item fishSign = new Item();

        fishSign.setId(this.itemId++);
        fishSign.setPosition(position);
        fishSign.getDefinition().setSprite("fish_sign");
        fishSign.getDefinition().setLength(1);
        fishSign.getDefinition().setWidth(1);
        fishSign.getDefinition().setColour("0,0,0");

        this.player.send(new PLACE_FLOORITEM(fishSign));
    }

    /**
     * Spawns or respawns fishing pools in the room.
     */
    private void spawnOrMovePools() {
        final List<Position> targets = this.fishingConfiguration.getRandomFishAreas();
        final List<Integer> poolIds = new ArrayList<>(this.pools.keySet());

        for (int i = 0; i < targets.size(); i++) {
            if (i < poolIds.size()) {
                // Move existing pool.
                final int poolId = poolIds.get(i);
                final Item pool = this.pools.get(poolId);

                pool.setPosition(targets.get(i));

                this.player.sendQueued(new MOVE_FLOORITEM(pool));
            } else {
                // Create new pool.
                final Item pool = new Item();

                pool.setId(this.itemId++);
                pool.setPosition(targets.get(i));
                pool.getDefinition().setSprite("fish_area");
                pool.getDefinition().setLength(1);
                pool.getDefinition().setWidth(1);
                pool.getDefinition().setColour("0,0,0");

                this.pools.put(pool.getId(), pool);
                this.player.sendQueued(new PLACE_FLOORITEM(pool));
            }
        }

        this.player.flush();

        this.lastPoolSpawn = System.currentTimeMillis();
    }

    private static String createFishingStatus(Position position, int action) {
        final int x = position.getX();
        final int y = position.getY();
        final int h = (int) (Math.ceil(position.getZ()));

        return "%d,%d,%d,%d".formatted(x, y, h, action);
    }
}

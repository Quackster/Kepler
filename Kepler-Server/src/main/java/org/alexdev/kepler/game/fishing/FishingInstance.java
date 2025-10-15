package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.items.MOVE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Keep track of fishing instances in a room.
 * Every player has their own instance.
 */
public class FishingInstance {

    private static final long MIN_MOVE_MS = TimeUnit.MINUTES.toMillis(3);
    private static final long MAX_MOVE_MS = TimeUnit.MINUTES.toMillis(6);
    private static final double MIN_MOVE_PROB = 0.20;
    private static final double MAX_MOVE_PROB = 1.00;

    private final FishingConfiguration fishingConfiguration;
    private final Player player;
    private final List<Item> pools;
    private final long startTime;

    private long lastPoolSpawn;
    private int itemId;

    public FishingInstance(FishingConfiguration fishingConfiguration, Player player) {
        this.fishingConfiguration = fishingConfiguration;
        this.player = player;
        this.pools = new ArrayList<>();
        this.startTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
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
        long now = System.currentTimeMillis();

        if (this.checkMovePools(now) && now > this.startTime) {
            this.spawnOrMovePools();
        }
    }

    /**
     * Check if we need to move the pools.
     */
    private boolean checkMovePools(long now) {
        if (this.lastPoolSpawn == 0) {
            return true;
        }

        long elapsed = now - this.lastPoolSpawn;

        if (elapsed < MIN_MOVE_MS) return false;
        if (elapsed >= MAX_MOVE_MS) return true;

        double t = (double) (elapsed - MIN_MOVE_MS) / (double) (MAX_MOVE_MS - MIN_MOVE_MS);
        double p = MIN_MOVE_PROB + (MAX_MOVE_PROB - MIN_MOVE_PROB) * (t * t);

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
        List<Position> targets = this.fishingConfiguration.getRandomFishAreas();

        for (int i = 0; i < targets.size(); i++) {
            if (i < this.pools.size()) {
                // Move existing pool.
                Item pool = this.pools.get(i);

                pool.setPosition(targets.get(i));

                this.player.sendQueued(new MOVE_FLOORITEM(pool));
            } else {
                // Create new pool.
                Item pool = new Item();

                pool.setId(this.itemId++);
                pool.setPosition(targets.get(i));
                pool.getDefinition().setSprite("fish_area");
                pool.getDefinition().setLength(1);
                pool.getDefinition().setWidth(1);
                pool.getDefinition().setColour("0,0,0");

                this.pools.add(pool);
                this.player.sendQueued(new PLACE_FLOORITEM(pool));
            }
        }

        this.player.flush();

        this.lastPoolSpawn = System.currentTimeMillis();
    }
}

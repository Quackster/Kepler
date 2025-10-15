package org.alexdev.kepler.game.fishing;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.components.RoomComponent;

import java.util.HashMap;

public class FishingRoomHandler implements RoomComponent, Runnable {

    private final FishingConfiguration config;
    private final Room room;
    private final HashMap<Player, FishingInstance> instances;

    public FishingRoomHandler(FishingConfiguration config, Room room) {
        this.config = config;
        this.room = room;
        this.instances = new HashMap<>();
    }

    @Override
    public void run() {
        // Ticks every second.
        for (FishingInstance instance : this.instances.values()) {
            instance.tick();
        }
    }

    @Override
    public void onPlayerAdded(Player player) {
        this.instances.computeIfAbsent(player, k -> {
            final FishingInstance instance = new FishingInstance(this.config, player);

            instance.initialize();

            return instance;
        });
    }

    @Override
    public void onPlayerRemoved(Player player) {
        this.instances.remove(player);
    }
}

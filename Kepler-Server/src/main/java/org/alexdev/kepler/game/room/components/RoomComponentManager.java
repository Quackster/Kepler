package org.alexdev.kepler.game.room.components;

import org.alexdev.kepler.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomComponentManager implements RoomComponent {
    private final List<RoomComponent> components;

    public RoomComponentManager() {
        this.components = new ArrayList<>();
    }

    public void add(RoomComponent component) {
        this.components.add(component);
    }

    public void clear() {
        this.components.clear();
    }

    public List<RoomComponent> getComponents() {
        return components;
    }

    @Override
    public void onPlayerAdded(Player player) {
        for (RoomComponent component : components) {
            component.onPlayerAdded(player);
        }
    }

    @Override
    public void onPlayerRemoved(Player player) {
        for (RoomComponent component : components) {
            component.onPlayerRemoved(player);
        }
    }
}

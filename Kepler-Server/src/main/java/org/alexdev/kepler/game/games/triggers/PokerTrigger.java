package org.alexdev.kepler.game.games.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.games.gamehalls.GamePoker;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class PokerTrigger extends GameTrigger {
    public PokerTrigger(int roomId) {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GamePoker(roomId, kvp));
        }
    }

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition, Object... customArgs) {
        super.onEntityStep(entity, roomEntity, item, oldPosition, customArgs);
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        super.onEntityStop(entity, roomEntity, item, customArgs);
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        super.onEntityLeave(entity, roomEntity, item, customArgs);
    }

    /**
     * Gets the list of seats as a group for when people play a game to gether.
     *
     * @return the map of chair groups
     */
    @Override
    public List<List<int[]>> getChairGroups() {
        return new ArrayList<>() {{
            add(new ArrayList<>() {{
                add(new int[]{2, 14});
                add(new int[]{2, 16});
                add(new int[]{3, 15});
                add(new int[]{1, 15});
            }});

            add(new ArrayList<>() {{
                add(new int[]{8, 2});
                add(new int[]{8, 4});
                add(new int[]{9, 3});
                add(new int[]{7, 3});
            }});

            add(new ArrayList<>() {{
                add(new int[]{14, 2});
                add(new int[]{14, 4});
                add(new int[]{15, 3});
                add(new int[]{13, 3});
            }});

            add(new ArrayList<>() {{
                add(new int[]{2, 8});
                add(new int[]{2, 10});
                add(new int[]{3, 9});
                add(new int[]{1, 9});
            }});

            add(new ArrayList<>() {{
                add(new int[]{8, 8});
                add(new int[]{8, 10});
                add(new int[]{9, 9});
                add(new int[]{7, 9});
            }});

            add(new ArrayList<>() {{
                add(new int[]{14, 8});
                add(new int[]{14, 10});
                add(new int[]{15, 9});
                add(new int[]{13, 9});
            }});

            add(new ArrayList<>() {{
                add(new int[]{8, 14});
                add(new int[]{8, 16});
                add(new int[]{9, 15});
                add(new int[]{7, 15});
            }});

            add(new ArrayList<>() {{
                add(new int[]{14, 14});
                add(new int[]{14, 16});
                add(new int[]{15, 15});
                add(new int[]{13, 15});
            }});
        }};
    }
}

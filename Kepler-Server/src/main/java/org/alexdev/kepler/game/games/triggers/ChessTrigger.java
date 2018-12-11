package org.alexdev.kepler.game.games.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.games.gamehalls.GameChess;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class ChessTrigger extends GameTrigger {
    public ChessTrigger(int roomId) {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GameChess(roomId, kvp));
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
     * Gets the list of seats and their pairs as coordinates
     *
     * @return the map of chair pairs
     */
    @Override
    public List<List<int[]>> getChairGroups() {
        return new ArrayList<>() {{
            add(new ArrayList<>() {{
                add(new int[]{2, 7});
                add(new int[]{2, 9});
            }});

            add(new ArrayList<>() {{
                add(new int[]{6, 14});
                add(new int[]{4, 14});
            }});

            add(new ArrayList<>() {{
                add(new int[]{12, 14});
                add(new int[]{12, 12});
            }});

            add(new ArrayList<>() {{
                add(new int[]{13, 7});
                add(new int[]{13, 5});
            }});

            add(new ArrayList<>() {{
                add(new int[]{7, 3});
                add(new int[]{9, 3});
            }});
        }};
    }
}

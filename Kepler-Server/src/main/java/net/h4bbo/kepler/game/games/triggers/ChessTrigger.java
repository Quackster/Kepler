package net.h4bbo.kepler.game.games.triggers;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.games.gamehalls.GameChess;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class ChessTrigger extends GameTrigger {
    public ChessTrigger() {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GameChess(kvp));
        }
    }

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) {
        super.onEntityStep(entity, roomEntity, item, oldPosition);
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        super.onEntityStop(entity, roomEntity, item, isRotation);
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) {
        super.onEntityLeave(entity, roomEntity, item);
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

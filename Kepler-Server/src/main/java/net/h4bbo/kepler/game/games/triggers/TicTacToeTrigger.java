package net.h4bbo.kepler.game.games.triggers;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.games.gamehalls.GameTicTacToe;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeTrigger extends GameTrigger {
    public TicTacToeTrigger() {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GameTicTacToe(kvp));
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
                add(new int[]{15, 4});
                add(new int[]{15, 5});
            }});

            add(new ArrayList<>() {{
                add(new int[]{15, 9});
                add(new int[]{15, 10});
            }});

            add(new ArrayList<>() {{
                add(new int[]{15, 14});
                add(new int[]{15, 15});
            }});

            add(new ArrayList<>() {{
                add(new int[]{10, 4});
                add(new int[]{10, 5});
            }});

            add(new ArrayList<>() {{
                add(new int[]{10, 9});
                add(new int[]{10, 10});
            }});

            add(new ArrayList<>() {{
                add(new int[]{10, 14});
                add(new int[]{10, 15});
            }});


            add(new ArrayList<>() {{
                add(new int[]{5, 4});
                add(new int[]{5, 5});
            }});

            add(new ArrayList<>() {{
                add(new int[]{5, 9});
                add(new int[]{5, 10});
            }});

            add(new ArrayList<>() {{
                add(new int[]{5, 14});
                add(new int[]{5, 15});
            }});
        }};
    }
}

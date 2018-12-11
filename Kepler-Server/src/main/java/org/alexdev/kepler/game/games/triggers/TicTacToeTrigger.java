package org.alexdev.kepler.game.games.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.games.gamehalls.GameTicTacToe;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeTrigger extends GameTrigger {
    public TicTacToeTrigger(int roomId) {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GameTicTacToe(roomId, kvp));
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

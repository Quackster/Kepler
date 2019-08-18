package org.alexdev.kepler.game.games.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.games.gamehalls.GameBattleShip;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.entities.RoomEntity;

import java.util.ArrayList;
import java.util.List;

public class BattleShipsTrigger extends GameTrigger {
    public BattleShipsTrigger() {
        for (var kvp : this.getChairGroups()) {
            this.getGameInstances().add(new GameBattleShip(kvp));
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
     * Gets the list of seats as a group for when people play a game to gether.
     *
     * @return the map of chair groups
     */
    @Override
    public List<List<int[]>> getChairGroups() {
        return new ArrayList<>() {{
            add(new ArrayList<>() {{
                add(new int[]{15, 3});
                add(new int[]{13, 3});
            }});

            add(new ArrayList<>() {{
                add(new int[]{8, 3});
                add(new int[]{6, 3});
            }});

            add(new ArrayList<>() {{
                add(new int[]{2, 4});
                add(new int[]{2, 6});
            }});

            add(new ArrayList<>() {{
                add(new int[]{2, 10});
                add(new int[]{2, 12});
            }});

            add(new ArrayList<>() {{
                add(new int[]{2, 16});
                add(new int[]{2, 18});
            }});
        }};
    }
}

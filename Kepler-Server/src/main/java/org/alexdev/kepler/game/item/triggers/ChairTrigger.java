package org.alexdev.kepler.game.item.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.util.StringUtil;

public class ChairTrigger extends GenericTrigger {

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        boolean isRolling = false;

        if (customArgs.length > 0) {
            isRolling = (boolean)customArgs[0];
        }

        int headRotation = roomEntity.getPosition().getHeadRotation();

        roomEntity.getPosition().setRotation(item.getPosition().getRotation());
        roomEntity.removeStatus(StatusType.DANCE);
        roomEntity.setStatus(StatusType.SIT, StringUtil.format(item.getDefinition().getTopHeight()));
        roomEntity.setNeedsUpdate(true);

        if (isRolling) {
            if (roomEntity.getTimerManager().getLookTimer() > -1) {
                roomEntity.getPosition().setHeadRotation(headRotation);
            }
        }
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {

    }
}

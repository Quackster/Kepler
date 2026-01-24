package net.h4bbo.kepler.game.item.interactors.types;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.util.StringUtil;

public class ChairInteractor extends GenericTrigger {
    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        boolean isRolling = entity.getRoomUser().isRolling();

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
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) {

    }
}

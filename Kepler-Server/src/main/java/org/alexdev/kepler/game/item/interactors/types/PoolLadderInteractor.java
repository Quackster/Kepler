package org.alexdev.kepler.game.item.interactors.types;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public class PoolLadderInteractor extends GenericTrigger {
    /*@Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) {
        this.onEntityStep(entity, roomEntity, item, oldPosition);

    }*/

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        if (item.getTeleportTo() == null) {
            return;
        }

        if (item.getDefinition().getSprite().equals("poolEnter")) {
            if (roomEntity.containsStatus(StatusType.SWIM)) {
                return;
            }

            roomEntity.setStatus(StatusType.SWIM, "");
        }

        if (item.getDefinition().getSprite().equals("poolExit")) {
            if (!roomEntity.containsStatus(StatusType.SWIM)) {
                return;
            }

            roomEntity.removeStatus(StatusType.SWIM);
        }

        roomEntity.stopWalking();

        //roomEntity.setWalkingAllowed(false);

        roomEntity.warp(item.getTeleportTo(), true, false);

        if (item.getSwimTo() != null) {
            roomEntity.setEnableWalkingOnStop(true);
            roomEntity.walkTo(item.getSwimTo().getX(), item.getSwimTo().getY());
        }

        item.showProgram(null);

       /* if (item.getRoom().getModel().equals("md_a")) {
            item.showProgram(null);
        } else {
         */   // handle_pool_stair_splash
            /*roomEntity.getRoom().send(new MessageComposer() {
                @Override
                public void compose(NettyResponse response) {
                    response.writeInt(0);
                }

                @Override
                public short getHeader() {
                    return 505;
                }
            });*/
       //}

        // Don't handle step event from RoomUser when changing paths
        //if (customArgs.length > 0) {
        //    return;
        //}

        /*

        Position warp = null;
        Position goal = null;

        if (item.getPosition().getX() == 20 && item.getPosition().getY() == 28) {
            warp = new Position(21, 28);
            goal = new Position(22, 28);
        }

        if (item.getPosition().getX() == 17 && item.getPosition().getY() == 21) {
            warp = new Position(16, 22);
            goal = new Position(16, 23);
        }

        if (item.getPosition().getX() == 31 && item.getPosition().getY() == 10) {
            warp = new Position(30, 11);
            goal = new Position(30, 12);
        }

        if ((item.getPosition().getX() == 11 && item.getPosition().getY() == 11) ||
                item.getPosition().getX() == 11 && item.getPosition().getY() == 10) {
            warp = new Position(12, 11);
            goal = new Position(13, 12);
        }
*/

        //if (warp != null) {
        //PoolHandler.warpSwim(item, entity, warp, goal, false);
        //}
    }
}

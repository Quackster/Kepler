package org.alexdev.kepler.game.item.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.room.tasks.StatusTask;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.messages.outgoing.user.currencies.NO_TICKETS;

public class PoolQueueTrigger extends GenericTrigger {

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (player.getDetails().getTickets() == 0 || player.getDetails().getPoolFigure().isEmpty()) {
            oldPosition.setRotation(2); // Make user face this way, like the original Lido behaviour
            player.getRoomUser().stopWalking();
            player.getRoomUser().warp(oldPosition, false);

            if (player.getDetails().getTickets() == 0) {
                player.send(new NO_TICKETS());
            }
        }
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (player.getDetails().getTickets() == 0 || player.getDetails().getPoolFigure().isEmpty()) {
            return;
        }

        // When they stop walking, check if the player is on a pool lido queue and walk to the next one
        StatusTask.processPoolQueue(player);
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item, Object... customArgs) {

    }
}

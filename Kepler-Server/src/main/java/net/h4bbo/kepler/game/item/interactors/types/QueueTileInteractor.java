package net.h4bbo.kepler.game.item.interactors.types;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.room.tasks.StatusTask;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.messages.outgoing.user.currencies.NO_TICKETS;

public class QueueTileInteractor extends GenericTrigger {

    @Override
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (!roomEntity.getRoom().getData().getModel().equals("pool_b")) {
            return;
        }
        if (player.getDetails().getTickets() == 0 || player.getDetails().getPoolFigure().isEmpty()) {
            oldPosition.setRotation(2); // Make user face this way, like the original Lido behaviour
            player.getRoomUser().stopWalking();
            player.getRoomUser().warp(oldPosition, false, false);

            if (player.getDetails().getTickets() == 0) {
                player.send(new NO_TICKETS());
            }
        }
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (roomEntity.getRoom().getData().getModel().equals("pool_b")) {
            if (player.getDetails().getTickets() == 0 || player.getDetails().getPoolFigure().isEmpty()) {
                return;
            }
        }

        // When they stop walking, check if the player is on a pool lido queue and walk to the next one
        StatusTask.processPoolQueue(player);
    }
}


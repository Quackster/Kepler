package net.h4bbo.kepler.game.item.interactors.types.wobblesquabble;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.games.wobblesquabble.WobbleSquabbleManager;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.interactors.InteractionType;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.room.mapping.RoomTile;
import net.h4bbo.kepler.game.triggers.GenericTrigger;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;

public class WobbleSquabbleJoinQueue extends GenericTrigger {
    public void onEntityStep(Entity entity, RoomEntity roomEntity, Item item, Position oldPosition) {

    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        if (roomEntity.getRoom().getTaskManager().hasTask(WobbleSquabbleManager.getInstance().getName())) {
            return;
        }

        Player player = (Player) entity;

        if (player.getDetails().getTickets() < WobbleSquabbleManager.WS_GAME_TICKET_COST) {
            player.send(new ALERT("You need at least " + WobbleSquabbleManager.WS_GAME_TICKET_COST + " ticket(s) to play Wobble Squabble!"));
            return; // Too poor!
        }

        String[] teleportPositionData = item.getCurrentProgram().split(",");

        Position teleportPosition = new Position(
                Integer.valueOf(teleportPositionData[0]),
                Integer.valueOf(teleportPositionData[1])
        );

        teleportPosition.setRotation(Integer.valueOf(teleportPositionData[2]));
        RoomTile roomTile = roomEntity.getRoom().getMapping().getTile(teleportPosition);

        if (roomTile == null || roomTile.getEntities().size() > 0) {
            return;
        }

        roomEntity.removeStatus(StatusType.SWIM);
        roomEntity.warp(teleportPosition, true, false);

        InteractionType.WS_QUEUE_TILE.getTrigger().onEntityStop(entity, roomEntity, item, isRotation);
    }
}

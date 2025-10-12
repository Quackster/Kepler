package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.items.MOVE_FLOORITEM;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MOVESTUFF implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        final int itemId = reader.readInt();
        final int x = reader.readInt();
        final int y = reader.readInt();
        final int rotation = reader.readInt();

        Item item = room.getItemManager().getById(itemId);

        if (item == null) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            return;
        }

        Position oldPosition = item.getPosition().copy();

        boolean isRotation = false;

        if (item.getPosition().equals(new Position(x, y)) && item.getPosition().getRotation() != rotation) {
            isRotation = true;
        }

        if (isRotation) {
            if (item.getRollingData() != null) {
                return; // Don't allow rotating when rolling.
            }
        }

        if ((oldPosition.getX() == x &&
                oldPosition.getY() == y &&
                oldPosition.getRotation() == rotation) || !item.isValidMove(item, room, player, x, y, rotation)) {
            // Send item update even though we cancelled, otherwise the client will be confused.
            player.send(new MOVE_FLOORITEM(item));
            return;
        }


        room.getMapping().moveItem(player, item, new Position(x, y, item.getPosition().getZ(), rotation, rotation), oldPosition);
    }
}

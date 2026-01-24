package net.h4bbo.kepler.messages.incoming.rooms.items;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.items.MOVE_FLOORITEM;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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

        String content = reader.contents();
        String[] data = content.split(" ");

        int itemId = Integer.parseInt(data[0]);
        Item item = room.getItemManager().getById(itemId);

        if (item == null) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            return;
        }

        int x = (int) Double.parseDouble(data[1]);
        int y = (int) Double.parseDouble(data[2]);
        int rotation = (int) Double.parseDouble(data[3]);

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

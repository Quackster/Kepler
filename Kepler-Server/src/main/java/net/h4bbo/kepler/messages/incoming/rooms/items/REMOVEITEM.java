package net.h4bbo.kepler.messages.incoming.rooms.items;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class REMOVEITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        int itemId = Integer.parseInt(reader.contents());
        Item item = room.getItemManager().getById(itemId);

        if (item == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) &&
                !(item.hasBehaviour(ItemBehaviour.PHOTO) && !player.hasFuse(Fuseright.REMOVE_PHOTOS)) &&
                !(item.hasBehaviour(ItemBehaviour.POST_IT) && !player.hasFuse(Fuseright.REMOVE_STICKIES))) {
            return;
        }


        // Set up trigger for leaving a current item
        if (player.getRoomUser().getCurrentItem() != null) {
            if (player.getRoomUser().getCurrentItem().getDefinition().getInteractionType().getTrigger() != null) {
                player.getRoomUser().getCurrentItem().getDefinition().getInteractionType().getTrigger().onEntityLeave(player, player.getRoomUser(), player.getRoomUser().getCurrentItem());
            }
        }

        room.getMapping().pickupItem(player, item);
        item.delete();
    }
}

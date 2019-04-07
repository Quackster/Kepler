package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ADDSTRIPITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        String content = reader.contents();
        String[] data = content.split(" ");

        int itemId = Integer.parseInt(data[2]);

        Item item = room.getItemManager().getById(itemId);

        if (item == null) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.POST_IT)) {
            return; // The client does not allow picking up post-it's, thus neither will the server
        }

        item.setOwnerId(player.getDetails().getId());
        room.getMapping().removeItem(item);

        item.getDefinition().getInteractionType().getTrigger().onItemPickup(player, room, item);

        player.getInventory().addItem(item);
        player.getInventory().getView("new");
    }
}

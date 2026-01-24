package net.h4bbo.kepler.messages.incoming.jukebox;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.jukebox.USER_SONG_DISKS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.HashMap;
import java.util.Map;

public class GET_USER_SONG_DISCS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        Map<Item, Integer> userDisks = new HashMap<>();

        for (Item item : player.getInventory().getItems()) {
            if (item.isHidden()) {
                continue;
            }

            if (item.hasBehaviour(ItemBehaviour.SONG_DISK)) {
                userDisks.put(item, item.getId());
            }
        }

        player.send(new USER_SONG_DISKS(userDisks));
    }
}

package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.jukebox.USER_SONG_DISKS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuse.ANY_ROOM_CONTROLLER)) {
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

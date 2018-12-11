package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.USER_SOUND_PACKAGES;
import org.alexdev.kepler.messages.outgoing.songs.SOUND_PACKAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.Map;

public class EJECT_SOUND_PACKAGE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        // We don't want a user to get kicked when making cool beats
        player.getRoomUser().getTimerManager().resetRoomTimer();

        int slotId = reader.readInt();
        Map<Integer, Integer> tracks = SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId());

        if (tracks.containsKey(slotId)) {
            int songSoundId = tracks.get(slotId);

            ItemDefinition track = ItemManager.getInstance().getDefinitionBySprite("sound_set_" + songSoundId);

            Item item = new Item();
            item.setOwnerId(player.getDetails().getId());
            item.setDefinitionId(track.getId());

            ItemDao.newItem(item);

            player.getInventory().addItem(item);
            player.getInventory().getView("new");
        }

        SongMachineDao.removeTrack(room.getItemManager().getSoundMachine().getId(), slotId);

        player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));
        player.send(new USER_SOUND_PACKAGES(player.getInventory().getSoundsets()));

        // TODO: resend song with removed sounds from soundset (check first if client maybe requests from other packet)
    }
}

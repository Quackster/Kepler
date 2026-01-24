package net.h4bbo.kepler.messages.incoming.jukebox;

import net.h4bbo.kepler.dao.mysql.JukeboxDao;
import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.kepler.game.song.jukebox.JukeboxManager;
import net.h4bbo.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class ADD_JUKEBOX_DISC implements MessageEvent {
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

        int itemId = reader.readInt();
        int slotId = reader.readInt();

        Item songDisk = null;

        for (Item item : player.getInventory().getItems()) {
            if (item.getId() == itemId && !item.isHidden()) {
                songDisk = item;
                break;
            }
        }

        if (songDisk == null) {
            return;
        }

        songDisk.setHidden(true);
        songDisk.save();

        player.getInventory().getView("new"); // Refresh hand

        int songId = JukeboxDao.getSongIdByItem(songDisk.getId());
        Song song = SongMachineDao.getSong(songId);

        if (song == null) {
            return;
        }

        if (slotId < 1 || slotId > 10) {
            return;
        }

        JukeboxDao.editDisk(songDisk.getId(), room.getItemManager().getSoundMachine().getId(), slotId);

        room.send(new JUKEBOX_DISCS(JukeboxManager.getInstance().getDisks(room.getItemManager().getSoundMachine().getId())));
        new GET_USER_SONG_DISCS().handle(player, null);
    }
}

package net.h4bbo.kepler.messages.incoming.jukebox;

import net.h4bbo.kepler.dao.mysql.JukeboxDao;
import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.song.jukebox.BurnedDisk;
import net.h4bbo.kepler.game.song.jukebox.JukeboxManager;
import net.h4bbo.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import net.h4bbo.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class REMOVE_JUKEBOX_DISC implements MessageEvent {
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

        int slotId = reader.readInt();
        BurnedDisk burnedDisk = JukeboxDao.getDisk(room.getItemManager().getSoundMachine().getId(), slotId);

        if (burnedDisk == null) {
            return;
        }

        Item songDisk = null;

        for (Item item : player.getInventory().getItems()) {
            if ((burnedDisk.getItemId() == item.getId()) && item.isHidden()) {
                songDisk = item;
                break;
            }
        }

        if (songDisk == null) {
            return;
        }

        songDisk.setHidden(false);
        player.getInventory().addItem(songDisk); // Re-add at start.
        songDisk.save();

        SongMachineDao.removePlaylistSong(burnedDisk.getSongId(), room.getItemManager().getSoundMachine().getId());
        JukeboxDao.editDisk(songDisk.getId(), 0, 0);

        player.getInventory().getView("new"); // Refresh hand
        new GET_USER_SONG_DISCS().handle(player, null);

        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
        room.send(new JUKEBOX_DISCS(JukeboxManager.getInstance().getDisks(room.getItemManager().getSoundMachine().getId())));
    }
}

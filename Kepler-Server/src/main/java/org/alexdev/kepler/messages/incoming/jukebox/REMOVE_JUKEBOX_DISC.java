package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.JukeboxDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.song.BurnedDisk;
import org.alexdev.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import org.alexdev.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

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
        songDisk.save();

        player.getInventory().getView("new"); // Refresh hand
        SongMachineDao.removeTrack(room.getItemManager().getSoundMachine().getId(), slotId);

        new GET_USER_SONG_DISCS().handle(player, null);

        SongMachineDao.removePlaylistSong(burnedDisk.getSongId());

        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
        room.send(new JUKEBOX_DISCS(ItemManager.getInstance().getJukeboxTracks(room.getItemManager().getSoundMachine().getId())));
    }
}

package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.JukeboxDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
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

        var tracks = SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId());

        if (!tracks.containsKey(slotId)) {
            return;
        }


        int songId = tracks.get(slotId);

        List<Integer> itemList = JukeboxDao.getItemsBySong(songId);
        Item songDisk = null;

        for (Item item : player.getInventory().getItems()) {
            if (itemList.contains(item.getId()) && item.isHidden()) {
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

        SongMachineDao.removePlaylistSong(songId);
        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
    }
}

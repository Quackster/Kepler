package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.song.SongPlaylist;
import org.alexdev.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class JUKEBOX_PLAYLIST_ADD implements MessageEvent {
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

        int songId = reader.readInt();
        int newSlotId = 1;

        SongMachineDao.removePlaylistSong(songId);
        var playList = SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId());

        for (SongPlaylist playlist : playList) {
            newSlotId++;
        }

        SongMachineDao.addPlaylist(room.getItemManager().getSoundMachine().getId(), songId, newSlotId);
        playList.add(new SongPlaylist(room.getItemManager().getSoundMachine().getId(), SongMachineDao.getSong(songId), newSlotId));

        room.send(new SONG_PLAYLIST(playList));
    }
}

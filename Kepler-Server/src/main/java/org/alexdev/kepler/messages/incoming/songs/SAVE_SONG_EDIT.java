package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.messages.outgoing.songs.SONG_LIST;
import org.alexdev.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import org.alexdev.kepler.messages.outgoing.songs.SONG_UPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

import java.util.List;

import static org.alexdev.kepler.messages.incoming.songs.SAVE_SONG_NEW.calculateSongLength;

public class SAVE_SONG_EDIT implements MessageEvent {
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

        int songId = reader.readInt();
        String title = StringUtil.filterInput(reader.readString(), true);
        String data = StringUtil.filterInput(reader.readString(), true);

        SongMachineDao.saveSong(songId, title, calculateSongLength(data), data);

        player.send(new SONG_UPDATE());
        player.send(new SONG_LIST(SongMachineDao.getSongList(room.getItemManager().getSoundMachine().getId())));
        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
    }
}

package net.h4bbo.kepler.messages.incoming.songs;

import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.songs.SONG_LIST;
import net.h4bbo.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import net.h4bbo.kepler.messages.outgoing.songs.SONG_UPDATE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.StringUtil;

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
        var song = SongMachineDao.getSong(songId);

        if (song == null) {
            return;
        }

        if (song.getUserId() != player.getDetails().getId()) {
            return;
        }

        String title = StringUtil.filterInput(reader.readString(), true);
        String data = StringUtil.filterInput(reader.readString(), true);

        SongMachineDao.saveSong(songId, title, SAVE_SONG.calculateSongLength(data), data);

        player.send(new SONG_UPDATE());
        player.send(new SONG_LIST(SongMachineDao.getSongList(room.getItemManager().getSoundMachine().getId())));
        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
    }
}

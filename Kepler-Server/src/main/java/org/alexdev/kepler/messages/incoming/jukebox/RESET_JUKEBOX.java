package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class RESET_JUKEBOX implements MessageEvent {
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

        SongMachineDao.clearPlaylist(room.getItemManager().getSoundMachine().getId());
        room.send(new SONG_PLAYLIST(List.of()));
    }
}

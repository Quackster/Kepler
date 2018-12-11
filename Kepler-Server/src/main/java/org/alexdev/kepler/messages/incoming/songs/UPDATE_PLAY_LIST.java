package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.SONG_PLAYLIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class UPDATE_PLAY_LIST implements MessageEvent {
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

        int amount = reader.readInt();

        if (amount >= 6) {
            return;
        }

        // We don't want a user to get kicked when making cool beats
        player.getRoomUser().getTimerManager().resetRoomTimer();

        SongMachineDao.clearPlaylist(room.getItemManager().getSoundMachine().getId());

        for (int i = 0; i < amount; i++) {
            int songId = reader.readInt();
            SongMachineDao.addPlaylist(room.getItemManager().getSoundMachine().getId(), songId, i);
        }

        room.send(new SONG_PLAYLIST(SongMachineDao.getSongPlaylist(room.getItemManager().getSoundMachine().getId())));
    }
}

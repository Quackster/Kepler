package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class GET_JUKEBOX_DISCS implements MessageEvent {
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
            //return;
        }

        List<Song> savedTracks = new ArrayList<>();

        for (var kvp : SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId()).entrySet()) {
            int slotId = kvp.getKey();
            int songId = kvp.getValue();

            Song song = SongMachineDao.getSong(songId);
            song.setSlotId(slotId);

            savedTracks.add(song);
        }

        player.send(new JUKEBOX_DISCS(savedTracks));
    }
}

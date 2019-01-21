package org.alexdev.kepler.messages.incoming.jukebox;

import org.alexdev.kepler.dao.mysql.JukeboxDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.messages.outgoing.jukebox.JUKEBOX_DISCS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

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

        SongMachineDao.addTrack(room.getItemManager().getSoundMachine().getId(), songId, slotId);

        List<Song> savedTracks = new ArrayList<>();

        for (var kvp : SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId()).entrySet()) {
            slotId = kvp.getKey();
            songId = kvp.getValue();

            song = SongMachineDao.getSong(songId);
            song.setSlotId(slotId);

            savedTracks.add(song);
        }

        player.send(new JUKEBOX_DISCS(savedTracks));
        new GET_USER_SONG_DISCS().handle(player, null);
    }
}

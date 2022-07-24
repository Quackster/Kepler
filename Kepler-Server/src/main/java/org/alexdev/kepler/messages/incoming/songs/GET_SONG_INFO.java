package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.SONG_INFO;
import org.alexdev.kepler.messages.outgoing.songs.SOUND_PACKAGES;
import org.alexdev.kepler.messages.outgoing.songs.USER_SOUND_PACKAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GET_SONG_INFO implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (room.getItemManager().getSoundMachine() == null) {
            return;
        }

        player.send(new USER_SOUND_PACKAGES(player.getInventory().getSoundsets()));
        player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));

        var song = SongMachineDao.getSongList(room.getItemManager().getSoundMachine().getId());

        if (song.size() > 0) {
            player.send(new SONG_INFO(song.get(0)));
        }

        /*
        int songId = -1;

        if (player.getVersion() <= 14) {
            songId = room.getItemManager().getSoundMachine().getId();

            player.send(new USER_SOUND_PACKAGES(player.getInventory().getSoundsets()));
            player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));
        } else {
            try {
                songId = reader.readInt();
            } catch (Exception ex){
                throw new Exception("Replace your fucking CCTs with the ones in /tools/v15_ccts.zip if you're running this on v15 port");
            }
        }

        var song = SongMachineDao.getSong(songId);

        if (song != null) {
            player.send(new SONG_INFO(SongMachineDao.getSong(songId)));
        }*/
    }
}
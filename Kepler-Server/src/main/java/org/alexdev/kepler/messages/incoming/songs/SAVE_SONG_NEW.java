package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.SONG_NEW;
import org.alexdev.kepler.messages.outgoing.songs.SOUND_PACKAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class SAVE_SONG_NEW implements MessageEvent {
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

        String title = StringUtil.filterInput(reader.readString(), true);
        String data = StringUtil.filterInput(reader.readString(), true);

        SongMachineDao.addSong(player.getDetails().getId(),
                room.getItemManager().getSoundMachine().getId(),
                title,
                calculateSongLength(data),
                data);

        player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));
        player.send(new SONG_NEW(room.getItemManager().getSoundMachine().getId(), title));
    }

    public static int calculateSongLength(String Data) {
        int songLength = 0;

        try {
            String[] Track = Data.split(":");

            for (int i = 1; i < 8; i += 3) {
                int trackLength = 0;
                String[] Samples = Track[i].split(";");

                for (String Sample : Samples) {
                    trackLength += Integer.parseInt(Sample.substring(Sample.indexOf(",") + 1));
                }

                if (trackLength > songLength) {
                    songLength = trackLength;
                }
            }
            return songLength;
        }
        catch (Exception e) { return -1; }
    }
}

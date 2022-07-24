package org.alexdev.kepler.messages.incoming.songs;

import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.songs.SONG_NEW;
import org.alexdev.kepler.messages.outgoing.songs.SOUND_PACKAGES;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

import java.util.regex.Pattern;

public class SAVE_SONG implements MessageEvent {
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

        String title = ("Trax item song " + room.getItemManager().getSoundMachine().getId());
        String data = StringUtil.filterInput(reader.readString(), true);

        SongMachineDao.deleteSong(room.getItemManager().getSoundMachine().getId());
        SongMachineDao.addSong(
                room.getItemManager().getSoundMachine().getId(),
                player.getDetails().getId(),
                room.getItemManager().getSoundMachine().getId(),
                "",
                calculateSongLength(data),
                data);

        player.send(new SOUND_PACKAGES(SongMachineDao.getTracks(room.getItemManager().getSoundMachine().getId())));
        player.send(new SONG_NEW(room.getItemManager().getSoundMachine().getId(), title));
    }

    public static int calculateSongLength(String song) {
        try {
            String songData = song.substring(0, song.length() - 1);
            songData = songData.replace(":4:", "|");
            songData = songData.replace(":3:", "|");
            songData = songData.replace(":2:", "|");
            songData = songData.replace("1:", "");

            String[] data = songData.split(Pattern.quote("|"));
            String[] tracks = new String[]{data[0], data[1], data[2], data[3]};

            int songLength = 0;

            for (String track : tracks) {
                String[] samples = track.split(Pattern.quote(";"));
                int trackLength = 0;

                for (String sample : samples) {
                    int sampleSeconds = Integer.parseInt(sample.split(Pattern.quote(","))[1]) * 2;
                    trackLength += sampleSeconds;
                }

                if (trackLength > songLength)
                    songLength = trackLength;
            }

            return songLength;
        } catch (Exception e) {
            return 0;
        }
    }
}

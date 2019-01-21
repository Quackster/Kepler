package org.alexdev.kepler.messages.outgoing.jukebox;


import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class JUKEBOX_DISCS extends MessageComposer {
    private final List<Song> savedTracks;

    public JUKEBOX_DISCS(List<Song> savedTracks) {
        this.savedTracks = savedTracks;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(10);
        response.writeInt(this.savedTracks.size());

        for (Song song : this.savedTracks) {
            response.writeInt(song.getSlotId());
            response.writeInt(song.getId());
            response.writeInt(song.getLength());

            response.writeString(song.getTitle());
            response.writeString(PlayerManager.getInstance().getPlayerData(song.getUserId()).getName());
        }
    }

    @Override
    public short getHeader() {
        return 334;
    }
}

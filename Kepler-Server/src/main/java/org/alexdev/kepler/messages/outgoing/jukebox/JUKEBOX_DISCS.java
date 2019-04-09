package org.alexdev.kepler.messages.outgoing.jukebox;


import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.game.song.jukebox.BurnedDisk;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.Map;

public class JUKEBOX_DISCS extends MessageComposer {
    private final Map<BurnedDisk, Song> disks;

    public JUKEBOX_DISCS(Map<BurnedDisk, Song> savedTracks) {
        this.disks = savedTracks;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(10);
        response.writeInt(this.disks.size());

        for (var kvp : this.disks.entrySet()) {
            BurnedDisk burnedDisk = kvp.getKey();
            Song song = kvp.getValue();

            response.writeInt(burnedDisk.getSlotId());
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

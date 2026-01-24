package net.h4bbo.kepler.messages.outgoing.jukebox;


import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.kepler.game.song.jukebox.BurnedDisk;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

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

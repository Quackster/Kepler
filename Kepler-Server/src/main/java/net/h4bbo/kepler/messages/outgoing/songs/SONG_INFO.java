package net.h4bbo.kepler.messages.outgoing.songs;

import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SONG_INFO extends MessageComposer {
    private final Song song;

    public SONG_INFO(Song song) {
        this.song = song;
    }

    @Override
    public void compose(NettyResponse response) {
        // TODO: V15 toggle?
        // if (getPlayer().getVersion() > 14) {
         //   response.writeInt(this.song.getId());
         //   response.writeString(this.song.getTitle());
        //}

        response.writeString(this.song.getData());
    }

    @Override
    public short getHeader() {
        return 300; // "Dl"
    }
}

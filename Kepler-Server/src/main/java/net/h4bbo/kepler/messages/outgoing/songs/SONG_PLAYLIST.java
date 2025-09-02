package net.h4bbo.kepler.messages.outgoing.songs;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.song.SongPlaylist;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class SONG_PLAYLIST extends MessageComposer {
    private final List<SongPlaylist> songPlaylist;

    public SONG_PLAYLIST(List<SongPlaylist> songPlaylist) {
        this.songPlaylist = songPlaylist;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(0);
        response.writeInt(this.songPlaylist.size());

        int slotId = 1;
        for (SongPlaylist playlist : this.songPlaylist) {
            response.writeInt(playlist.getSong().getId());
            response.writeInt(playlist.getSong().getLength());
            response.writeString(playlist.getSong().getTitle());
            response.writeString(PlayerDao.getName(playlist.getSong().getUserId()));
            slotId++;
        }
    }

    @Override
    public short getHeader() {
        return 323; // "EC"
    }
}

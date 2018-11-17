package org.alexdev.kepler.messages.outgoing.songs;

import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.Map;

public class SOUND_PACKAGES extends MessageComposer {
    private final Map<Integer, Integer> tracks;

    public SOUND_PACKAGES(Map<Integer, Integer> tracks) {
        this.tracks = tracks;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(4);
        response.writeInt(this.tracks.size());

        for (var set : this.tracks.entrySet()) {
            int slotId = set.getKey();
            int soundSet = set.getValue();

            response.writeInt(slotId);
            response.writeInt(soundSet);
            response.writeInt(9); // 9 samples per set

            int v = (soundSet * 9) - 8;

            for (int j = v; j <= v + 8; j++) {
                response.writeInt(j);
            }
        }
    }

    @Override
    public short getHeader() {
        return 301; // "Dm"
    }
}

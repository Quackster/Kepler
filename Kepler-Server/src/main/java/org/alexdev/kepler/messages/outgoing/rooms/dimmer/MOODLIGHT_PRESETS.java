package org.alexdev.kepler.messages.outgoing.rooms.dimmer;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class MOODLIGHT_PRESETS extends MessageComposer {
    private final int currentPreset;
    private final List<String> presets;

    public MOODLIGHT_PRESETS(int currentPreset, List<String> presets) {
        this.currentPreset = currentPreset;
        this.presets = presets;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.presets.size());
        response.writeInt(this.currentPreset);

        int i = 1;
        for (String preset : this.presets) {
            String[] presetData = preset.split(",");

            response.writeInt(i);
            response.writeInt(Integer.parseInt(presetData[0]));
            response.writeString(presetData[1].replace("#", ""));
            response.writeInt(Integer.parseInt(presetData[2]));
            i++;
        }
    }

    @Override
    public short getHeader() {
        return 365; // "Em"
    }
}

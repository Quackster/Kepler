package org.alexdev.kepler.messages.incoming.rooms.dimmer;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.MoodlightDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.dimmer.MOODLIGHT_PRESETS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class MSG_ROOMDIMMER_GET_PRESETS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();
        Item item = room.getItemManager().getMoodlight();

        if (item == null) {
            return;
        }

        if (!MoodlightDao.containsPreset(item.getId())) {
            MoodlightDao.createPresets(item.getId());
        }

        Pair<Integer, ArrayList<String>> presetData = MoodlightDao.getPresets(item.getId());

        int currentPreset = presetData.getLeft();
        ArrayList<String> presets = presetData.getRight();

            String currentPresetData = presets.get(currentPreset - 1);

            if (!item.getCustomData().contains(currentPreset + ",")) {
                item.setCustomData("1," + currentPreset + "," + currentPresetData);
                ItemDao.updateItem(item);
            }

        player.send(new MOODLIGHT_PRESETS(currentPreset, presets));
    }
}
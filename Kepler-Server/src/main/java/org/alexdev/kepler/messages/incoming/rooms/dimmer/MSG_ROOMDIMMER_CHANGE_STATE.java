package org.alexdev.kepler.messages.incoming.rooms.dimmer;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.MoodlightDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE.ChatMessageType;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class MSG_ROOMDIMMER_CHANGE_STATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        Item item = room.getItemManager().getMoodlight();

        if (item == null) {
            return;
        }

        if (!MoodlightDao.containsPreset(item.getId())) {
            MoodlightDao.createPresets(item.getId());
        }

        // Cancel RainbowTask because the operator decided to use their own moodlight settings.
        if (room.getTaskManager().hasTask("RainbowTask")) {
            room.getTaskManager().cancelTask("RainbowTask");
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Rainbow room dimmer cycle has stopped"));
        }

        Pair<Integer, ArrayList<String>> presetData = MoodlightDao.getPresets(item.getId());

        int currentPreset = presetData.getLeft();
        ArrayList<String> presets = presetData.getRight();

        boolean isEnabled = !(item.getCustomData().charAt(0) == '2');
        item.setCustomData((isEnabled ? "2" : "1") + "," + currentPreset + "," + presets.get(currentPreset - 1));
        item.updateStatus();

        ItemDao.updateItem(item);
    }
}
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
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSG_ROOMDIMMER_SET_PRESET implements MessageEvent {
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

        int presetId = reader.readInt();
        int backgroundState = reader.readInt();
        String presetColour = reader.readString();
        int presetStrength = reader.readInt();

        // Make sure presetColour is a valid hex colour
        Pattern colorPattern = Pattern.compile("#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})");

        if (!colorPattern.matcher(presetColour).matches()) {
            return; // Not a hex color
        }

        if (!GameConfiguration.getInstance().getBoolean("roomdimmer.scripting.allowed")) {
            // Only check if roomdimmer scripting is allowed
            if (presetId > 3 || presetId < 1 || backgroundState > 2 || backgroundState < 1 ||
                    (presetColour.equals("#74F5F5") &&
                            presetColour.equals("#0053F7") &&
                            presetColour.equals("#E759DE") &&
                            presetColour.equals("#EA4532") &&
                            presetColour.equals("#F2F851") &&
                            presetColour.equals("#82F349") &&
                            presetColour.equals("#000000")
                            || presetStrength > 255 || presetStrength < 77)) {
                return; // Nope, no scripting room dimmers allowed here!
            }
        }

        // Cancel RainbowTask because the operator decided to use their own moodlight settings.
        if (room.getTaskManager().hasTask("RainbowTask")) {
            room.getTaskManager().cancelTask("RainbowTask");
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Rainbow room dimmer cycle has stopped"));
        }

        Pair<Integer, ArrayList<String>> presetData = MoodlightDao.getPresets(item.getId());
        List<String> presets = presetData.getRight();

        presets.set(presetId - 1, backgroundState + "," + presetColour + "," + presetStrength);
        item.setCustomData("2," + presetId + "," + backgroundState + "," + presetColour + "," + presetStrength);
        item.updateStatus();

        ItemDao.updateItem(item);
        MoodlightDao.updatePresets(item.getId(), presetId, presets);
    }
}
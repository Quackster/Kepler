package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.tasks.RainbowTask;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE.ChatMessageType;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class RainbowDimmerCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!player.getRoomUser().getRoom().isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        int tickInterval = 5;

        if (args.length == 1) {
            if (!StringUtils.isNumeric(args[0])) {
                player.send(new ALERT("Please specify the amount of seconds inbetween the colours changing as a number"));
                return;
            } else {
                tickInterval = Integer.parseInt(args[0]);
            }
        }

        if (tickInterval < 1) {
            tickInterval = 1;
        }

        Item moodlight = room.getItemManager().getMoodlight();

        if (moodlight == null) {
            player.send(new ALERT("This command requires a moodlight placed for it to work"));
            return;
        }


        Player roomOwner = PlayerManager.getInstance().getPlayerById(room.getData().getOwnerId());
        boolean ownerInRoom = false;

        if (roomOwner.getRoomUser().getRoom() != null) {
            ownerInRoom = roomOwner.getRoomUser().getRoom().getData().getOwnerId() == room.getData().getOwnerId();
        }

        String statusMessage;

        if (room.getTaskManager().hasTask("RainbowTask")) {
            room.getTaskManager().cancelTask("RainbowTask");

            statusMessage = "Rainbow room dimmer cycle has stopped";
        } else {
            RainbowTask rainbowTask = new RainbowTask(room);
            room.getTaskManager().scheduleTask("RainbowTask", rainbowTask, 0, tickInterval, TimeUnit.SECONDS);

            statusMessage = "Rainbow room dimmer cycle has started";
        }

        player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), statusMessage));

        // Send status of room task to roomowner
        if (ownerInRoom) {
            roomOwner.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, roomOwner.getRoomUser().getInstanceId(), statusMessage));
        }
    }

    @Override
    public String getDescription() {
        return "<seconds> - Cycles through the rainbow in your very own room!";
    }
}

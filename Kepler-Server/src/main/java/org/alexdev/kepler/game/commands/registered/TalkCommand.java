package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import org.alexdev.kepler.util.StringUtil;

public class TalkCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        String talkMessage;

        if (args.length > 0) {
            talkMessage = StringUtil.filterInput(String.join(" ", args), true);
        } else {
            talkMessage = "";
        }

        TalkCommand.createVoiceSpeakMessage(player.getRoomUser().getRoom(), talkMessage);
    }

    public static void createVoiceSpeakMessage(Room room, String text) {
        // 'Speaker'
        Item pItem = new Item();
        pItem.setId(Integer.MAX_VALUE);
        pItem.setPosition(new Position(255, 255, -1f));
        pItem.setCustomData("voiceSpeak(\"" + text + "\")");
        pItem.getDefinition().setSprite("spotlight");
        pItem.getDefinition().setLength(1);
        pItem.getDefinition().setWidth(1);
        room.send(new PLACE_FLOORITEM(pItem));
    }

    @Override
    public String getDescription() {
        return "Voice to text command";
    }
}

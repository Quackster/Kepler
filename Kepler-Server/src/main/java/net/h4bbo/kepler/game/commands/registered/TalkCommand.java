package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import net.h4bbo.kepler.util.StringUtil;

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

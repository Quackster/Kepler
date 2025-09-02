package net.h4bbo.kepler.messages.incoming.rooms.items;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

public class SETITEMSTATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        String itemIdString = reader.readString();

        if (!StringUtils.isNumeric(itemIdString)) {
            return;
        }

        int itemId = Integer.parseInt(itemIdString);
        Item item = room.getItemManager().getById(itemId);

        if (item == null) return;
        if (item.getDefinition().getSprite().equals("poster") ) return; // Stop poster scripting no more bus posters.

        if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)
                || item.hasBehaviour(ItemBehaviour.DICE)
                || item.hasBehaviour(ItemBehaviour.PRIZE_TROPHY)
                || item.hasBehaviour(ItemBehaviour.POST_IT)
                || item.hasBehaviour(ItemBehaviour.WHEEL_OF_FORTUNE)
                || item.hasBehaviour(ItemBehaviour.PHOTO)
                || item.hasBehaviour(ItemBehaviour.SOUND_MACHINE_SAMPLE_SET)) {
            return; // Prevent dice rigging, scripting trophies, post-its, etc.
        }

        if (item.getDefinition().hasBehaviour(ItemBehaviour.REQUIRES_RIGHTS_FOR_INTERACTION) && !room.hasRights(player.getDetails().getId())) {
            return;
        }

        String customData = String.valueOf(reader.readInt());

        item.setCustomData(customData);
        item.updateStatus();
        item.save();
    }
}

package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.interactors.InteractionType;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.mapping.RoomTile;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class CARRYDRINK implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (player.getRoomUser().getRoom().isPublicRoom()) {
            String contents = reader.contents();

            if (StringUtils.isNumeric(contents)) {
                RoomTile roomTile = player.getRoomUser().getRoom().getMapping().getTile(player.getRoomUser().getPosition().getSquareInFront());

                if (roomTile.getItems().stream().anyMatch(item -> item.getDefinition().getSprite().equals("arabian_teamk"))) {
                    contents = String.valueOf(1);
                }

                player.getRoomUser().carryItem(Integer.parseInt(contents), null);
            } else {
                player.getRoomUser().carryItem(-1, contents);
            }
        } else {
            if (player.getRoomUser().getLastItemInteraction() == null) {
                RoomTile roomTile = player.getRoomUser().getRoom().getMapping().getTile(player.getRoomUser().getPosition().getSquareInFront());

                if (roomTile.getItems().stream().anyMatch(item -> item.getDefinition().getInteractionType() == InteractionType.VENDING_MACHINE)) {
                    player.getRoomUser().setLastItemInteraction(roomTile.getItems().stream().filter(item -> item.getDefinition().getInteractionType() == InteractionType.VENDING_MACHINE).findFirst().orElse(null));
                }

                if (player.getRoomUser().getLastItemInteraction() == null) {
                    return;
                }
            }

            Item item = player.getRoomUser().getLastItemInteraction();

            if (item.getDefinition().getInteractionType() != InteractionType.VENDING_MACHINE) {
                return;
            }

            if (!item.getTile().getPosition().touches(player.getRoomUser().getTile().getPosition())) {
                return;
            }

            int randomDrinkId = item.getDefinition().getDrinkIds()[ThreadLocalRandom.current().nextInt(0, item.getDefinition().getDrinkIds().length)];

            player.getRoomUser().carryItem(randomDrinkId, null);
            player.getRoomUser().setLastItemInteraction(null);
        }

        player.getRoomUser().getTimerManager().resetRoomTimer();
    }
}

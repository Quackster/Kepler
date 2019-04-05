package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class PLACESTUFF implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.hasRights(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        String content = reader.contents();
        String[] data = content.split(" ");

        if (data.length == 0) {
            return;
        }

        // Make sure provided data is numeric
        if (!StringUtils.isNumeric(data[0])) {
            return;
        }

        int itemId = Integer.parseInt(data[0]);
        Item item = player.getInventory().getItem(itemId);

        if (item == null) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            String wallPosition = content.substring(data[0].length() + 1);

            if (item.hasBehaviour(ItemBehaviour.POST_IT)) {
                String defaultColour = "FFFF33";

                Item sticky = new Item();
                sticky.setOwnerId(room.getData().getOwnerId());
                sticky.setDefinitionId(item.getDefinition().getId());
                sticky.setCustomData(defaultColour);
                sticky.setWallPosition(wallPosition);
                sticky.setRoomId(room.getId());

                ItemDao.newItem(sticky);
                room.getMapping().addItem(sticky);

                // Set custom data as 1 for 1 post-it, if for some reason they have no number for the post-it.
                if (!StringUtils.isNumeric(item.getCustomData())) {
                    item.setCustomData("1");
                }

                if (StringUtils.isNumeric(item.getCustomData())) {
                    int totalStickies = Integer.parseInt(item.getCustomData()) - 1;

                    if (totalStickies <= 0) {
                        player.getInventory().getItems().remove(item);
                        ItemDao.deleteItem(item.getId());
                    } else {
                        item.setCustomData(String.valueOf(totalStickies));
                        item.save();
                    }
                }
                return;
            }

            item.setWallPosition(wallPosition);
        } else {
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);

            // skip 3 and 4 as they're dimensions, we don't need 'em since it's server-side variables, never trust the client!
            int rotation = Integer.parseInt(data[5]);

            if (item.hasBehaviour(ItemBehaviour.REDIRECT_ROTATION_0)) {
                rotation = 0;
            }

            if (item.hasBehaviour(ItemBehaviour.REDIRECT_ROTATION_2)) {
                rotation = 2;
            }

            if (item.hasBehaviour(ItemBehaviour.REDIRECT_ROTATION_4)) {
                rotation = 4;
            }

            if (!item.isValidMove(item, room, x, y, rotation)) {
                return;
            }

            if (room.getMapping().getTile(x, y) != null) {
                item.getPosition().setX(x);
                item.getPosition().setY(y);
                item.getPosition().setRotation(rotation);
            }
        }

        if (room.getItemManager().getSoundMachine() != null && (item.hasBehaviour(ItemBehaviour.SOUND_MACHINE) || item.hasBehaviour(ItemBehaviour.JUKEBOX))) {
            player.send(new ALERT(TextsManager.getInstance().getValue("room_sound_furni_limit")));
            return;
        }

        if (room.getItemManager().getMoodlight() != null && (item.hasBehaviour(ItemBehaviour.ROOMDIMMER))) {
            player.send(new ALERT(TextsManager.getInstance().getValue("roomdimmer_furni_limit")));
            return;
        }

        room.getMapping().addItem(item);
        player.getInventory().getItems().remove(item);
    }
}

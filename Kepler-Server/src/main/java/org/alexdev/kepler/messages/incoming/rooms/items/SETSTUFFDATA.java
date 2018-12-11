package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SETSTUFFDATA implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        // Do not process public room items
        if (player.getRoomUser().getRoom().isPublicRoom()) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        int itemId = Integer.parseInt(reader.readString());
        String itemData = reader.readString();

        Item item = room.getItemManager().getById(itemId);

        if (item == null) {
            return;
        }

        if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)
                || item.hasBehaviour(ItemBehaviour.DICE)
                || item.hasBehaviour(ItemBehaviour.PRIZE_TROPHY)
                || item.hasBehaviour(ItemBehaviour.POST_IT)
                || item.hasBehaviour(ItemBehaviour.ROLLER)
                || item.hasBehaviour(ItemBehaviour.WHEEL_OF_FORTUNE)
                || item.hasBehaviour(ItemBehaviour.SOUND_MACHINE_SAMPLE_SET)) {
            return; // Prevent dice rigging, scripting trophies, post-its, etc.
        }


        if (item.hasBehaviour(ItemBehaviour.REQUIRES_RIGHTS_FOR_INTERACTION)
                && !room.hasRights(player.getDetails().getId())
                && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }
        
        if (item.hasBehaviour(ItemBehaviour.REQUIRES_TOUCHING_FOR_INTERACTION)) {
            if (!item.getTile().touches(player.getRoomUser().getTile())) {
                Position nextPosition = item.getPosition().getSquareInFront();

                // TODO: Look into why everyone receives the door close packet, in the meantime, disable the check for teleporters, otherwise everyone will work towards the teleporter
                if (!item.hasBehaviour(ItemBehaviour.TELEPORTER)) {


                    if (!RoomTile.isValidTile(room, player, nextPosition)) {
                        nextPosition = item.getTile().getNextAvailablePosition(player);
                    }

                    player.getRoomUser().walkTo(
                            nextPosition.getX(),
                            nextPosition.getY()
                    );

                    return;
                }
            }
        }

        String newData = null;

        if (item.hasBehaviour(ItemBehaviour.DOOR)) {
            if (itemData.equals("O") || itemData.equals("C")) {
                newData = itemData;

                if (itemData.equals("C")) {
                    RoomTile tile = item.getTile();

                    // Make all entities walk out of gate when it's closed
                    if (tile.getEntities().size() > 0) {
                            // Can't close gate if there's a user on the tile
                            return;
                    }
                }
            }
        } else {
            if (item.hasBehaviour(ItemBehaviour.CUSTOM_DATA_TRUE_FALSE) &&
                    (itemData.equals("TRUE") || itemData.equals("FALSE") || itemData.equals("I") || itemData.equals("H"))) {
                newData = itemData;
            }

            if (item.hasBehaviour(ItemBehaviour.CUSTOM_DATA_NUMERIC_ON_OFF) &&
                    (itemData.equals("2") || itemData.equals("1") || itemData.equals("0"))) {
                newData = itemData;
            }

            if (item.hasBehaviour(ItemBehaviour.CUSTOM_DATA_ON_OFF) &&
                    (itemData.equals("ON") || itemData.equals("OFF"))) {
                newData = itemData;
            }

            // Special handler because we don't want to allow users to empty the waterbowl, only pets can
            if (item.getDefinition().getSprite().startsWith("waterbowl") && itemData.equals("5")) {
                newData = itemData;
            }

            if (item.hasBehaviour(ItemBehaviour.CUSTOM_DATA_NUMERIC_STATE)) {
                if (itemData.equals("x")) {
                    newData = itemData;
                } else {
                    if (StringUtils.isNumeric(itemData)) {
                        int stateId = Integer.parseInt(itemData);

                        if (stateId >= 0 && stateId <= 99) {
                            newData = itemData;
                        }
                    }
                }
            }
        }

        if (newData == null) {
            return;
        }

        item.setCustomData(newData);
        item.updateStatus();

        if (!item.getDefinition().hasBehaviour(ItemBehaviour.CUSTOM_DATA_TRUE_FALSE)) {
            ItemDao.updateItem(item);
        }
    }
}

package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.incoming.catalogue.GRPC;
import org.alexdev.kepler.messages.outgoing.catalogue.DELIVER_PRESENT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;
import java.util.regex.Pattern;

public class PRESENTOPEN implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            return;
        }

        int itemId = Integer.parseInt(reader.contents());
        Item item = room.getItemManager().getById(itemId);

        if (item == null || !item.hasBehaviour(ItemBehaviour.PRESENT)) {
            return;
        }

        String[] presentData = item.getCustomData().split(Pattern.quote(Item.PRESENT_DELIMETER));

        String saleCode = presentData[0];
        String receivedFrom = presentData[1];
        String extraData = presentData[3];
        long timestamp= Long.parseLong(presentData[4]);

        //System.out.println("Present data: " + String.join(",", presentData));
        //System.out.println("Custom data: " + item.getCustomData());
        //System.out.println(receivedFrom);

        CatalogueItem catalogueItem = CatalogueManager.getInstance().getCatalogueItem(saleCode);

        // Don't create a new item instance, reuse if the item isn't a trophy or teleporter, etc
        if (!catalogueItem.isPackage() && !catalogueItem.getDefinition().hasBehaviour(ItemBehaviour.PRIZE_TROPHY) &&
                !catalogueItem.getDefinition().hasBehaviour(ItemBehaviour.TELEPORTER) &&
                !catalogueItem.getDefinition().hasBehaviour(ItemBehaviour.ROOMDIMMER) &&
                !catalogueItem.getDefinition().hasBehaviour(ItemBehaviour.DECORATION) &&
                !catalogueItem.getDefinition().hasBehaviour(ItemBehaviour.POST_IT)) {
            room.getMapping().removeItem(player, item);

            item.setDefinitionId(catalogueItem.getDefinition().getId());
            item.setCustomData(extraData);

            ItemDao.updateItem(item);

            player.send(new DELIVER_PRESENT(item));

            player.getInventory().addItem(item);
            player.getInventory().getView("new");
        } else {
            List<Item> itemList = CatalogueManager.getInstance().purchase(player, catalogueItem, extraData, receivedFrom, timestamp);

            if (!itemList.isEmpty()) {
                player.send(new DELIVER_PRESENT(itemList.get(0)));
                player.getInventory().getView("new");
            }

            room.getMapping().removeItem(player, item);
            ItemDao.deleteItem(item.getId());
        }
}
}

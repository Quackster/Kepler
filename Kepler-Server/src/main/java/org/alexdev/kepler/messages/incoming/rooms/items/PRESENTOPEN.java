package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.incoming.catalogue.GRPC;
import org.alexdev.kepler.messages.outgoing.catalogue.DELIVER_PRESENT;
import org.alexdev.kepler.messages.outgoing.rooms.items.ITEM_DELIVERED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;

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

        String[] presentData = item.getCustomData().split(Character.toString((char)9));

        String saleCode = presentData[0];
        String receivedFrom = presentData[1];
        String extraData = presentData[3];
        long timestamp = DateUtil.getCurrentTimeSeconds();

        try {
            timestamp = Long.parseLong(presentData[4]);
        } catch (Exception ignored) {

        }

        CatalogueItem catalogueItem = CatalogueManager.getInstance().getCatalogueItem(saleCode);

        List<Item> itemList = GRPC.purchase(player, catalogueItem, extraData, receivedFrom, timestamp);

        if (itemList.isEmpty()) {
            return;
        }

        player.send(new DELIVER_PRESENT(itemList.get(0)));
        player.getInventory().getView("new");

        room.getMapping().removeItem(item);
        ItemDao.deleteItem(item.getId());
    }
}

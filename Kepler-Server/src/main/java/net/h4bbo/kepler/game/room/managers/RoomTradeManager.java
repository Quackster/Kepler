package net.h4bbo.kepler.game.room.managers;

import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.dao.mysql.TransactionDao;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.entities.RoomPlayer;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.outgoing.trade.TRADE_CLOSE;
import net.h4bbo.kepler.messages.outgoing.trade.TRADE_ITEMS;

import java.util.ArrayList;
import java.util.List;

public class RoomTradeManager {

    /**
     * Close trade window, called when user leaves room, or closes
     * the trade window. Will close the partners trade window too.
     *
     * @param roomEntity the room user to close the trade window for
     */
    public static void close(RoomPlayer roomEntity) {
        Player player = (Player) roomEntity.getEntity();

        if (roomEntity.getTradePartner() != null) {
            player.send(new TRADE_CLOSE());
            player.getInventory().getView("new");

            roomEntity.getTradePartner().send(new TRADE_CLOSE());
            roomEntity.getTradePartner().getInventory().getView("new");

            reset(roomEntity.getTradePartner().getRoomUser());
        }

        reset(roomEntity);
    }

    /**
     * Resets all trade variables.
     *
     * @param roomEntity the room user to reset the trade variables for
     */
    private static void reset(RoomPlayer roomEntity) {
        roomEntity.getTradeItems().clear();
        roomEntity.setTradeAccept(false);
        roomEntity.setTradePartner(null);

        roomEntity.removeStatus(StatusType.TRADE);
        roomEntity.setNeedsUpdate(true);
    }

    /**
     * Refresh the trade window, called when a user agrees/unagrees or adds
     * an item to the trade window. Will be ignored if they have no trade
     * partner.
     *
     * @param player the player to refresh the trade window for
     */
    public static void refreshWindow(Player player) {
        if (player.getRoomUser().getTradePartner() == null) {
            return;
        }

        Player tradePartner = player.getRoomUser().getTradePartner();

        player.send(new TRADE_ITEMS(
                player,
                player.getRoomUser().getTradeItems(),
                player.getRoomUser().hasAcceptedTrade(),
                tradePartner,
                tradePartner.getRoomUser().getTradeItems(),
                tradePartner.getRoomUser().hasAcceptedTrade()
        ));
    }

    /**
     * Adds an item from the trade partners offered items into the first parameter
     * players' inventory.
     *
     * @param player the player to add the items into
     * @param tradePartner the player to get the items offered from
     */
    public static void addItems(Player player, Player tradePartner) {
        List<Item> itemsToUpdate = new ArrayList<>();

        for (Item item : tradePartner.getRoomUser().getTradeItems()) {
            tradePartner.getInventory().getItems().remove(item);
            player.getInventory().addItem(item);

            item.setOwnerId(player.getDetails().getId());
            itemsToUpdate.add(item);

            try {
                TransactionDao.createTransaction(player.getDetails().getId(),
                        String.valueOf(item.getId()), String.valueOf(item.getDefinition().getId()), 1,
                        "Traded " + item.getDefinition().getName() + " from " + tradePartner.getDetails().getName(),
                        0, tradePartner.getDetails().getId(), false);

                TransactionDao.createTransaction(tradePartner.getDetails().getId(),
                        String.valueOf(item.getId()), String.valueOf(item.getDefinition().getId()), 1,
                        "Traded " + item.getDefinition().getName() + " to " + player.getDetails().getName(),
                        0, player.getDetails().getId(), false);
            } catch (Exception ex) {

            }
        }

        ItemDao.updateItems(itemsToUpdate);
    }
}

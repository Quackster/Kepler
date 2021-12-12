package org.alexdev.kepler.game.inventory;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.inventory.INVENTORY;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

import java.util.*;

public class Inventory {
    private static final int MAX_ITEMS_PER_PAGE = 9;

    private Player player;
    private List<Item> items;

    private Map<Integer, List<Item>> paginatedItems;
    private int handStripPageIndex;

    public Inventory(Player player) {
        this.player = player;
        this.reload();
    }

    /**
     * Reload inventory.
     */
    public void reload() {
        this.handStripPageIndex = 0;
        this.items = ItemDao.getInventory(this.player.getDetails().getId());
        this.refreshPagination();

        // Prevent possible virtual item dupes
        if (this.player.getRoomUser().getRoom() != null) {
            this.items.removeIf(x -> this.player.getRoomUser().getRoom().getItems().stream().anyMatch(item -> x.getDatabaseId().equals(item.getDatabaseId())));
        }

        for (var item : this.items) {
            item.assignVirtualId();
        }
    }

    /**
     * Refreshes the pagination by making the most recently bought items appear first.
     */
    private void refreshPagination() {
        int orderId = 0;

        for (Item item : this.items) {
            if (orderId != item.getOrderId()) {
                item.setOrderId(orderId);
                item.save();
            }

            orderId++;

        }

        List<Item> tempList = new ArrayList<>();

        for (Item item : this.items) {
            // Don't show items if they are hidden
            if (item.isHidden()) {
                continue;
            }

            // Don't show items if they are currently in trade window
            if (this.player.getRoomUser().getTradePartner() != null) {
                if (this.player.getRoomUser().getTradeItems().contains(item)) {
                    continue;
                }
            }

            tempList.add(item);
        }

        this.items.sort(Comparator.comparingInt(Item::getOrderId));
        this.paginatedItems = StringUtil.paginate(tempList, MAX_ITEMS_PER_PAGE);
    }

    /**
     * Get the view of the inventory.
     *
     * @param stripView the view type
     */
    public void getView(String stripView) {
        this.refreshPagination();
        this.changeView(stripView);

        Map<Integer, Item> casts = this.getCasts();
        this.player.send(new INVENTORY(this, casts));
    }

    /**
     * Get the inventory casts for opening hand.
     */
    private Map<Integer,Item> getCasts() {
        Map<Integer, Item> casts = new LinkedHashMap<>();

        if (this.paginatedItems.containsKey(this.handStripPageIndex)) {
            int stripSlotId = (this.handStripPageIndex * MAX_ITEMS_PER_PAGE);

            for (Item item : this.paginatedItems.get(this.handStripPageIndex)) {
                casts.put(stripSlotId++, item);
            }
        }

        return casts;
    }

    /**
     * Change the inventory view over.
     *
     * @param stripView the strip view to change
     */
    private void changeView(String stripView) {
        if (stripView.equals("new")) {
            this.handStripPageIndex = 0;
        }

        if (stripView.equals("next")) {
            this.handStripPageIndex++;
        }

        if (stripView.equals("prev")) {
            this.handStripPageIndex--;
        }

        if (stripView.equals("last")) {
            this.handStripPageIndex = this.paginatedItems.size() - 1;
        }

        if (stripView.equals("update")) {
            if (this.handStripPageIndex > this.paginatedItems.size() - 1) {
                this.handStripPageIndex = this.paginatedItems.size() - 1;
            }
        }

        if (!this.paginatedItems.containsKey(this.handStripPageIndex)) {
            this.handStripPageIndex = 0;
        }
    }

    /**
     * Serialise item in hand.
     *
     * @param response the response to write the item to
     * @param item the item to use the data for the packet
     * @param stripSlotId the slot in the hand
     */
    public static void serialise(NettyResponse response, Item item, int stripSlotId) {
        response.writeDelimeter("SI", (char) 30);
        response.writeDelimeter(item.getGameId(), (char) 30);
        response.writeDelimeter(stripSlotId, (char) 30);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            response.writeDelimeter("I", (char) 30);
        } else {
            response.writeDelimeter("S", (char) 30);
        }

        response.writeDelimeter(item.getGameId(), (char) 30);
        response.writeDelimeter(item.getDefinition().getSprite(), (char) 30);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            response.writeDelimeter(item.getCustomData(), (char) 30);
            response.writeDelimeter("0", (char) 30);
        } else {
            response.writeDelimeter(item.getDefinition().getLength(), (char) 30);
            response.writeDelimeter(item.getDefinition().getWidth(), (char) 30);
            response.writeDelimeter(item.getCustomData(), (char) 30);
            response.writeDelimeter(item.getDefinition().getColour(), (char) 30);
            response.writeDelimeter(item.getDefinition().isRecyclable() ? 1 : 1, (char) 30);
            response.writeDelimeter(item.getDefinition().getSprite(), (char) 30);
        }

        response.write("/");
    }

    /**
     * Get inventory item by id.
     *
     * @param itemId the id used to get the inventory item
     * @return the inventory item
     */
    public Item getItem(int itemId) {
        for (Item item : this.items) {
            if (item.getGameId() == itemId) {
                return item;
            }
        }

        return null;
    }

    /**
     * Get inventory item by database id.
     *
     * @param databaseId the id used to get the inventory item
     * @return the inventory item
     */
    public Item getItemByDatabaseId(String databaseId) {
        for (Item item : this.items) {
            if (item.getDatabaseId().equals(databaseId)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Get all soundset track IDs within the inventory.
     *
     * @return the list of soundsets
     */
    public List<Integer> getSoundsets() {
        List<Integer> handSoundsets = new ArrayList<>();

        for (Item item : player.getInventory().getItems()) {
            if (item.isHidden()) {
                continue;
            }
            
            if (item.hasBehaviour(ItemBehaviour.SOUND_MACHINE_SAMPLE_SET)) {
                handSoundsets.add(Integer.parseInt(item.getDefinition().getSprite().split("_")[2]));
            }
        }

        return handSoundsets;
    }

    /**
     * Add the item to the start of items list.
     *
     * @param item the item
     */
    public void addItem(Item item) {
        this.items.remove(item);
        this.items.add(0, item);
    }

    /**
     * Get the list of inventory items.
     *
     * @return the list of items
     */
    public List<Item> getItems() {
        return items;
    }
}

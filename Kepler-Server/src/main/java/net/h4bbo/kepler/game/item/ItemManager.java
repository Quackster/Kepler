package net.h4bbo.kepler.game.item;

import net.h4bbo.kepler.dao.mysql.ItemDao;
import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.item.base.ItemDefinition;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.util.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ItemManager {
    private static ItemManager instance;

    private Map<Integer, ItemDefinition> itemDefinitionMap;

    public ItemManager() {
        this.itemDefinitionMap = ItemDao.getItemDefinitions();
    }

    /**
     * Quick and easy method for creating gifts.
     *
     * @param toPlayer the user to give the gift to
     * @param saleCode the sprite to give
     * @return the item as gift
     */
    public Item createGift(int ownerId, String receivedFrom, String saleCode, String presentLabel, String extraData) {
        int presentId = ThreadLocalRandom.current().nextInt(0, 7);
        String sprite = "present_gen";

        if (presentId > 0) {
            sprite += presentId;
        }

        ItemDefinition itemDef = ItemManager.getInstance().getDefinitionBySprite(sprite);

        Item item = new Item();
        item.setDefinitionId(itemDef.getId());
        item.setOwnerId(ownerId);
        item.setCustomData(CatalogueManager.getInstance().getCatalogueItem(saleCode).getId() +
                Item.PRESENT_DELIMETER + receivedFrom +
                Item.PRESENT_DELIMETER + presentLabel.replace(Item.PRESENT_DELIMETER, "") + //From Habbo" +
                Item.PRESENT_DELIMETER + extraData.replace(Item.PRESENT_DELIMETER, "") +
                Item.PRESENT_DELIMETER + DateUtil.getCurrentTimeSeconds());

        try {
            ItemDao.newItem(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return item;
    }

    /**
     * Get the saved jukebox tracks.
     *
     * @param itemId the jukebox item id
     * @return the list of saved tracks
     */
    public List<Song> getJukeboxTracks(int itemId) {
        List<Song> savedTracks = new ArrayList<>();

        for (var kvp : SongMachineDao.getTracks(itemId).entrySet()) {
            int slotId = kvp.getKey();
            int songId = kvp.getValue();

            Song song = SongMachineDao.getSong(songId);
            song.setSlotId(slotId);

            savedTracks.add(song);
        }

        return savedTracks;
    }

    /**
     * Handle bulk item saving.
     *
     * @param itemSavingQueue the queue that's used for saving items
     */
    public void performItemSaving(BlockingQueue<Item> itemSavingQueue) {
        if (itemSavingQueue.isEmpty()) {
            return;
        }

        List<Item> itemList = new ArrayList<>();
        itemSavingQueue.drainTo(itemList);

        ItemDao.updateItems(itemList);
    }

    /**
     * Handle bulk item deletion.
     *
     * @param itemDeletionQueue the queue that's used for deleting items
     */
    public void performItemDeletion(BlockingQueue<Integer> itemDeletionQueue) {
        try {
            if (itemDeletionQueue.isEmpty()) {
                return;
            }

            List<Integer> itemList = new ArrayList<>();
            itemDeletionQueue.drainTo(itemList);

            if (itemList.size() > 0) {
                ItemDao.deleteItems(itemList);
            }
        } catch (Exception ex) {
            Log.getErrorLogger().error("Error when attempting to save items: ", ex);
        }
    }

    /**
     * Get a item definition by the definition id.
     *
     * @param definitionId the definition id to get for
     * @return the item definition
     */
    public ItemDefinition getDefinition(int definitionId) {
        if (this.itemDefinitionMap.containsKey(definitionId)) {
            return this.itemDefinitionMap.get(definitionId);
        }

        return null;
    }

    /**
     * Get a item definition by sprite name.
     *
     * @param spriteName the name of the sprite to locate the definition
     * @return the item definition
     */
    public ItemDefinition getDefinitionBySprite(String spriteName) {
        for (ItemDefinition definition : this.itemDefinitionMap.values()) {
            if (definition.getSprite().equals(spriteName)) {
                return definition;
            }
        }

        return null;
    }

    /**
     * Resolve the active room item by database id.
     *
     * @param itemId the room item
     * @return the instance of the item, if found
     */
    public Item resolveItem(int itemId) {
        Item databaseItem = ItemDao.getItem(itemId);

        if (databaseItem == null) {
            return null;
        }

        return this.resolveItem(databaseItem);
    }

    /**
     * Resolve the active room item by database instance
     *
     * @param databaseItem the database item instance
     * @return the instance of the item, if found
     */
    public Item resolveItem(Item databaseItem) {
        if (RoomManager.getInstance().getRoomById(databaseItem.getRoomId()) != null) {
            Room room = databaseItem.getRoom();

            if (room != null) {
                return room.getItemManager().getById(databaseItem.getId());
            }
        } else {
            Player itemOwner = PlayerManager.getInstance().getPlayerById(databaseItem.getOwnerId());

            if (itemOwner != null) {
                return itemOwner.getInventory().getItem(databaseItem.getId());
            }
        }

        return null;
    }

    /**
     * Get the {@link ItemManager} instance
     *
     * @return the item manager instance
     */
    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }

        return instance;
    }

    /**
     * Resets the item manager singleton.
     */
    public static void reset() {
        instance = null;
        ItemManager.getInstance();
    }
}

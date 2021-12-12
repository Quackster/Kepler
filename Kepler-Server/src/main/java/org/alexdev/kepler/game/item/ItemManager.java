package org.alexdev.kepler.game.item;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.song.Song;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.util.DateUtil;

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
    public Item createGift(PlayerDetails toPlayer, PlayerDetails fromPlayer, String saleCode, String presentLabel, String extraData) throws Exception {
        String sprite = "present_gen" + ThreadLocalRandom.current().nextInt(1, 7);
        ItemDefinition itemDef = ItemManager.getInstance().getDefinitionBySprite(sprite);

        if (itemDef == null) {
            throw new Exception("Could not create gift, the definition for sprite " + sprite + " doesn't exist");
        }

        Item item = new Item();
        item.setDefinitionId(itemDef.getId());
        item.setOwnerId(toPlayer.getId());
        item.setCustomData(CatalogueManager.getInstance().getCatalogueItem(saleCode).getId() +
                Item.PRESENT_DELIMETER + fromPlayer.getName() +
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

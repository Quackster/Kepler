package org.alexdev.roseau.game.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.STRIPINFO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Inventory {
	private Player player;

	private List<Item> items;
	private Map<Integer, List<Item>> paginatedItems;

	private int cursor;

	public Inventory(Player player) {
		this.player = player;
		this.paginatedItems = Maps.newHashMap();
		this.items = Lists.newArrayList();
		this.cursor = 0;
	}

	public void load() {
		this.dispose();
		
		//Log.println("Inventory.java::load()");

		this.items = Roseau.getDao().getInventory().getInventoryItems(this.player.getDetails().getID());
		
		this.refreshPagination();
	}

	public void refreshPagination() {
		this.paginatedItems.clear();

		int pageID = 0;
		int counter = 0;

		for (Item item : this.items) {

			if (counter > (GameVariables.MAX_ITEMS_PER_PAGE - 1)) {
				pageID++;
				counter = 0;
			} else {
				counter++;
			}

			if (!this.paginatedItems.containsKey(pageID)) {
				this.paginatedItems.put(pageID, new ArrayList<Item>());
			}

			this.paginatedItems.get(pageID).add(item);
		}
	}


	public Item getItem(int id) {
		Optional<Item> inventoryItem = this.items.stream().filter(item -> item.getID() == id).findFirst();
		return inventoryItem.orElse(null);
	}

	public void removeItem(Item item) {
		this.removeItem(item, true);
	}
	
	public void removeItem(Item item, boolean refreshPagination) {
		if (item != null) {
			this.items.remove(item);
		}

		if (refreshPagination) {
			this.refreshPagination();

		}
	}

	public void addItem(Item item) {
		if (item != null) {
			this.items.add(item);
		}

		this.refreshPagination();
	}

	public void refresh(String mode) {
		if (this.paginatedItems.size() > 0) {
			if (mode.equals("last")) {
				cursor = this.paginatedItems.size() - 1;
			}

			if (mode.equals("new")) {
				cursor = 0;
			}

			if (mode.equals("next")) {
				cursor++;
			}

			if (this.paginatedItems.containsKey(this.cursor)) {
				this.player.send(new STRIPINFO(this.paginatedItems.get(this.cursor)));
			} else {
				this.paginatedItems.size();
				this.refresh("new");
			}

		} else {
			this.player.send(new STRIPINFO());
		}
	}

	public void dispose() {
		if (this.items != null) {
			this.items.clear();
			this.items = null;
		}
	}

	public List<Item> getItems() {
		return items;
	}

}

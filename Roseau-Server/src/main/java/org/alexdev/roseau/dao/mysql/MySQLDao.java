package org.alexdev.roseau.dao.mysql;

import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.util.Util;

public class MySQLDao implements Dao {

	private Storage storage;
	private boolean isConnected;

	private PlayerDao player;
	private RoomDao room;
	private ItemDao item;
	private CatalogueDao catalogue;
	private InventoryDao inventory;
	private NavigatorDao navigator;
	private MessengerDao messenger;

	public MySQLDao() {

		this.connect();
		this.player = new MySQLPlayerDao(this);
		this.room = new MySQLRoomDao(this);
		this.item = new MySQLItemDao(this);
		this.catalogue = new MySQLCatalogueDao(this);
		this.inventory = new MySQLInventoryDao(this);
		this.navigator = new MySQLNavigatorDao(this);
		this.messenger = new MySQLMessengerDao(this);
	}

	@Override
	public boolean connect() {

		Log.println("Connecting to MySQL server");
		
		storage = new Storage(Util.getConfiguration().get("Database", "mysql.hostname", String.class), 
				Util.getConfiguration().get("Database", "mysql.username", String.class), 
				Util.getConfiguration().get("Database", "mysql.password", String.class), 
				Util.getConfiguration().get("Database", "mysql.database", String.class)); 

		isConnected = storage.isConnected();

		if (!isConnected) {
			Log.println("Could not connect");
		} else {
			Log.println("Connection to MySQL was a success");
		}
		
		Log.println();
		
		return isConnected;
	}

	public Storage getStorage() {
		return storage;
	}
	
	@Override
	public boolean isConnected() {
		return isConnected;
	}
		
	@Override
	public PlayerDao getPlayer() {
		return this.player;
	}

	@Override
	public RoomDao getRoom() {
		return room;
	}

	@Override
	public ItemDao getItem() {
		return item;
	}

	@Override
	public CatalogueDao getCatalogue() {
		return catalogue;
	}

	@Override
	public InventoryDao getInventory() {
		return inventory;
	}

	@Override
	public NavigatorDao getNavigator() {
		return navigator;
	}

	@Override
	public MessengerDao getMessenger() {
		return messenger;
	}
}

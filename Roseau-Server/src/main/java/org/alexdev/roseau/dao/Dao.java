package org.alexdev.roseau.dao;

public interface Dao {

	public boolean connect();
	public PlayerDao getPlayer();
	public boolean isConnected();
	public RoomDao getRoom();
	public ItemDao getItem();
	public CatalogueDao getCatalogue();
	public InventoryDao getInventory();
	public NavigatorDao getNavigator();
	public MessengerDao getMessenger();
}

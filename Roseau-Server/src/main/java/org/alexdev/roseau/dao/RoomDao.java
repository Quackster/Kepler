package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.RoomModel;

public interface RoomDao {

	public List<Room> getPublicRooms(boolean storeInMemory);
	public List<Room> getPlayerRooms(PlayerDetails details, boolean storeInMemory);
	public Room getRoom(int roomID, boolean storeInMemory);
	public List<Integer> getRoomRights(int roomID);
	public void updateRoom(Room room);
	public RoomModel getModel(String model);
	public void deleteRoom(Room room);
	public Room createRoom(Player player, String name, String description, String model, int state, boolean showOwnerName);
	public List<Integer> setRoomConnections(Room room);
	public List<Bot> getBots(Room room, int roomID);
	public void saveRoomRights(int roomID, List<Integer> rights);
	public Room saveChatlog(Player chatter, int roomID, String chatType, String message);
	public List<Integer> getPublicRoomIDs();
	public List<Room> getLatestPlayerRooms(List<Integer> blacklist, int multiplier);
	
}

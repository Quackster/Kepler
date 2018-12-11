package org.alexdev.roseau.game.room;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_ADD;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_REMOVE;
import org.alexdev.roseau.messages.outgoing.ADDWALLITEM;
import org.alexdev.roseau.messages.outgoing.REMOVEWALLITEM;
import org.alexdev.roseau.messages.outgoing.STUFFDATAUPDATE;
import org.alexdev.roseau.util.Util;

import com.google.common.collect.Lists;

public class RoomMapping {

	private Room room;

	private RoomTile[][] tiles;
	private RoomConnection[][] connections = null;
	private List<Integer>  roomConnections;

	private int mapSizeX;

	private int mapSizeY;

	public RoomMapping(Room room) {
		this.room = room;
		this.roomConnections = Lists.newArrayList();
	}

	public void regenerateCollisionMaps() {

		this.mapSizeX = this.room.getData().getModel().getMapSizeX();
		this.mapSizeY = this.room.getData().getModel().getMapSizeY();

		if (this.connections == null) {
			this.connections = new RoomConnection[mapSizeX][mapSizeY];

			if (this.room.getData().getRoomType() == RoomType.PUBLIC) {
				this.roomConnections = Roseau.getDao().getRoom().setRoomConnections(this.room);
			}
		}

		this.tiles = new RoomTile[this.mapSizeX][this.mapSizeY];

		for (int y = 0; y < mapSizeY; y++) {
			for (int x = 0; x < mapSizeX; x++) {
				this.tiles[x][y] = new RoomTile(this.room);
				this.tiles[x][y].setHeight(this.room.getData().getModel().getHeight(x, y));
			}
		}

		ConcurrentHashMap<Integer, Item> items;

		if (room.getData().getRoomType() == RoomType.PUBLIC) { 
			items = room.getPassiveObjects();
		} else {
			items = room.getItems();
		}

		for (Item item : items.values()) {

			if (item == null) {
				continue;
			}

			if (item.getDefinition().getBehaviour().isOnWall()) {
				continue;
			}
			
			if (item.getDefinition().getBehaviour().isCanStandOnTop()) {
				continue;
			}
			
			if (item.getDefinition().getSprite().equals("bed_budget_one") ||
				item.getDefinition().getSprite().equals("bed_budgetb_one")) {
				item.delete();
				return;
			}

			double stacked_height = item.getDefinition().getHeight();
			
			this.checkHighestItem(item, item.getPosition().getX(), item.getPosition().getY());

			RoomTile roomTile = this.getTile(item.getPosition().getX(), item.getPosition().getY());

			if (roomTile == null) {
				//continue;
			}

			roomTile.getItems().add(item);
			roomTile.setHeight(roomTile.getHeight() + stacked_height);

			for (Position tile : item.getAffectedTiles()) {

				if (this.checkHighestItem(item, tile.getX(), tile.getY())) {

					RoomTile affectedRoomTile = this.getTile(tile.getX(), tile.getY());

					if (affectedRoomTile != null) {
						affectedRoomTile.getItems().add(item);
						affectedRoomTile.setHeight(affectedRoomTile.getHeight() + stacked_height);
					}
				}
			}
		}
	}

	private boolean checkHighestItem(Item item, int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return false;
		}

		Item highest_item = this.tiles[x][y].getHighestItem();

		if (highest_item == null) {
			this.tiles[x][y].setHighestItem(item);
		}
		else {
			if (item.getPosition().getZ() > highest_item.getPosition().getZ()) {
				this.tiles[x][y].setHighestItem(item);
			}
		}

		return true;
	}

	public boolean isValidTile(Entity entity, int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return false;
		}

		RoomTile tile = this.tiles[x][y];

		if (tile.hasOverrideLock()) {
			return false;
		}

		Item item = tile.getHighestItem();
		boolean tile_valid = (this.room.getData().getModel().isBlocked(x, y) == false);

		if (item != null) {
			tile_valid = item.canWalk(entity, new Position(x, y));
		}

		// This is returned when there's no items found, it will
		// just check the default model if the tile is valid

		Player avoid = this.getPlayer(x, y);

		if (avoid != null) {
			if (entity != avoid) {
				tile_valid = false;
			}
		}
		
		Player goal = this.isValidGoal(x, y);
		
		if (goal != null) {
			if (entity != goal) {
				tile_valid = false;
			}
		}
		

		return tile_valid;
	}

	public List<Player> getNearbyPlayers(Entity entity, Position start, int distance) {

		List<Player> players = Lists.newArrayList();

		for (Player roomPlayer : room.getPlayers()) {

			if (roomPlayer == entity) {
				continue;
			}

			Position currentPoint = start;
			Position playerPoint = roomPlayer.getRoomUser().getPosition();

			if (currentPoint.getDistance(playerPoint) <= distance) {
				players.add(roomPlayer);
			}
		}

		return players;
	}

	public List<Bot> getNearbyBots(Entity entity, Position start, int distance) {

		List<Bot> players = Lists.newArrayList();

		for (Entity roomPlayer : room.getEntities(EntityType.BOT)) {

			if (roomPlayer == entity) {
				continue;
			}

			Position currentPoint = start;
			Position playerPoint = roomPlayer.getRoomUser().getPosition();

			if (currentPoint.getDistance(playerPoint) <= distance) {
				players.add((Bot)roomPlayer);
			}
		}

		return players;
	}

	public void addItem(Item item) {

		
		item.setRoomID(this.room.getData().getID());
		
		this.room.getItems().put(item.getID(), item);

		if (item.getDefinition().getBehaviour().isOnFloor()) {
			this.handleItemAdjustment(item, false);
			this.regenerateCollisionMaps();
		}

		if (item.getDefinition().getDataClass().equals("DIR")) {
			item.setCustomData(String.valueOf(item.getPosition().getRotation()));
		}


		if (item.getDefinition().getBehaviour().isOnFloor()) {
			this.room.send(new ACTIVEOBJECT_ADD(item));
		}

		if (item.getDefinition().getBehaviour().isOnWall()) {
			this.room.send(new ADDWALLITEM(item));
		}


		//this.room.send(new ACTIVEOBJECT_ADD(item));
		item.save();
	}

	public void updateItemPosition(Item item, boolean rotation_only) {

		item.setCustomData("");

		if (item.getDefinition().getBehaviour().isOnFloor()) {
			this.handleItemAdjustment(item, rotation_only);
			this.regenerateCollisionMaps();
		}

		if (item.getDefinition().getDataClass().equals("DIR")) {

			int rotation = Util.getRandom().nextInt(7);

			item.getPosition().setRotation(rotation);
			item.setCustomData(String.valueOf(rotation));
		}

		item.updateStatus();	    
		item.save();
	}


	public void removeItem(Item item) {

		item.setRoomID(0);
		item.save();

		if (item.getDefinition().getBehaviour().isOnFloor()) {
			item.setCustomData("");
			this.room.send(new ACTIVEOBJECT_REMOVE(item));
		}

		if (item.getDefinition().getBehaviour().isOnWall()) {
			this.room.send(new REMOVEWALLITEM(item.getID()));
		}

		this.room.getItems().remove(item.getID());
		this.regenerateCollisionMaps();

	}

	private void handleItemAdjustment(Item item, boolean rotation_only) {

		if (rotation_only) {
			for (Item items : this.getTile(item.getPosition().getX(), item.getPosition().getY()).getItems()) {
				if (items != item && items.getPosition().getZ() >= item.getPosition().getZ()) {
					items.getPosition().setRotation(item.getPosition().getRotation());
					items.updateStatus();
				}
			}
		}
		else {
			
			// Don't make rugs stackable on top of other objects
			if (item.getDefinition().getBehaviour().isCanStandOnTop()) {	
				item.getPosition().setZ(this.room.getData().getModel().getHeight(item.getPosition().getX(), item.getPosition().getY()));
			} else {
				item.getPosition().setZ(this.getStackHeight(item.getPosition().getX(), item.getPosition().getY()));
					
			}
		}

		item.updateEntities();
	}

	public void updateItem(Player player, Item item, String dataClass, String customData) {

		if (item == null) {
			return;

		}

		if (item.getDefinition().getDataClass().equals("NULL")) {
			return;
		}

		if (item.getDefinition().getDataClass().equals("DIR")) {
			return;
		}

		if (item.getDefinition().getBehaviour().getRequiresRightsForInteraction()) {
			if (this.room.hasRights(player, false)) {
				return;
			}
		}

		if (item.getDefinition().getDataClass().equals("DOOROPEN") && !customData.equals("TRUE")) {
			customData = "FALSE";
		} else if ((item.getDefinition().getDataClass().equals("SWITCHON") || item.getDefinition().getDataClass().equals("FIREON")) && !customData.equals("ON")) {
			customData = "OFF";
		} else if (item.getDefinition().getDataClass().equals("STATUS") && !customData.equals("O")) { 
			customData = "C";
		}

		this.room.send(new STUFFDATAUPDATE(item, customData));
		item.setCustomData(customData);

		if (!item.getDefinition().getDataClass().equals("DOOROPEN")) {
			item.save();
		}
	}


	private double getStackHeight(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return 0;
		}

		return this.tiles[x][y].getHeight();
	}

	public RoomTile getTile(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return null;
		}

		return this.tiles[x][y];
	}

	public Item getHighestItem(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return null;
		}

		return this.tiles[x][y].getHighestItem();
	}

	public Player getPlayer(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return null;
		}

		for (Player player : this.room.getPlayers()) {

			if (player.getRoomUser().getPosition().getX() == x &&
					player.getRoomUser().getPosition().getY() == y) {

				return player;
			}
		}

		return null;
	}
	
	public Player isValidGoal(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return null;
		}

		for (Player player : this.room.getPlayers()) {

			if (player.getRoomUser().getGoal() == null) {
				continue;
			}
			
			if (player.getRoomUser().getGoal().getX() == x &&
					player.getRoomUser().getGoal().getY() == y) {

				return player;
			}
		}

		return null;
	}


	public RoomConnection[][] getConnections() {
		return connections;
	}


	public RoomConnection getRoomConnection(int x, int y) {

		if (this.room.getData().getModel().invalidXYCoords(x, y)) {
			return null;
		}

		return this.connections[x][y];
	}

	public List<Integer> getRoomWalkwayIDs() {
		return roomConnections;
	}

	public void setRoomConnections(List<Integer> roomConnections) {
		this.roomConnections = roomConnections;
	}

}

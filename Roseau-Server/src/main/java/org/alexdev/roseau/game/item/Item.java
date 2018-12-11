package org.alexdev.roseau.game.item;

import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.interactors.BlankInteractor;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.item.interactors.TeleporterInteractor;
import org.alexdev.roseau.game.item.interactors.furniture.BedInteractor;
import org.alexdev.roseau.game.item.interactors.furniture.ChairInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolChangeBoothInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolLadderInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolLiftInteractor;
import org.alexdev.roseau.game.item.interactors.pool.PoolQueueInteractor;
import org.alexdev.roseau.game.pathfinder.AffectedTile;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomTile;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.ACTIVEOBJECT_UPDATE;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.messages.outgoing.UPDATEWALLITEM;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

import com.google.common.collect.Lists;

public class Item implements SerializableObject {
	private int ID;
	private int roomID;
	private int targetTeleporterID = 0;

	private Position position;

	private String itemData;
	private String customData;
	private String wallPosition;

	private int definitionID;
	private int ownerID;
	private String currentProgram = null;
	private Interaction interaction;

	public Item(int ID, int roomID, int ownerID, String x, int y, double z, int rotation, int definitionID, String itemData, String customData) {
		this.ID = ID;
		this.roomID = roomID;
		this.ownerID = ownerID;

		this.definitionID = definitionID;

		this.itemData = itemData;	
		this.customData = customData;

		if (this.getDefinition().getBehaviour().isOnWall()) {
			this.wallPosition = x;
			this.position = new Position(-1, -1);
		} else {
			this.position = new Position(Integer.valueOf(x), y, z);
			this.position.setRotation(rotation);
		}
		
		this.setTeleporterID();
		this.setInteractionType();
	}
	
	private void setInteractionType() {
		if (this.getDefinition().getBehaviour().isCanSitOnTop()) {
			this.interaction = new ChairInteractor(this);
		} else if (this.getDefinition().getBehaviour().isCanLayOnTop()) {
			this.interaction = new BedInteractor(this);
		} else if (this.getDefinition().getBehaviour().isTeleporter()) {
			this.interaction = new TeleporterInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolBooth")) {
			this.interaction = new PoolChangeBoothInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolQueue")) {
			this.interaction = new PoolQueueInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolLift")) {
			this.interaction = new PoolLiftInteractor(this);
		} else if (this.getDefinition().getSprite().equals("poolEnter")) {
			this.interaction = new PoolLadderInteractor(this, true);
		} else if (this.getDefinition().getSprite().equals("poolExit")) {
			this.interaction = new PoolLadderInteractor(this, false);
		}
		
		if (this.interaction == null) {
			this.interaction = new BlankInteractor(this);
		}
	}

	private void setTeleporterID() {
		if (this.getDefinition().getBehaviour().isTeleporter()) {
			try {
				this.targetTeleporterID = Integer.valueOf(this.customData);
			} catch (NumberFormatException e) {  }
		}
	}

	@Override
	public void serialise(Response response) {
		if (this.getDefinition().getBehaviour().isInvisible()) {
			return;
		}

		if (this.getDefinition().getBehaviour().isPassiveObject()) {

			response.appendNewArgument(Integer.toString(this.ID));
			response.appendArgument(this.getDefinition().getSprite());
			response.appendArgument(Integer.toString(this.position.getX()));
			response.appendArgument(Integer.toString(this.position.getY()));
			response.appendArgument(Integer.toString((int)this.position.getZ()));
			response.appendArgument(Integer.toString(this.position.getRotation()));
			return;
		}

		if (this.getDefinition().getBehaviour().isOnFloor()) {
			response.appendNewArgument(this.getPadding());
			response.append(Integer.toString(this.getID()));
			response.appendArgument(this.getDefinition().getSprite(), ',');
			response.appendArgument(Integer.toString(this.position.getX()));
			response.appendArgument(Integer.toString(this.position.getY()));
			response.appendArgument(Integer.toString(this.getDefinition().getLength()));
			response.appendArgument(Integer.toString(this.getDefinition().getWidth()));
			response.appendArgument(Integer.toString(this.position.getRotation()));
			response.appendArgument(Double.toString((int)this.position.getZ()));
			response.appendArgument(this.getDefinition().getColor());
			response.appendArgument(this.getDefinition().getName(), '/');
			response.appendArgument(this.getDefinition().getDescription(), '/');

			if (this.targetTeleporterID > 0) {
				response.appendArgument("extr=", '/');
				response.appendArgument(Integer.toString(this.targetTeleporterID), '/');
			}
			
			if (!this.getDefinition().getSprite().equals("fireplace_polyfon")) {

			if (this.customData != null && this.getDefinition().getDataClass() != null) {
				response.appendArgument(this.getDefinition().getDataClass(), '/');
				response.appendArgument(this.customData, '/');
			}
			} else {
				response.appendArgument(this.getDefinition().getDataClass(), '/');
				response.appendArgument(this.customData, '/');				
			}

			return;
		} 

		if (this.getDefinition().getBehaviour().isOnWall()) {
			response.append(Integer.toString(this.ID));
			response.appendArgument(this.getDefinition().getSprite(), ';');
			response.appendArgument("Alex", ';');
			response.appendArgument(this.wallPosition, ';');
			response.appendNewArgument(this.customData);
			return;
		}
	}

	public List<Position> getAffectedTiles() {
		return AffectedTile.getAffectedTilesAt(
				this.getDefinition().getLength(), 
				this.getDefinition().getWidth(), 
				this.position.getX(), 
				this.position.getY(),
				this.position.getRotation());
	}

	public boolean canWalk(Entity player, Position position) {
		boolean tile_valid = false;

		if (this.getDefinition().getBehaviour().isCanSitOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isCanStandOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isCanLayOnTop()) {
			tile_valid = true;
		}

		if (this.getDefinition().getBehaviour().isTeleporter()) {
			if (this.getDefinition().getDataClass().equals("DOOROPEN")) {
				if (this.customData.equals("TRUE")) {
					tile_valid = true;
				}
			}
		}

		if (this.getDefinition().getSprite().equals("poolBooth")) {
			tile_valid = true;
		}

		if (this.getDefinition().getSprite().equals("stair")) {
		    tile_valid = true;
		}
		
		if (this.getDefinition().getSprite().equals("poolQueue")) {
			tile_valid = true;
		}
		
		if (this.getDefinition().getSprite().equals("poolLift")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		if (this.getDefinition().getSprite().equals("poolEnter")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		if (this.getDefinition().getSprite().equals("poolExit")) {
			tile_valid = player.getDetails().getPoolFigure().length() > 0;
		}

		return tile_valid; 
	}


	public RoomTile getTileInstance() {
		return this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY());
	}

	public void lockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(true);

		try {
			if (this.customData != null) {
				for (String coordinate : this.customData.split(" ")) {
					int x = Integer.valueOf(coordinate.split(",")[0]);
					int y = Integer.valueOf(coordinate.split(",")[1]);

					this.getRoom().getMapping().getTile(x, y).setOverrideLock(true);
				}
			}
		} catch (NumberFormatException e) {	}
	}

	public void unlockTiles() {
		this.getRoom().getMapping().getTile(this.position.getX(), this.position.getY()).setOverrideLock(false);

		try {
			if (this.customData != null) {
				for (String coordinate : this.customData.split(" ")) {
					int x = Integer.valueOf(coordinate.split(",")[0]);
					int y = Integer.valueOf(coordinate.split(",")[1]);

					this.getRoom().getMapping().getTile(x, y).setOverrideLock(false);
				}
			}
		} catch (NumberFormatException e) {	}
	}

	public void updateEntities() {
		List<Entity> affected_players = Lists.newArrayList();;

		Room room = this.getRoom();

		if (room == null) {
			return;
		}

		for (Entity entity : this.getRoom().getEntities()) {

			if (entity.getRoomUser().getCurrentItem() != null) {
				if (entity.getRoomUser().getCurrentItem().getID() == this.ID) {

					if (!hasEntityCollision(entity.getRoomUser().getPosition().getX(), entity.getRoomUser().getPosition().getY())) {
						entity.getRoomUser().setCurrentItem(null);
					}

					affected_players.add(entity);
				}
			}

			// Moved item inside a player
			else if (hasEntityCollision(entity.getRoomUser().getPosition().getX(), entity.getRoomUser().getPosition().getY())) {
				entity.getRoomUser().setCurrentItem(this);
				affected_players.add(entity);
			}
		}

		for (Entity entity : affected_players) {
			entity.getRoomUser().currentItemTrigger();
		}
	}

	private boolean hasEntityCollision(int x, int y) {
		if (this.position.getX() == x && this.position.getY() == y) {
			return true;
		}
		else {
			for (Position tile : this.getAffectedTiles()) {
				if (tile.getX() == x && tile.getY() == y) {
					return true;
				}
			}
		}

		return false;

	}

	public OutgoingMessageComposer getCurrentProgram() {
		if (this.currentProgram != null) {
			return new SHOWPROGRAM(new String[] { this.itemData, this.currentProgram });
		} else {
			return null;
		}
	}

	
	public void showProgram(String data) {
		if (this.getRoom() == null) {
			return;
		}

		this.currentProgram = data;
		
		OutgoingMessageComposer composer = new SHOWPROGRAM(new String[] { this.itemData, data });
		this.getRoom().send(composer);
	}

	public void updateStatus() {
		if (this.getRoom() == null) {
			return;
		}

		if (this.getDefinition().getBehaviour().isOnFloor()) {
			this.getRoom().send(new ACTIVEOBJECT_UPDATE(this)); 
		} else {
			this.getRoom().send(new UPDATEWALLITEM(this)); 
		}
	}

	public void save() {
		Roseau.getDao().getItem().saveItem(this);
	}

	public void delete() {
		Roseau.getDao().getItem().deleteItem(this.ID);
	}

	public String getPadding() {
		String sprite = this.getDefinition().getSprite();
		return String.format("%0" + sprite.length() + "d", 0);

	}

	public int getID() {
		return this.ID;
	}

	public ItemDefinition getDefinition() {
		return Roseau.getGame().getItemManager().getDefinition(this.definitionID);
	}

	public Room getRoom() {

		return Roseau.getGame().getRoomManager().getRoomByID(roomID);
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		
		if (customData.length() > 400) {
			customData = customData.substring(0, 400); 
		}
		
		this.customData = customData;
		this.setTeleporterID();
	}

	public String getWallPosition() {
		return wallPosition;
	}

	public void setWallPosition(String wallPosition) {
		this.wallPosition = wallPosition;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getTargetTeleporterID() {
		return targetTeleporterID;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public void setTargetTeleporterID(int targetTeleporterID) {
		this.targetTeleporterID = targetTeleporterID;
	}

	public Position getPosition() {
		return this.position;
	}

	public int getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

}

package org.alexdev.roseau.game.player;

import java.util.List;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.inventory.Inventory;
import org.alexdev.roseau.game.messenger.Messenger;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.IPlayerNetwork;

public class Player implements Entity {

	private String machineID;
	private PlayerDetails details;
	private IPlayerNetwork network;
	private RoomUser roomEntity;
	private Inventory inventory;
	private Room lastCreatedRoom;
	private Messenger messenger;

	private boolean sendHotelAlert;

	private long orderInfoProtection;

	public Player(IPlayerNetwork network) {
		this.network = network;
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);
		this.inventory = new Inventory(this);
		this.messenger = new Messenger(this);
		this.lastCreatedRoom = null;
		this.sendHotelAlert = true;
	}

	public void login(boolean initalAuthentication) {

		Roseau.getDao().getPlayer().updateLastLogin(this.details);

		if (initalAuthentication) {
			
			
		}
	}

	public boolean hasPermission(String permission) {
		return Roseau.getGame().getPlayerManager().hasPermission(this.details.getRank(), permission);
	}


	public void sendAlert(String string) {
		this.send(new SYSTEMBROADCAST(string));
	}
	
	public Player getMainServerPlayer() {

		try {
			return Roseau.getGame()
					.getPlayerManager()
					.getPlayers()
					.values().stream()
					.filter(s -> s.getDetails().getID() == this.details.getID() && 
					s.getNetwork().getServerPort() == (Roseau.getServerPort())).findFirst().get();

		} catch (Exception e) {
			return null;
		}
	}


	public Player getPrivateRoomPlayer() {

		try {
			return Roseau.getGame()
					.getPlayerManager()
					.getPlayers()
					.values().stream()
					.filter(s -> s.getDetails().getID() == this.details.getID() && 
					s.getNetwork().getServerPort() == (Roseau.getPrivateServerPort())).findFirst().get();

		} catch (Exception e) {
			return null;
		}
	}

	public Player getPublicRoomPlayer() {

		try {
			return Roseau.getGame()
					.getPlayerManager()
					.getPlayers()
					.values().stream()
					.filter(s -> s.getDetails().getID() == this.details.getID() && 
					s.getNetwork().getServerPort() != (Roseau.getPrivateServerPort()) &&
					s.getNetwork().getServerPort() != Roseau.getServerPort()).findFirst().get();

		} catch (Exception e) {
			return null;
		}
	}


	public void dispose() {

		if (this.network.getServerPort() == Roseau.getServerPort()) {

			for (Room room : this.getRooms()) {
				room.dispose();
			}

			this.messenger.dispose();
			this.inventory.dispose();

		} else {
			
			if (this.roomEntity != null) {
				if (this.roomEntity.getRoom() != null) {
					this.roomEntity.getRoom().leaveRoom(this, false);
				}
			}

		}
	}

	public void send(OutgoingMessageComposer response) {
		this.network.send(response);
	}

	public void kick() {
		this.network.close();
	}

	public void kickAllConnections() {

		try {

			List<Player> players = Roseau.getGame().getPlayerManager().getPlayers().values().stream().filter(s -> s.getDetails().getID() == this.details.getID()).collect(Collectors.toList());

			for (Player player : players) {
				player.kick();
			}

		} catch (Exception e) {
			return;
		}
	}

	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

	public String getMachineID() {
		return machineID;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	public IPlayerNetwork getNetwork() {
		return network;
	}

	public List<Room> getRooms() {
		return Roseau.getDao().getRoom().getPlayerRooms(this.details, true);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public RoomUser getRoomUser() {
		return this.roomEntity;
	}

	public long getOrderInfoProtection() {
		return orderInfoProtection;
	}

	public void setOrderInfoProtection(long orderInfoProtection) {
		this.orderInfoProtection = orderInfoProtection;
	}

	public Room getLastCreatedRoom() {
		return lastCreatedRoom;
	}

	public void setLastCreatedRoom(Room lastCreatedRoom) {
		this.lastCreatedRoom = lastCreatedRoom;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean canSendHotelAlert() {
		return sendHotelAlert;
	}

	public void setSendHotelAlert(boolean sendHotelAlert) {
		this.sendHotelAlert = sendHotelAlert;
	}

	public Messenger getMessenger() {
		return this.messenger;
	}


}

package org.alexdev.roseau.game.room.entity;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.RoomConnection;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.CHAT;
import org.alexdev.roseau.messages.outgoing.PH_NOTICKETS;
import org.alexdev.roseau.messages.outgoing.STATUS;
import org.alexdev.roseau.messages.outgoing.USERS;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.BlankInteractor;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.model.Position;
import com.google.common.collect.Lists;

public class RoomUser {

	private int danceID;
	private int timeUntilNextDrink;

	private Position position;
	private Position goal;
	private Position next = null;

	private ConcurrentHashMap<String, RoomUserStatus> statuses;
	private LinkedList<Position> path;

	private Room room;

	private boolean isWalking = false;
	private boolean needsUpdate = false;
	private boolean canWalk = true;

	private Entity entity;
	private int lookResetTime;
	private Item current_item;

	private int afkTimer;

	private boolean kickWhenStop;

	public RoomUser(Entity entity) {
		this.dispose();
		this.entity = entity;
	}

	public void dispose() {

		if (this.statuses != null) {
			this.statuses.clear();
		}

		if (this.path != null) {
			this.path.clear();
		}

		this.statuses = null;
		this.path = null;

		this.statuses = new ConcurrentHashMap<String, RoomUserStatus>();
		this.path = Lists.newLinkedList();

		this.position = null;
		this.goal = null;

		this.position = new Position(0, 0, 0);
		this.goal = new Position(0, 0, 0);

		this.current_item = null;

		this.needsUpdate = false;
		this.isWalking = false;

		this.danceID = 0;
		this.timeUntilNextDrink = -1;

		this.resetAfkTimer();

	}

	public void walkItemTrigger() {

		if (!(this.entity instanceof Player)) {
			return;
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		if (item == null) {
			return;
		}

		item.getInteraction().onTrigger((Player)this.entity);
	}

	public void stopWalking() {

		this.removeStatus("mv");

		this.isWalking = false;
		
		this.goal = null;
		this.next = null;

		if (!(this.entity instanceof Player)) {
			this.needsUpdate = true;
			return;
		}
		
		Player player = (Player)this.entity;

		if (this.kickWhenStop) {
			player.dispose();
			player.kick();
			return;
		}

		RoomConnection connectionRoom = this.room.getMapping().getRoomConnection(this.position.getX(), this.position.getY());

		if (connectionRoom != null) {
			Room room = Roseau.getGame().getRoomManager().getRoomByID(connectionRoom.getToID());

			if (room != null) {

				if (this.room != null) {
					this.room.leaveRoom(player, false);
				}

				player.getNetwork().setServerPort(room.getData().getServerPort());

				if (connectionRoom.getDoorPosition() != null) {
					room.loadRoom(player, connectionRoom.getDoorPosition(), connectionRoom.getDoorPosition().getRotation());
				} else {
					room.loadRoom(player);
				}

				this.setNeedUpdate(true);

				return;
			} else {
				Log.println("Tried to connect player to room ID: " + connectionRoom.getToID() + " but no room could be found.");
			}
		}

		Item item = this.room.getMapping().getHighestItem(this.position.getX(), this.position.getY());

		boolean no_current_item = false;

		if (item != null) {
			if (item.canWalk(this.entity, position)) {
				this.current_item = item;
				this.currentItemTrigger();
			} else {
				no_current_item = true;
			}
		} else {
			no_current_item = true;
		}

		if (no_current_item) {
			this.current_item = null;
		}

	}

	public void currentItemTrigger() {

		if (this.current_item == null) {
			new BlankInteractor(null).onStoppedWalking((Player) this.entity);
		} else {
			this.current_item.getInteraction().onStoppedWalking((Player) this.entity);
		}

		this.needsUpdate = true;

	}

	public boolean walkTo(int x, int y) {

		if (this.room == null) {
			return false;
		}

		if (!this.canWalk) {
			return false;
		}

		if (this.kickWhenStop) {
			this.kickWhenStop = false;
		}

		if (GameVariables.DEBUG_ENABLE) {
			Item item = this.room.getMapping().getHighestItem(x, y);
			if (item != null) {
				Log.println(item.getDefinition().getSprite() + " - " + item.getDefinition().getID() + " - " + item.getCustomData());
			}
		}

		this.resetAfkTimer();

		if (!this.room.getMapping().isValidTile(this.entity, x, y)) {
			return false;
		}

		Item item = this.room.getMapping().getHighestItem(x, y);
		if (item != null) {
			if (item.getDefinition().getSprite().equals("poolLift") || item.getDefinition().getSprite().equals("poolQueue")) {

				if (!(entity.getDetails().getTickets() > 0)) {
					if (entity.getType() == EntityType.PLAYER) {
						((Player)entity).send(new PH_NOTICKETS());
					}

					return false;
				}
			}
		}

		if (this.position.isMatch(new Position(x, y))) {
			return false;
		}

		this.goal = new Position(x, y);
		LinkedList<Position> path = Pathfinder.makePath(this.entity);

		if (path == null) {
			return false;
		}

		if (path.size() == 0) {
			return false;
		}

		this.path = path;
		this.isWalking = true;

		return true;
	}

	public void chat(String talkMessage) {
		this.room.send(new CHAT("CHAT", this.entity.getDetails().getName(), talkMessage));
	}

	public void chat(String header, String talkMessage) {
		this.room.send(new CHAT(header, this.entity.getDetails().getName(), talkMessage));
	}


	public void chat(final String response, final int delay) {

		final Room room = this.room;
		final PlayerDetails details = this.entity.getDetails();

		Runnable task = new Runnable() {
			@Override
			public void run() {
				room.send(new CHAT("CHAT", details.getName(), response));
			}
		};

		Roseau.getGame().getScheduler().schedule(task, GameVariables.BOT_RESPONSE_DELAY, TimeUnit.MILLISECONDS);

	}

	/*
	 * Rotation calculator taken from Blunk v5
	 */
	public void lookTowards(Position look) {

		if (this.isWalking) {
			return;
		}

		int diff = this.getPosition().getRotation() - Rotation.calculateDirection(this.position.getX(), this.position.getY(), look.getX(), look.getY());


		if ((this.getPosition().getRotation() % 2) == 0) {

			if (diff > 0) {
				this.position.setHeadRotation(this.getPosition().getRotation() - 1);
			} else if (diff < 0) {
				this.position.setHeadRotation(this.getPosition().getRotation() + 1);
			} else {
				this.position.setHeadRotation(this.getPosition().getRotation());
			}
		}

		this.needsUpdate = true;
	}

	public void removeStatus(String key) {
		this.statuses.remove(key);
	}

	public void setStatus(String key, String value, boolean infinite, long duration, boolean sendUpdate) {

		if (key.equals("carryd")) {
			this.timeUntilNextDrink = GameVariables.CARRY_DRINK_INTERVAL;
		}

		this.setStatus(key, value, infinite, duration);

		if (sendUpdate) {
			this.needsUpdate = true;
		}
	}

	public void setStatus(String key, String value, boolean infinite, long duration) {

		if (this.containsStatus(key)) {
			this.removeStatus(key);
		}

		this.statuses.put(key, new RoomUserStatus(key, value, infinite, duration));
	}

	public void forceStopWalking() {

		this.removeStatus("mv");
		this.path.clear();
	}

	public boolean containsStatus(String string) {
		return this.statuses.containsKey(string);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getGoal() {
		return goal;
	}

	public void setGoal(Position goal) {
		this.goal = goal;
	}

	public void sendStatusComposer() {
		this.room.send(new STATUS(this.entity));
	}

	public STATUS getStatusComposer() {
		return new STATUS(this.entity);
	}

	public USERS getUsersComposer() {
		return new USERS(this.entity);
	}

	public void updateStatus() {
		this.room.send(new STATUS(this.entity));
	}

	public boolean isDancing() {
		return this.danceID != 0;
	}

	public int getDanceID() {
		return danceID;
	}

	public void setDanceID(int danceID) {
		this.danceID = danceID;
	}

	public ConcurrentHashMap<String, RoomUserStatus> getStatuses() {
		return statuses;
	}

	public LinkedList<Position> getPath() {
		return path;
	}


	public void setPath(LinkedList<Position> path) {

		if (this.path != null) {
			this.path.clear();
		}

		this.path = path;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void setNeedUpdate(boolean needsWalkUpdate) {
		this.needsUpdate = needsWalkUpdate;
	}

	public Room getRoom() {
		return room;
	}

	public int getRoomID() {
		return (room == null ? 0 : room.getData().getID());
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public RoomModel getModel() {
		return room.getData().getModel();
	}
	public boolean isWalking() {
		return isWalking;
	}

	public void setWalking(boolean isWalking) {
		this.isWalking = isWalking;
	}

	public Entity getEntity() {
		return entity;
	}

	public Position getNext() {
		return next;
	}

	public void setNext(Position next) {
		this.next = next;
	}

	public void setCanWalk(boolean flag) {
		this.canWalk = flag;
	}

	public boolean canWalk() {
		return this.canWalk;
	}

	public int getTimeUntilNextDrink() {
		return timeUntilNextDrink;
	}

	public void setTimeUntilNextDrink(int timeUntilNextDrink) {
		this.timeUntilNextDrink = timeUntilNextDrink;
	}

	public int getLookResetTime() {
		return lookResetTime;
	}

	public void setLookResetTime(int lookResetTime) {
		this.lookResetTime = lookResetTime;
	}

	public Item getCurrentItem() {
		return current_item;
	}

	public void setCurrentItem(Item currentitem) {
		this.current_item = currentitem;
	}

	public boolean isKickWhenStop() {
		return kickWhenStop;
	}

	public void setKickWhenStop(boolean kickWhenStop) {
		this.kickWhenStop = kickWhenStop;
	}

	public int getAfkTimer() {
		return afkTimer;
	}

	public void setAfkTimer(int afkTimer) {
		this.afkTimer = afkTimer;
	}

	public void resetAfkTimer() {
		this.afkTimer = GameVariables.AFK_ROOM_KICK;
	}


}

package org.alexdev.roseau.game.messenger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.outgoing.BUDDYADDREQUESTS;
import org.alexdev.roseau.messages.outgoing.BUDDYLIST;

public class Messenger {
	private boolean initalised;
	private Player player;

	private List<MessengerUser> friends;
	private List<MessengerUser> requests;

	public Messenger(Player player) {
		this.player = player;
		this.initalised = false;
	}

	public void load() {
		this.friends = Roseau.getDao().getMessenger().getFriends(player.getDetails().getID());
		this.requests = Roseau.getDao().getMessenger().getRequests(player.getDetails().getID());
	}

	public boolean hasRequest(int id) {
		return this.getRequest(id) != null;
	}

	public boolean isFriend(int id) {
		return this.getFriend(id) != null;
	}

	public MessengerUser getFriend(int id) {
		Optional<MessengerUser> friend = this.friends.stream().filter(f -> f.getDetails().getID() == id).findFirst();
		return friend.orElse(null);
	}

	public MessengerUser getRequest(int id) {
		Optional<MessengerUser> request = this.requests.stream().filter(f -> f.getDetails().getID() == id).findFirst();
		return request.orElse(null);
	}

	public void removeFriend(int id) {
		MessengerUser user = this.getFriend(id);
		this.friends.remove(user);
	}


	public void sendRequests() {
		if (!(this.requests.size() > 0)) {
			return;
		}

		this.player.send(new BUDDYADDREQUESTS(this.requests));
	}


	public void sendFriends() {
		this.sendFriends(-1);
	}

	public void sendFriends(int offlineID) {

		/*Collections.sort(this.friends, new Comparator<MessengerUser>() {
			@Override
			public int compare(MessengerUser a, MessengerUser b) {
				return Long.signum(b.getDetails().getLastOnline() - a.getDetails().getLastOnline());
			}
		});

		Collections.sort(this.friends, new Comparator<MessengerUser>() {
			@Override
			public int compare(MessengerUser a, MessengerUser b) {
				return Boolean.compare(a.isOnline(), b.isOnline());
			}
		});*/

		this.friends.sort((a, b) -> {
			int result = Long.compare(a.getDetails().getLastOnline(), b.getDetails().getLastOnline());
			if (result == 0) {

				if (a.isOnline() || b.isOnline()) {
					result = Boolean.compare(a.isOnline(), b.isOnline());
				}
			}

			return result;
		});

		//this.friends.stream().sorted(Comparator.comparing(MessengerUser::isOnline).thenComparing(MessengerUser::getDetails::getLastOnline));

		this.player.send(new BUDDYLIST(this.friends, offlineID));
	}

	public void sendStatus() {
		this.sendStatus(-1);
	}

	public void sendStatus(int offlineUserID) {
		for (MessengerUser friend : this.friends) {
			if (friend.isOnline()) {
				if (friend.getPlayer().getMessenger().hasInitalised()) {
					friend.getPlayer().getMessenger().sendFriends(offlineUserID);
				}
			}
		}
	}

	public void dispose() {
		this.sendStatus(this.player.getDetails().getID());

		if (this.friends != null) {
			this.friends.clear();
			this.friends = null;
		}

		if (this.requests != null) {
			this.requests.clear();
			this.requests = null;
		}

		this.player = null;
	}

	public List<MessengerUser> getFriends() {
		return friends;
	}

	public List<MessengerUser> getRequests() {
		return requests;
	}

	public boolean hasInitalised() {
		return initalised;
	}

	public void setInitalised(boolean initalised) {
		this.initalised = initalised;
	}

	public String getLocation() {
		String location = "";
		boolean hotelView = true;
		Room room = null;

		if (player.getPrivateRoomPlayer() != null) {
			room = player.getPrivateRoomPlayer().getRoomUser().getRoom();

			if (room != null) {
				hotelView = false;
			}
		} 

		if (player.getPublicRoomPlayer() != null) {
			room = player.getPublicRoomPlayer().getRoomUser().getRoom();

			if (room != null) {
				hotelView = false;
			}
		}

		if (!hotelView) {
			if (room.getData().getRoomType() == RoomType.PRIVATE) {
				location = "In a user flat";
			} 

			if (room.getData().getRoomType() == RoomType.PUBLIC) {
				location = room.getData().getName();
			}

		} else {
			location = "On Hotel View";
		}

		return location;
	}
}

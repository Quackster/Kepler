package org.alexdev.roseau.game.messenger;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

public class MessengerUser {

	private int userId;
	private PlayerDetails details;
	private Player player;
	
	public MessengerUser(int userId) {
		this.userId = userId;

		if (this.isOnline()) {
			this.details = this.player.getDetails();
		} else {
			this.details = Roseau.getGame().getPlayerManager().getPlayerData(this.userId);
		}
	}

	public void update() {
		this.player = Roseau.getGame().getPlayerManager().getByIDMainServer(this.userId);
	}

	public void dispose() {
		this.player = null;
		this.details = null;
	}

	public Player getPlayer() {
		return player;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	public int getUserId() {
		return userId;
	}

	public boolean isOnline() {
		this.update();
		return player != null;
	}

	public boolean inRoom() {
		return isOnline() && player.getRoomUser().getRoom() != null;
	}

}

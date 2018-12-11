package org.alexdev.roseau.game.entity;

import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.entity.RoomUser;

public interface Entity {

	public PlayerDetails getDetails();
	public RoomUser getRoomUser();
	public EntityType getType();
}

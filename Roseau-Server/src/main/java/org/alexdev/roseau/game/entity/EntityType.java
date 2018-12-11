package org.alexdev.roseau.game.entity;

import org.alexdev.roseau.game.player.Player;

public enum EntityType {

	PLAYER(Player.class),
	PET(Entity.class),
	BOT(Entity.class);
	
	Class<? extends Entity> clazz;
	
	EntityType(Class<? extends Entity> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends Entity> getClazz() {
		return clazz;
	}
	
}

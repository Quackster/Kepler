package net.h4bbo.kepler.game.entity;

import net.h4bbo.kepler.game.player.Player;

public enum EntityType {

    PLAYER(Player.class),
    PET(Entity.class),
    BOT(Entity.class);
    
    Class<? extends Entity> clazz;
    
    EntityType(Class<? extends Entity> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Entity> getEntityClass() {
        return clazz;
    }
}
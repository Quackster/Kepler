package net.h4bbo.kepler.game.entity;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.room.entities.RoomEntity;

public abstract class Entity {

    /**
     * Checks for permission.
     *
     * @param permission the permission
     * @return true, if successful
     */
    public abstract boolean hasFuse(Fuseright permission);
    
    /**
     * Gets the details.
     *
     * @return the details
     */
    public abstract PlayerDetails getDetails();

    /**
     * Gets the room user.
     *
     * @return the room user
     */
    public abstract RoomEntity getRoomUser();
    
    /**
     * Gets the type.
     *
     * @return the type
     */
    public abstract EntityType getType();
    
    /**
     * Dispose.
     */
    public abstract void dispose();
}

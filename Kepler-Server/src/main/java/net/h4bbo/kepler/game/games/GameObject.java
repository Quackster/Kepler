package net.h4bbo.kepler.game.games;

import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public abstract class GameObject {
    private int id;
    private GameObjectType gameObjectType;

    public GameObject(int id, GameObjectType gameObjectType) {
        this.id = id;
        this.gameObjectType = gameObjectType;
    }

    public abstract void serialiseObject(NettyResponse response);

    /**
     * Get the game event type for the update loop
     *
     * @return the event type
     */
    public GameObjectType getGameObjectType() {
        return gameObjectType;
    }

    /**
     * Get the id of this object
     *
     * @return the object id
     */
    public int getId() {
        return id;
    }
}

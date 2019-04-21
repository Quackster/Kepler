package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;

import java.util.ArrayList;
import java.util.List;

public abstract class SnowStormObject extends GameObject {
    public SnowStormObject(GameObjectType gameObjectType) {
        super(gameObjectType != null ? gameObjectType.getObjectId() : -1, gameObjectType);
    }

    public abstract List<Integer> getGameObjectsSyncValues();
}

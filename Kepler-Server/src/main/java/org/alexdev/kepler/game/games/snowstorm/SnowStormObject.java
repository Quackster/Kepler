package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;

import java.util.ArrayList;
import java.util.List;

public abstract class SnowStormObject extends GameObject {
    private final List<Integer> pGameObjectsSyncValues;

    public SnowStormObject(GameObjectType gameObjectType) {
        super(gameObjectType != null ? gameObjectType.getObjectId() : -1, gameObjectType);
        this.pGameObjectsSyncValues = new ArrayList<>();
    }

    public abstract void refreshSyncValues();

    public List<Integer> getGameObjectsSyncValues() {
        return pGameObjectsSyncValues;
    }
}

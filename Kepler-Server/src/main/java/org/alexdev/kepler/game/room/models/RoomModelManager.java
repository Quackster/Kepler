package org.alexdev.kepler.game.room.models;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.dao.mysql.RoomModelDao;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class RoomModelManager {
    private static RoomModelManager instance = null;
    private ConcurrentHashMap<String, RoomModel> modelMap;

    public RoomModelManager() {
        this.modelMap = RoomModelDao.getModels();
    }

    /**
     * Get the instance of {@link RoomModelManager}
     *
     * @return the instance
     */
    public static RoomModelManager getInstance() {
        if (instance == null) {
            instance = new RoomModelManager();
        }

        return instance;
    }

    /**
     * Reload the instance of {@link RoomModelManager}
     *
     * @return the instance
     */
    public static void reset() {
        instance = null;
        RoomModelManager.getInstance();
    }

    /**
     * Get the map of models.
     *
     * @return the model map
     */
    public RoomModel getModel(String modelId) {
        return modelMap.get(modelId);
    }
}

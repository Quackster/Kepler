package org.alexdev.kepler.game.recycler;

import org.alexdev.kepler.dao.mysql.RecyclerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecyclerManager {
    private static RecyclerManager instance;
    private final List<RecyclerReward> recyclerRewards;
    private boolean recyclerDisabled;
    private Logger log = LoggerFactory.getLogger(RecyclerManager.class);

    public RecyclerManager() {
        this.recyclerRewards = RecyclerDao.getRewards();
        this.recyclerRewards.forEach(recyclerReward -> {
            if (recyclerReward.getCatalogueItem() == null) {
                this.log.error("Could not locate catalogue item with sale code: " + recyclerReward.getSaleCode());
                this.recyclerDisabled = true;
            }
        });

        if (this.recyclerDisabled) {
            this.recyclerRewards.clear();
            log.warn("Recycler is disabled");
        }
    }

    /**
     * Get if the recycler is disabled.
     *
     * @return true, if disabled
     */
    public boolean isRecyclerDisabled() {
        return recyclerDisabled;
    }

    /**
     * Get the recycler rewards list.
     *
     * @return the list of rewards
     */
    public List<RecyclerReward> getRecyclerRewards() {
        return recyclerRewards;
    }

    /**
     * Get the {@link RecyclerManager} instance
     *
     * @return the item manager instance
     */
    public static RecyclerManager getInstance() {
        if (instance == null) {
            instance = new RecyclerManager();
        }

        return instance;
    }

    /**
     * Resets the item manager singleton.
     */
    public static void reset() {
        instance = null;
        RecyclerManager.getInstance();
    }
}

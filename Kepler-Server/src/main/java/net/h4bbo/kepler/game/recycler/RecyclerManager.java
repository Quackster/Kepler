package net.h4bbo.kepler.game.recycler;

import net.h4bbo.kepler.dao.mysql.RecyclerDao;
import net.h4bbo.kepler.util.config.GameConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecyclerManager {
    private static RecyclerManager instance;
    private final List<RecyclerReward> recyclerRewards;
    private final int recyclerTimeoutSeconds;
    private final int recyclerSessionLengthSeconds;
    private final int recyclerItemQuarantineSeconds;

    private boolean recyclerEnabled;
    private Logger log = LoggerFactory.getLogger(RecyclerManager.class);

    public RecyclerManager() {
        this.recyclerTimeoutSeconds = GameConfiguration.getInstance().getInteger("recycler.max.time.to.collect.seconds");
        this.recyclerSessionLengthSeconds = GameConfiguration.getInstance().getInteger("recycler.session.length.seconds");
        this.recyclerItemQuarantineSeconds = GameConfiguration.getInstance().getInteger("recycler.item.quarantine.seconds");

        /*
                config.put("recycler.timeout.seconds", "300");
        config.put("recycler.session.length.seconds", "3660");
        config.put("recycler.item.quarantine.seconds", "2592000");
         */

        this.recyclerEnabled = true;
        this.recyclerRewards = RecyclerDao.getRewards();
        this.recyclerRewards.forEach(recyclerReward -> {
            if (recyclerReward.getCatalogueItem() == null) {
                this.log.error("Could not locate catalogue item with sale code: " + recyclerReward.getSaleCode());
                this.recyclerEnabled = false;
            }
        });

        if (this.recyclerRewards.isEmpty()) {
            this.recyclerEnabled = false;
        }

        if (!this.recyclerEnabled) {
            this.recyclerRewards.clear();
            log.warn("Recycler is disabled");
        }
    }

    /**
     * Get if the recycler is disabled.
     *
     * @return true, if disabled
     */
    public boolean isRecyclerEnabled() {
        return recyclerEnabled;
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
     * Get the timeout/cooldown until we can use recycler again.
     *
     * @return the cooldown seconds
     */
    public int getRecyclerTimeoutSeconds() {
        return recyclerTimeoutSeconds;
    }

    /**
     * Get the time it takes for recycling.
     *
     * @return the seconds
     */
    public int getRecyclerSessionLengthSeconds() {
        return recyclerSessionLengthSeconds;
    }

    /**
     * Get how long you must own a furni before you can recycle it.
     *
     * @return the ownership time
     */
    public int getRecyclerItemQuarantineSeconds() {
        return recyclerItemQuarantineSeconds;
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

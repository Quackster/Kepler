package org.alexdev.kepler.game.recycler;

import org.alexdev.kepler.util.DateUtil;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RecyclerSession {
    private final int rewardId;
    private final long sessionStarted;
    private final int[] items;

    public RecyclerSession(int rewardId, long sessionStarted, String items) {
        this.rewardId = rewardId;
        this.sessionStarted = sessionStarted;
        this.items = Arrays.stream(items.split(",")).mapToInt(Integer::parseInt).toArray();;
    }

    /**
     * Get the minutes passed since the session started.
     *
     * @return the minutes passed
     */
    public int getMinutesPassed() {
        int seconds = (int) (DateUtil.getCurrentTimeSeconds() - this.sessionStarted);
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    /**
     * Get the minutes left since the session started.
     *
     * @return the minutes left
     */
    public int getMinutesLeft() {
        if (!this.isRecyclingDone()) {
            int seconds = (int) (this.getRecyclerReward().getRecyclingTimeSessions() - (DateUtil.getCurrentTimeSeconds() - this.sessionStarted));
            return (int) TimeUnit.SECONDS.toMinutes(seconds);
        }

        return 0;
    }

    public boolean isRecyclingDone() {
        return getMinutesPassed() >= TimeUnit.SECONDS.toMinutes(this.getRecyclerReward().getRecyclingTimeSessions());
    }

    public boolean hasTimeout() {
        if (this.isRecyclingDone()) {
            return getMinutesPassed() >= TimeUnit.SECONDS.toMinutes(this.getRecyclerReward().getRecyclingTimeSessions() + this.getRecyclerReward().getCollectionTimeSeconds());
        }

        return false;
    }

    /**
     * Get the reward id for this.
     *
     * @return the reward id
     */
    public int getRewardId() {
        return rewardId;
    }

    /**
     * Get the recycler reward.
     *
     * @return the recycler reward
     */
    public RecyclerReward getRecyclerReward() {
        return RecyclerManager.getInstance().getRecyclerRewards().stream().filter(reward -> reward.getId() == this.rewardId).findFirst().orElse(null);
    }

    /**
     * Get the timestamp the session started.
     *
     * @return the timestamp
     */
    public long getSessionStarted() {
        return sessionStarted;
    }
    /**
     * Get the id of the items used.
     *
     * @return the id
     */
    public int[] getItems() {
        return items;
    }
}

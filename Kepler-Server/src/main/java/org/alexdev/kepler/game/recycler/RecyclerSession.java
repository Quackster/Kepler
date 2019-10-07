package org.alexdev.kepler.game.recycler;

import org.alexdev.kepler.util.DateUtil;

import java.util.concurrent.TimeUnit;

public class RecyclerSession {
    private final int rewardId;
    private final long sessionStarted;
    private final boolean claimed;

    public RecyclerSession(int rewardId, long sessionStarted, boolean claimed) {
        this.rewardId = rewardId;
        this.sessionStarted = sessionStarted;
        this.claimed = claimed;
    }

    /**
     * Get the minutes passed since the session started.
     *
     * @return the minutes passed
     */
    public int getMinutesPassed() {
        return (int) TimeUnit.SECONDS.toMinutes(DateUtil.getCurrentTimeSeconds() - this.sessionStarted);
    }

    /**
     * Get the minutes left since the session started.
     *
     * @return the minutes left
     */
    public int getMinutesLeft() {
        if (!this.isRecyclingDone()) {
            return RecyclerManager.getInstance().getRecyclerSessionLengthSeconds() - getMinutesPassed();
        }

        return 0;
    }

    public boolean isRecyclingDone() {
        return getMinutesPassed() >= TimeUnit.SECONDS.toMinutes(RecyclerManager.getInstance().getRecyclerSessionLengthSeconds());
    }

    public boolean hasTimeout() {
        if (this.isClaimed()) {
            return getMinutesPassed() <= TimeUnit.SECONDS.toMinutes(RecyclerManager.getInstance().getRecyclerSessionLengthSeconds() + RecyclerManager.getInstance().getRecyclerTimeoutSeconds());
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
     * Get if the item has been claimed or not (for timeouts).
     *
     * @return true, if successful
     */
    public boolean isClaimed() {
        return claimed;
    }
}

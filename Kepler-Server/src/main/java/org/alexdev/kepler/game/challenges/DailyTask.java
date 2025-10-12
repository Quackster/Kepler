package org.alexdev.kepler.game.challenges;

import java.util.List;

public class DailyTask {

    private final int taskId;
    private final String dailyTaskCode;
    private final String questTypeCode;
    private final int requiredRepeats;
    private int currentRepeats;
    private int statusCode;
    private final int timestampEnd;
    private final List<DailyTaskReward> rewards;

    public DailyTask(int taskId, String dailyTaskCode, String questTypeCode, int requiredRepeats, int timestampEnd, List<DailyTaskReward> rewards) {
        this.taskId = taskId;
        this.dailyTaskCode = dailyTaskCode;
        this.questTypeCode = questTypeCode;
        this.requiredRepeats = requiredRepeats;
        this.timestampEnd = timestampEnd;
        this.rewards = rewards;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getDailyTaskCode() {
        return dailyTaskCode;
    }

    public String getQuestTypeCode() {
        return questTypeCode;
    }

    public int getRequiredRepeats() {
        return requiredRepeats;
    }

    public int getCurrentRepeats() {
        return currentRepeats;
    }

    public void setCurrentRepeats(int currentRepeats) {
        this.currentRepeats = currentRepeats;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getTimestampEnd() {
        return timestampEnd;
    }

    public List<DailyTaskReward> getRewards() {
        return rewards;
    }
}

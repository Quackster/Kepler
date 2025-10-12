package org.alexdev.kepler.game.challenges;

import java.util.List;

public class CommunityGoal {

    private int goalExpired;
    private int personalContribution;
    private int personalRank;
    private int communityTotalScore;
    private int communityHighestAchievedLevel;
    private int scoreUntilNextLevel;
    private int percentageOfCurrentLevel;
    private String goalCode;
    private int secondsLeft;
    private List<Integer> rewardLimits;
    private List<Integer> targetScores;
    private List<Integer> goalClaimed;

    public CommunityGoal(int goalExpired, int personalContribution, int personalRank, int communityTotalScore, int communityHighestAchievedLevel, int scoreUntilNextLevel, int percentageOfCurrentLevel, String goalCode, int secondsLeft, List<Integer> rewardLimits, List<Integer> targetScores, List<Integer> goalClaimed) {
        this.goalExpired = goalExpired;
        this.personalContribution = personalContribution;
        this.personalRank = personalRank;
        this.communityTotalScore = communityTotalScore;
        this.communityHighestAchievedLevel = communityHighestAchievedLevel;
        this.scoreUntilNextLevel = scoreUntilNextLevel;
        this.percentageOfCurrentLevel = percentageOfCurrentLevel;
        this.goalCode = goalCode;
        this.secondsLeft = secondsLeft;
        this.rewardLimits = rewardLimits;
        this.targetScores = targetScores;
        this.goalClaimed = goalClaimed;
    }

    public int getGoalExpired() {
        return goalExpired;
    }

    public void setGoalExpired(int goalExpired) {
        this.goalExpired = goalExpired;
    }

    public int getPersonalContribution() {
        return personalContribution;
    }

    public void setPersonalContribution(int personalContribution) {
        this.personalContribution = personalContribution;
    }

    public int getPersonalRank() {
        return personalRank;
    }

    public void setPersonalRank(int personalRank) {
        this.personalRank = personalRank;
    }

    public int getCommunityTotalScore() {
        return communityTotalScore;
    }

    public void setCommunityTotalScore(int communityTotalScore) {
        this.communityTotalScore = communityTotalScore;
    }

    public int getCommunityHighestAchievedLevel() {
        return communityHighestAchievedLevel;
    }

    public void setCommunityHighestAchievedLevel(int communityHighestAchievedLevel) {
        this.communityHighestAchievedLevel = communityHighestAchievedLevel;
    }

    public int getScoreUntilNextLevel() {
        return scoreUntilNextLevel;
    }

    public void setScoreUntilNextLevel(int scoreUntilNextLevel) {
        this.scoreUntilNextLevel = scoreUntilNextLevel;
    }

    public int getPercentageOfCurrentLevel() {
        return percentageOfCurrentLevel;
    }

    public void setPercentageOfCurrentLevel(int percentageOfCurrentLevel) {
        this.percentageOfCurrentLevel = percentageOfCurrentLevel;
    }

    public String getGoalCode() {
        return goalCode;
    }

    public void setGoalCode(String goalCode) {
        this.goalCode = goalCode;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public List<Integer> getRewardLimits() {
        return rewardLimits;
    }

    public void setRewardLimits(List<Integer> rewardLimits) {
        this.rewardLimits = rewardLimits;
    }

    public List<Integer> getTargetScores() {
        return targetScores;
    }

    public void setTargetScores(List<Integer> targetScores) {
        this.targetScores = targetScores;
    }

    public List<Integer> getGoalClaimed() {
        return goalClaimed;
    }

    public void setGoalClaimed(List<Integer> goalClaimed) {
        this.goalClaimed = goalClaimed;
    }
}

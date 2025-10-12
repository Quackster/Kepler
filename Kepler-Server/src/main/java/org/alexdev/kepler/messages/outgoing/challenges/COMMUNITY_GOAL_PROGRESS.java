package org.alexdev.kepler.messages.outgoing.challenges;

import org.alexdev.kepler.game.challenges.CommunityGoal;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.Arrays;

public class COMMUNITY_GOAL_PROGRESS extends MessageComposer {

    private static final CommunityGoal GOAL = new CommunityGoal(0, 0, 0, 6547, 3, 0, 100, "c25_habbo_c25_anniversary", 0,
            Arrays.asList(50, 100, 250),
            Arrays.asList(500, 1500, 3000),
            Arrays.asList(0, 0, 0));

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(GOAL.getGoalExpired());
        response.writeInt(GOAL.getPersonalContribution());
        response.writeInt(GOAL.getPersonalRank());
        response.writeInt(GOAL.getCommunityTotalScore());
        response.writeInt(GOAL.getCommunityHighestAchievedLevel());
        response.writeInt(GOAL.getScoreUntilNextLevel());
        response.writeInt(GOAL.getPercentageOfCurrentLevel());
        response.writeString(GOAL.getGoalCode());
        response.writeInt(GOAL.getSecondsLeft());

        response.writeInt(GOAL.getRewardLimits().size());
        for (var limit : GOAL.getRewardLimits()) {
            response.writeInt(limit);
        }

        response.writeInt(GOAL.getTargetScores().size());
        for (var score : GOAL.getTargetScores()) {
            response.writeInt(score);
        }

        response.writeInt(GOAL.getGoalClaimed().size());
        for (var claimed : GOAL.getGoalClaimed()) {
            response.writeInt(claimed);
        }
    }

    @Override
    public short getHeader() {
        return 790;
    }
}

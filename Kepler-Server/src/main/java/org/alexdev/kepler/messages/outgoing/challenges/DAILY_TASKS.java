package org.alexdev.kepler.messages.outgoing.challenges;

import org.alexdev.kepler.game.challenges.DailyTask;
import org.alexdev.kepler.game.challenges.DailyTaskReward;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DAILY_TASKS extends MessageComposer {
    private static final int TASK_END = DateUtil.getCurrentTimeSeconds() + 86400;
    private static final List<DailyTask> TASKS = new ArrayList<>() {
        {
            add(new DailyTask(1, "NPC_REGINALD", "CHALLENGE_DICES", 1, TASK_END, null));
            add(new DailyTask(2, "NPC_ZIGGY", "CHALLENGE_WHEEL", 3, TASK_END, null));
            add(new DailyTask(3, "NPC_CLAUDE", "CHALLENGE_DISHES", 10, TASK_END, null));
            add(new DailyTask(4, "NPC_GUSKIN", "CHALLENGE_QUESTION", 5, TASK_END, null));
            add(new DailyTask(5, "NPC_PICCOLO", "CHALLENGE_SUITCASES", 1, TASK_END, null));
            add(new DailyTask(6, "NPC_GLOWRISSA", "CHALLENGE_BARRELS", 10, TASK_END, null));
            add(new DailyTask(7, "NPC_PETAL", "CHALLENGE_WTC", 1, TASK_END, null));
            add(new DailyTask(8, "1742573100000", "LIDO_VOTE", 1, TASK_END, new ArrayList<>() {
                {
                    add(new DailyTaskReward(8, "10", null, 10));
                }
            }));
            add(new DailyTask(9, "1742573100000", "BATTLE_BALL_LOCK_TILES", 1, TASK_END, new ArrayList<>() {
                {
                    add(new DailyTaskReward(8, "10", null, 10));
                }
            }));
            add(new DailyTask(10, "1742573100000", "LOGIN", 1, TASK_END, new ArrayList<>() {
                {
                    add(new DailyTaskReward(8, "10", null, 10));
                }
            }));
        }
    };

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(TASKS.size());

        for (var task : TASKS) {
            int secondsLeft = task.getTimestampEnd() - DateUtil.getCurrentTimeSeconds();
            if (secondsLeft < 0) {
                secondsLeft = 0;
            }

            response.writeInt(task.getTaskId());
            response.writeString(task.getDailyTaskCode());
            response.writeString(task.getQuestTypeCode());
            response.writeInt(task.getRequiredRepeats());
            response.writeInt(task.getCurrentRepeats());
            response.writeInt(task.getStatusCode());
            response.writeInt(secondsLeft);

            if (task.getRewards() != null) {
                response.writeInt(task.getRewards().size());

                for (var reward : task.getRewards()) {
                    response.writeInt(reward.getContentType());
                    response.writeString(reward.getItemTypeId());
                    response.writeString(reward.getParameter());
                    response.writeInt(reward.getItemCount());
                }
            } else {
                response.writeInt(0);
            }
        }
    }

    @Override
    public short getHeader() {
        return 1200;
    }
}

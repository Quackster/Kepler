package net.h4bbo.kepler.messages.incoming.tutorial;

import net.h4bbo.kepler.game.achievements.AchievementManager;
import net.h4bbo.kepler.game.achievements.AchievementType;
import net.h4bbo.kepler.game.guides.GuideManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.messages.outgoing.tutorial.TUTORS_AVAILABLE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MSG_GET_TUTORS_AVAILABLE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        //int daysSinceJoined = (int) Math.floor(TimeUnit.SECONDS.toDays((long) (DateUtil.getCurrentTimeSeconds() - Math.floor(player.getDetails().getJoinDate()))));
        if (player.getStatisticManager().getIntValue(PlayerStatistic.IS_GUIDABLE) != 1) {
            GuideManager.getInstance().tryClearTutorial(player);
            return;
        }

        if (!player.getGuideManager().canUseTutorial()) {
            GuideManager.getInstance().tryClearTutorial(player);
            return;
        }

        player.getGuideManager().setGuidable(true);
        player.getGuideManager().setBlockingTutorial(true);

        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_GRADUATE, player);
        player.send(new TUTORS_AVAILABLE(1));//GuideManager.getInstance().getGuidesAvailable().size()));
    }

}

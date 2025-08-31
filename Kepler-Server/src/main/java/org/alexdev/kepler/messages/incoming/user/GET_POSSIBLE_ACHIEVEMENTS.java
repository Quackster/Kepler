package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.POSSIBLE_ACHIEVEMENTS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;

public class GET_POSSIBLE_ACHIEVEMENTS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        var possibleAchievements = new ArrayList<>(player.getAchievementManager().getPossibleAchievements());

        /*
        if (!player.getGuideManager().isGuide()) {
            possibleAchievements.removeIf(ach -> ach.getName().equals("GL"));
        }*/

        player.send(new POSSIBLE_ACHIEVEMENTS(possibleAchievements));
    }
}

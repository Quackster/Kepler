package net.h4bbo.kepler.messages.incoming.tutorial;

import net.h4bbo.kepler.game.guides.GuideManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.tutorial.ENABLE_TUTOR_SERVICE_STATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MSG_WAIT_FOR_TUTOR_INVITATIONS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getGuideManager().isGuide()) {
            return;
        }

        if (player.getGuideManager().isWaitingForInvitations()) {
            return;
        }

        if (GuideManager.getInstance().isDisabled()) {
            player.send(new ENABLE_TUTOR_SERVICE_STATUS(ENABLE_TUTOR_SERVICE_STATUS.TutorEnableStatus.SERVICE_DISABLED));
            return;
        }

        if (player.getMessenger().isFriendsLimitReached()) {
            player.send(new ENABLE_TUTOR_SERVICE_STATUS(ENABLE_TUTOR_SERVICE_STATUS.TutorEnableStatus.FRIENDSLIST_FULL));
            return;
        }

        if (player.getGuideManager().getGuiding().size() >= GuideManager.MAX_SIMULTANEOUS_GUIDING) {
            player.send(new ENABLE_TUTOR_SERVICE_STATUS(ENABLE_TUTOR_SERVICE_STATUS.TutorEnableStatus.MAX_NEWBIES));
            return;
        }


        player.getGuideManager().setWaitingForInvitations(true);
        //player.send(new INVITATION(player.getDetails()));
    }
}

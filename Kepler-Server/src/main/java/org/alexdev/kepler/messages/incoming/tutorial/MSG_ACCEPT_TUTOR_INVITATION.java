package org.alexdev.kepler.messages.incoming.tutorial;

import org.alexdev.kepler.game.guides.GuideManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.tutorial.INVITE_FOLLOW_FAILED;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

public class MSG_ACCEPT_TUTOR_INVITATION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getGuideManager().isGuide()) {
            return;
        }

        if (player.getGuideManager().getInvites().isEmpty()) {
            return;
        }

        String data = reader.readString();

        if (!StringUtils.isNumeric(data)) {
            return;
        }

        int userId = Integer.parseInt(data);

        if (!player.getGuideManager().hasInvite(userId)) {
            return;
        }

        Player newb = PlayerManager.getInstance().getPlayerById(userId);

        if (newb == null || newb.getRoomUser().getRoom() == null || !newb.getRoomUser().getRoom().isOwner(newb.getDetails().getId())) {
            player.send(new INVITE_FOLLOW_FAILED());
            return;
        }

        // TODO: Error checking
        //on handleInvitationFollowFailed me, tMsg
        //  executeMessage(#alert, "invitation_follow_failed")
        //end
        //
        //on handleInvitationCancelled me, tMsg
        //  me.getComponent().cancelInvitation()
        //end
        player.getGuideManager().removeInvite(userId);
        player.getGuideManager().setInvitedBy(newb.getDetails().getId());

        if (player.getRoomUser().getRoom() == null || player.getRoomUser().getRoom() != newb.getRoomUser().getRoom()) {
            player.getRoomUser().setAuthenticateId(newb.getRoomUser().getRoom().getId());
            newb.getRoomUser().getRoom().forward(player, false);
        } else {
            if (player.getRoomUser().getRoom() == newb.getRoomUser().getRoom()) {
                GuideManager.getInstance().tutorEnterRoom(player, newb);
            }
        }
    }
}

package org.alexdev.kepler.messages.incoming.tutorial;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MSG_CANCEL_WAIT_FOR_TUTOR_INVITATIONS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.getGuideManager().isGuide()) {
            return;
        }

        player.getGuideManager().setWaitingForInvitations(false);
        player.getGuideManager().getInvites().clear();

        // Remove your user from the newbs that invited you
        PlayerManager.getInstance().getPlayers().forEach(p -> p.getGuideManager().getInvited().removeIf(i -> i == player.getDetails().getId()));
    }
}

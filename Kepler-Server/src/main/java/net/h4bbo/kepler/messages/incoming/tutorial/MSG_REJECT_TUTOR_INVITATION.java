package net.h4bbo.kepler.messages.incoming.tutorial;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.tutorial.INVITE_CANCELLED;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

public class MSG_REJECT_TUTOR_INVITATION implements MessageEvent {
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

        if (!player.getGuideManager().getInvites().contains(userId)) {
            return;
        }

        Player newb = PlayerManager.getInstance().getPlayerById(userId);

        if (newb == null || newb.getRoomUser().getRoom() == null) {
            return;
        }

        // TODO: Error checking
        player.getGuideManager().removeInvite(userId);
        player.send(new INVITE_CANCELLED());
    }
}

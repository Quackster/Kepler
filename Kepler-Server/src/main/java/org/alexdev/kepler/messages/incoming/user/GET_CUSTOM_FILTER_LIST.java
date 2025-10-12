package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.UsersMutesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.CUSTOM_FILTER_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class GET_CUSTOM_FILTER_LIST implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        List<String> ignorePhrases = UsersMutesDao.getPhrases(player.getDetails().getId());

        for (String phrase : ignorePhrases) {
            player.getIgnoredPhrases().add(phrase);
        }

        player.send(new CUSTOM_FILTER_LIST(player.getIgnoredPhrases()));
    }
}

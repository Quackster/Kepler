package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.UsersMutesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.CUSTOM_FILTER_RESULT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REMOVE_FROM_CUSTOM_FILTER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String phrase = reader.readString();

        UsersMutesDao.removePhrase(player.getDetails().getId(), phrase);
        player.getIgnoredPhrases().remove(phrase);
        player.send(new CUSTOM_FILTER_RESULT(3));
    }
}

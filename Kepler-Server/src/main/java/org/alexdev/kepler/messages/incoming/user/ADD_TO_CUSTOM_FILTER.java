package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.UsersMutesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.CUSTOM_FILTER_RESULT;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class ADD_TO_CUSTOM_FILTER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final String phrase = reader.readString();

        if (phrase.isEmpty() || phrase.length() > 128) {
            player.send(new CUSTOM_FILTER_RESULT(0));
            return;
        }

        if (player.getIgnoredPhrases().contains(phrase)) {
            player.send(new CUSTOM_FILTER_RESULT(0));
            return;
        }

        UsersMutesDao.addPhrase(player.getDetails().getId(), phrase);
        player.getIgnoredPhrases().add(phrase);
        player.send(new CUSTOM_FILTER_RESULT(1));
    }
}

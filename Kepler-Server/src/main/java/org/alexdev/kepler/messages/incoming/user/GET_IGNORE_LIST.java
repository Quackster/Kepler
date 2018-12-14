package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.UsersMutesDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.IGNORED_LIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class GET_IGNORE_LIST implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getIgnoredList().size() > 0) {
            return;
        }

        List<Integer> ignoreList = UsersMutesDao.getMutedUsers(player.getDetails().getId());

        for (int userId : ignoreList) {
            player.getIgnoredList().add(PlayerManager.getInstance().getPlayerData(userId).getName());
        }

        player.send(new IGNORED_LIST(player.getIgnoredList()));
    }
}

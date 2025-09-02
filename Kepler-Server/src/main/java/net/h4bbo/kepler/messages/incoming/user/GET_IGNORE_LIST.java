package net.h4bbo.kepler.messages.incoming.user;

import net.h4bbo.kepler.dao.mysql.UsersMutesDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.user.IGNORED_LIST;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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

package net.h4bbo.kepler.messages.incoming.messenger;

import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.messenger.MESSENGER_SEARCH;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class FINDUSER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        String searchQuery = reader.readString();

        // if (player.getVersion() < 23) {
            int userId = MessengerDao.search(searchQuery).stream().findFirst().orElse(0);
            player.send(new MESSENGER_SEARCH(PlayerManager.getInstance().getPlayerData(userId)));
        /*} else {
            List<Integer> userList = MessengerDao.search(searchQuery.toLowerCase());

            List<PlayerDetails> friends = new ArrayList<>();
            List<PlayerDetails> others = new ArrayList<>();

            for (int userId : userList) {
                if (player.getMessenger().hasFriend(userId)) {
                    friends.add(PlayerManager.getInstance().getPlayerData(userId));
                } else {
                    others.add(PlayerManager.getInstance().getPlayerData(userId));
                }
            }

            friends.removeIf(playerDetails -> playerDetails.getId() == player.getDetails().getId());
            others.removeIf(playerDetails -> playerDetails.getId() == player.getDetails().getId());

            player.send(new MESSENGER_SEARCH(friends, others));
        }*/
    }
}

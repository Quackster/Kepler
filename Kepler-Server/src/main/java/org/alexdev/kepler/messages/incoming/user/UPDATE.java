package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class UPDATE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.isLoggedIn()) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (reader.remainingBytes().length <= 0) {
                continue;
            }

            int updateId = reader.readBase64();

            if (updateId == 4) {
                String figure = StringUtil.filterInput(reader.readString(), true);
                player.getDetails().setFigure(figure);
            }

            if (updateId == 5) {
                char sex = StringUtil.filterInput(reader.readString(), true).toCharArray()[0];

                if (sex != player.getDetails().getSex()) {
                    player.getDetails().setSex(sex);
                }
            }

            if (updateId == 6) {
                String motto = StringUtil.filterInput(reader.readString(), true);
                player.getDetails().setMotto(motto);
            }
        }

        PlayerDao.saveDetails(player.getDetails());
        PlayerDao.saveMotto(player.getDetails());

        new GET_INFO().handle(player, null);
    }
}

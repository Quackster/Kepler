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

        while (reader.remainingBytes().length > 0) {
            int updateId = reader.readBase64();
            System.out.println(updateId);

            switch (updateId) {
                case 9:
                {
                    boolean receiveNews = reader.readBytes(1)[0] == 'A';
                    player.getDetails().setReceiveNews(receiveNews);
                    break;
                }
                case 4:
                {
                    String figure = StringUtil.filterInput(reader.readString(), true);
                    player.getDetails().setFigure(figure);
                    break;
                }
                case 5:
                {
                    char sex = StringUtil.filterInput(reader.readString(), true).toCharArray()[0];

                    if (sex != player.getDetails().getSex()) {
                        player.getDetails().setSex(sex);
                    }

                    break;
                }
                case 6:
                {
                    String motto = StringUtil.filterInput(reader.readString(), true);
                    player.getDetails().setMotto(motto);
                    break;
                }
                default:
                {
                    System.out.println("Unknown: " + new String(reader.remainingBytes()));
                    reader.readBytes(reader.remainingBytes().length);
                    break;
                }
            }
        }

        PlayerDao.saveDetails(player.getDetails());
        PlayerDao.saveMotto(player.getDetails());

        new GET_INFO().handle(player, null);

        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().refreshAppearance();
        }
    }
}

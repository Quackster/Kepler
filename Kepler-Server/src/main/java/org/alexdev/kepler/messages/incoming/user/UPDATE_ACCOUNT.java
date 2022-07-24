package org.alexdev.kepler.messages.incoming.user;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.LOCALISED_ERROR;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.ServerConfiguration;

public class UPDATE_ACCOUNT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.isLoggedIn()) {
            return;
        }

        String password = "";
        String newPassword = "";
        String birthday = "";
        String email = "";

        while (reader.remainingBytes().length > 0) {
            var valueId = reader.readBase64();
            String value = reader.readString();
            switch(valueId) {
                case 13:
                    password = value;
                    break;
                case 8:
                    birthday = value;
                    break;
                case 7:
                    email = value;
                    break;
                case 3:
                    newPassword = value;
                    break;
            }
        }

        // 0 success
        // 1 wrong password
        // 2 wrong birthday
        // Handle if the player is created with a birthday - otherwise ignore
        if (!PlayerDao.login(player.getDetails(), player.getDetails().getName(), password)) {
            player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(1));
        } else {
            if(player.getDetails().getBirthday().length() > 0) {
                if(newPassword.length() > 0) {
                    var hashedPassword = PlayerManager.getInstance().createPassword(newPassword);
                    player.getDetails().setPassword(hashedPassword);
                    PlayerDao.savePassword(player.getDetails());
                    player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(0));
                } else {
                    if(player.getDetails().getBirthday() == birthday) {
                        player.getDetails().setEmail(email);
                        PlayerDao.saveEmail(player.getDetails());
                        player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(0));
                    } else {
                        player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(2));
                    }
                }

            } else {
                if(newPassword.length() > 0) {
                    var hashedPassword = PlayerManager.getInstance().createPassword(newPassword);
                    player.getDetails().setPassword(hashedPassword);
                    PlayerDao.savePassword(player.getDetails());
                    player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(0));
                } else {
                    player.getDetails().setEmail(email);
                    PlayerDao.saveEmail(player.getDetails());
                    player.send(new org.alexdev.kepler.messages.outgoing.user.UPDATE_ACCOUNT(0));
                }
            }
        }
    }
}

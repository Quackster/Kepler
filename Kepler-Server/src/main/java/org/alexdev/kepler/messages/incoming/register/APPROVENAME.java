package org.alexdev.kepler.messages.incoming.register;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.register.APPROVENAMERELY;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class APPROVENAME implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.isLoggedIn()) {
            return;
        }

        String name = StringUtil.filterInput(reader.readString(), true);
        int nameCheckCode = getNameCheckCode(name);

        player.send(new APPROVENAMERELY(nameCheckCode));
    }

    public static int getNameCheckCode(String name) {
        int nameCheckCode = 0;

        if (PlayerDao.getId(name) > 0) {
            nameCheckCode = 4;
        } else if (name.length() > 16) {
            nameCheckCode = 1;
        } else if (name.length() < 1) {
            nameCheckCode = 2;
        } else if (name.contains(" ") || !hasAllowedCharacters(name, "1234567890qwertyuiopasdfghjklzxcvbnm-+=?!@:.,$") || name.contains("MOD-")) {
            nameCheckCode = 3;
        }

        return nameCheckCode;
    }

    public static boolean hasAllowedCharacters(String str, String allowedChars) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (allowedChars.contains(Character.valueOf(str.toCharArray()[i]).toString())) {
                return true;
            }
        }

        return false;
    }
}

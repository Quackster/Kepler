package org.alexdev.http.util;

import org.alexdev.kepler.dao.mysql.PlayerDao;

import static org.alexdev.kepler.messages.incoming.register.APPROVENAME.hasAllowedCharacters;

public class RegisterUtil {
    public static boolean isValidName(String username) {
        return getNameErrorCode(username) == 0;
    }

    public static int getNameErrorCode(String username) {
        if (!hasAllowedCharacters(username.toLowerCase(), "1234567890qwertyuiopasdfghjklzxcvbnm-+=?!@:.,$")) {
            return 5;
        } else if (username.equalsIgnoreCase("mod") ||
                username.equalsIgnoreCase("staff") ||
                username.equalsIgnoreCase("moderator") ||
                username.toLowerCase().startsWith("admin") ||
                username.toLowerCase().startsWith("mod-")) {
            return 4;
        } else if (username.length() > 24) {
            return 3;
        } else if (username.length() < 1) {
            return 2;
        } else if (PlayerDao.getId(username) > 0) {
            return 1;
        }

        return 0;
    }
}

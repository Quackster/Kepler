package net.h4bbo.http.kepler.util;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.http.kepler.controllers.habblet.NameCheckController;
import org.apache.commons.validator.routines.EmailValidator;

public class RegisterUtil {
    public static boolean isValidName(String username) {
        return getNameErrorCode(username) == 0;
    }

    public static int getNameErrorCode(String username) {
        if (!WordfilterManager.filterSentence(username).equals(username)) {
            return 6;
        } else if (!NameCheckController.hasAllowedCharacters(username.toLowerCase(), "1234567890qwertyuiopasdfghjklzxcvbnm-+=?!@:.,$")) {
            return 5;
        } else if (username.equalsIgnoreCase("admin") ||
                username.equalsIgnoreCase("mod") ||
                username.equalsIgnoreCase("staff") ||
                username.equalsIgnoreCase("moderator") ||
                username.equalsIgnoreCase("vip") ||
                username.toLowerCase().startsWith("admin-") ||
                username.toLowerCase().startsWith("admin=") ||
                username.toLowerCase().startsWith("mod-") ||
                username.toLowerCase().startsWith("mod=") ||
                username.toLowerCase().startsWith("bot-") ||
                username.toLowerCase().startsWith("bot=") ||
                username.toLowerCase().startsWith("vip=") ||
                username.toLowerCase().startsWith("vip-")) {
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

    public static boolean isValidEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            return false;
        }

        if (PlayerDao.getByEmail(email) > 0) {
            return false;
        }

        return true;
    }
}

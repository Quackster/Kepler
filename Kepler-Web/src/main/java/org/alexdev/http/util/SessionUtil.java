package org.alexdev.http.util;

import com.goterl.lazycode.lazysodium.interfaces.PwHash;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.PlayerDetails;

public class SessionUtil {
    public static final String USER_ID = "userId";
    public static final String LOGGED_IN = "authenticated";

    public static boolean login(WebConnection webConnection, String username, String password, boolean deleteAuthVariables) {
        PlayerDetails details = new PlayerDetails();
        boolean hasError;

        if (username.isBlank() || password.isBlank()) {
            hasError = true;
        } else {
            hasError = !PlayerDao.login(details, username, password);
        }

        if (hasError) {
            webConnection.session().set("alertMessage", "Incorrect username or password\n");

            // Delete user login session
            if (deleteAuthVariables) {
                webConnection.session().delete("user.id");
                webConnection.session().delete("authenticated");
            }
            return false;
        } else {
            webConnection.session().set("authenticated", true);
            webConnection.session().set("captcha.invalid", false);
            webConnection.session().set("user.id", details.getId());
            return true;
        }
    }

    public static void logout(WebConnection webConnection) {
        // Delete user login session
        webConnection.session().delete("user.id");
        webConnection.session().delete("authenticated");

    }

    public static String createPassword(String password) throws Exception {
        byte[] pw = password.getBytes();
        byte[] outputHash = new byte[PwHash.STR_BYTES];
        PwHash.Native pwHash = (PwHash.Native) PlayerDao.LIB_SODIUM;
        boolean success = pwHash.cryptoPwHashStr(
                outputHash,
                pw,
                pw.length,
                PwHash.OPSLIMIT_INTERACTIVE,
                PwHash.MEMLIMIT_INTERACTIVE
        );

        if (!success) {
            throw new Exception("Password creation was a failure!");
        }

        return new String(outputHash).replace((char)0 + "", "");
    }

}

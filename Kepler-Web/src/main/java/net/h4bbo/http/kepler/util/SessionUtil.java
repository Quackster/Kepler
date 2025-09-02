package net.h4bbo.http.kepler.util;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.dao.SessionDao;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SessionUtil {
    public static final String PLAYER = "player";
    public static final String USER_ID = "user.id";
    public static final String LOGGED_IN = "authenticated";
    public static final String LOGGED_IN_HOUSKEEPING = "authenticatedHousekeeping";
    public static final String MACHINE_ID = "SECURITY_KEY";

    public static String REMEMEBER_TOKEN_NAME = "remember_token";
    public static int REMEMBER_TOKEN_AGE_SECONDS = (int) TimeUnit.DAYS.toSeconds(31);
    public static int REAUTHENTICATE_TIME = (int) TimeUnit.MINUTES.toSeconds(30);

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
            webConnection.session().set("user.id", details.getId() + "");
            webConnection.session().set("clientAuthenticate", false);
            webConnection.session().set("lastRequest", String.valueOf(DateUtil.getCurrentTimeSeconds() + SessionUtil.REAUTHENTICATE_TIME));

            boolean rememberMe = webConnection.post().getString("_login_remember_me").equals("true");

            if (rememberMe) {
                String rememberMeToken = String.valueOf(UUID.randomUUID());
                webConnection.cookies().set(SessionUtil.REMEMEBER_TOKEN_NAME, rememberMeToken, DateUtil.getCurrentTimeSeconds() + SessionUtil.REMEMBER_TOKEN_AGE_SECONDS, TimeUnit.SECONDS);
                SessionDao.setRememberToken(details.getId(), rememberMeToken);
            } else {
                webConnection.cookies().set(SessionUtil.REMEMEBER_TOKEN_NAME, "", 0, TimeUnit.SECONDS); // Clear cookie
            }

            webConnection.cookies().set("vote_stamp", "", 0, TimeUnit.SECONDS); // Clear cookie

            var pair = details.isBanned();

            if (pair != null) {
                webConnection.redirect("/account/banned");
            }

            return true;
        }
    }

    public static void logout(WebConnection webConnection) {
        // Delete cookies
        if (webConnection.cookies().exists(SessionUtil.REMEMEBER_TOKEN_NAME)) {
            webConnection.cookies().set(SessionUtil.REMEMEBER_TOKEN_NAME, "", 0, TimeUnit.SECONDS);
            SessionDao.clearRememberToken(webConnection.session().getInt("user.id"));
        }

        // Delete user login session
        webConnection.session().delete("user.id");
        webConnection.session().delete("authenticated");
        webConnection.session().delete("minimailLabel");
        webConnection.session().delete("lastBrowsedPage");

    }

    public static void checkCookie(WebConnection webConnection) {
        if (!webConnection.cookies().exists(SessionUtil.REMEMEBER_TOKEN_NAME)) {
            return;
        }

        String token = webConnection.cookies().get(SessionUtil.REMEMEBER_TOKEN_NAME);

        if (token == null || token.isBlank()) {
            return;
        }

        int userId = SessionDao.getRememberToken(token);

        if (userId > 0) {
            webConnection.session().set("authenticated", true);
            webConnection.session().set("captcha.invalid", false);
            webConnection.session().set("user.id", userId);

            if (webConnection.request().uri().equals("/home") ||
                    webConnection.request().uri().equals("/index") ||
                    webConnection.request().uri().equals("/")) {
                webConnection.redirect("/me");
            }
        } else {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.cookies().set(SessionUtil.REMEMEBER_TOKEN_NAME, "", 0, TimeUnit.SECONDS);
        }
    }

}

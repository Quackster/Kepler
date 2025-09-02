package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.EmailDao;
import net.h4bbo.http.kepler.util.EmailUtil;
import net.h4bbo.http.kepler.util.SessionUtil;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.UUID;

public class RecoveryController {
    public static void forgot(WebConnection webConnection) {
        if (!GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("account/email/account_forgot");

        if (webConnection.post().getValues().size() > 0) {
            if (webConnection.post().contains("actionList")) {
                var email = webConnection.post().getString("ownerEmailAddress");

                if (!webConnection.post().contains("ownerEmailAddress")
                        || !EmailValidator.getInstance().isValid(email)
                        || !EmailDao.getDetailsByEmail(webConnection.post().getString("ownerEmailAddress"))) {
                    template.set("invalidForgetName", true);
                    template.render();
                    return;
                }

                template = webConnection.template("account/email/sent");
                template.render();
                //template.set("validForgetName", true);
                return;
            }

            if (webConnection.post().contains("actionForgot")) {
                var email = webConnection.post().getString("forgottenpw-email");

                if (!webConnection.post().contains("forgottenpw-username") || webConnection.post().getString("forgottenpw-username").isBlank()) {
                    template.set("invalidForgetPassword", true);
                    template.render();
                    return;
                }

                if (!webConnection.post().contains("forgottenpw-email") || webConnection.post().getString("forgottenpw-email").isBlank()) {
                    template.set("invalidForgetPassword", true);
                    template.render();
                    return;
                }

                var details = EmailDao.getDetails(webConnection.post().getString("forgottenpw-username"), webConnection.post().getString("forgottenpw-email"));

                if (!EmailValidator.getInstance().isValid(email) || details == null) {
                    template.set("invalidForgetPassword", true);
                    template.render();
                    return;
                }

                //var statistics = new PlayerStatisticManager(playerDetails.getId(), PlayerStatisticsDao.getStatistics(playerDetails.getId()));
                var recoveryCode = UUID.randomUUID().toString();
                var userId = PlayerDao.getId(webConnection.post().getString("forgottenpw-username"));

                PlayerStatisticsDao.updateStatistic(userId, PlayerStatistic.FORGOT_PASSWORD_CODE, recoveryCode);
                PlayerStatisticsDao.updateStatistic(userId, PlayerStatistic.FORGOT_RECOVERY_REQUESTED_TIME, DateUtil.getCurrentTimeSeconds());

                if (GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
                    EmailUtil.send(webConnection, email, "Password recovery at Classic Habbo",
                            EmailUtil.renderPasswordRecovery(
                                    details.getId(),
                                    details.getName(),
                                    recoveryCode
                            )
                    );
                }


                template = webConnection.template("account/email/sent");
                template.render();
                return;
                //template.set("validForgetPassword", true);
            }
        }


        webConnection.session().set("page", "recover");
        template.render();
    }


    public static void recovery(WebConnection webConnection) throws Exception {
        if (!GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
            webConnection.redirect("/");
            return;
        }

        int userId = 0;

        try {
            userId = webConnection.get().getInt("id");
        } catch (Exception ex) {

        }

        String recoveryCode = webConnection.get().getString("code");

        var template = webConnection.template("account/email/account_recovery");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (webConnection.post().contains("user_id") && webConnection.post().contains("recovery_code")) {
            userId = webConnection.post().getInt("user_id");
            recoveryCode = webConnection.post().getString("recovery_code");
        }

        if ((userId == 0 || recoveryCode == null) || !EmailDao.recoveryExists(userId, recoveryCode)) {
            webConnection.session().set("alertMessage", "The recovery code was invalid");
            webConnection.session().set("alertColour", "red");
        } else {
            if (webConnection.post().contains("password") && webConnection.post().contains("confirmpassword")) {
                var password = webConnection.post().getString("password");
                var newPassword = webConnection.post().getString("confirmpassword");

                if (!newPassword.equals(password)) {
                    webConnection.session().set("alertMessage", "The passwords don't match");
                    webConnection.session().set("alertColour", "red");
                } else if (newPassword.length() < 6) {
                    webConnection.session().set("alertMessage", "Password is too short, 6 characters minimum");
                    webConnection.session().set("alertColour", "red");
                } else {
                    webConnection.session().set("alertMessage", "Your password has been changed successfully.");
                    webConnection.session().set("alertColour", "green");

                    PlayerDao.setPassword(userId, PlayerManager.getInstance().createPassword(newPassword));
                    EmailDao.removeRecoveryCode(userId);
                }
            }

            template.set("recoveryCode", recoveryCode);
            template.set("userId", userId);
        }

        template.render();

        webConnection.session().delete("alertMessage");
        webConnection.session().delete("alertColour");
    }

    public static void activate(WebConnection webConnection) {
        if (!GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
            webConnection.redirect("/");
            return;
        }

        int userId = 0;

        try {
            userId = webConnection.get().getInt("id");
        } catch (Exception ex) {

        }

        String activationCode = webConnection.get().getString("code");

        var template = webConnection.template("account/email/account_activated");
        template.set("verifySuccess", true);

        if (userId == 0 || activationCode == null) {
            template.set("verifySuccess", false);
        } else if (!EmailDao.exists(userId, activationCode)) {
            template.set("verifySuccess", false);
        } else {
            EmailDao.activate(userId, activationCode);
        }

        template.render();
    }
}

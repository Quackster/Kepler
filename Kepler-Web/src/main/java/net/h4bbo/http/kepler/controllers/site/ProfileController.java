package net.h4bbo.http.kepler.controllers.site;

import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.dao.mysql.WardrobeDao;
import net.h4bbo.kepler.game.misc.figure.FigureManager;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.player.Wardrobe;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.player.statistics.PlayerStatisticManager;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.SessionDao;
import net.h4bbo.http.kepler.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.internet.AddressException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProfileController {
    public static void profile(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        var statistics = new PlayerStatisticManager(userId, PlayerStatisticsDao.getStatistics(userId));

        webConnection.session().set("page", "me");
        int tab = 0;

        if (webConnection.get().contains("tab")) {
            String value = webConnection.get().getString("tab");

            if (StringUtils.isNumeric(value)) {
                tab = Integer.parseInt(value);
            }
        };

        Template template = null;

        switch (tab) {
            case 1: {
                template = webConnection.template("profile/change_looks");
                profile_flash(template, webConnection);
                break;
            }
            case 2: {
                template = webConnection.template("profile/change_preferences");
                preferences(template, webConnection);
                break;
            }
            case 3: {
                template = webConnection.template("profile/change_email");
                break;
            }
            case 4: {
                template = webConnection.template("profile/change_password");
                break;
            }
            case 5: {
                template = webConnection.template("profile/friend_management");
                FriendManagementController.friendmanagement(template, webConnection, 30, 1, -1, null);
                break;
            }
            default: {
                template = webConnection.template("profile/change_looks");
                profile_flash(template, webConnection);
                break;
            }
        }

        if (template != null) {
            template.set("settingsSavedAlert", false);

            if (webConnection.session().contains("settings.saved.successfully")) {
                template.set("settingsSavedAlert", true);
            }

            template.set("randomNumber", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
            template.render();
        }
        webConnection.session().delete("settings.saved.successfully");

        webConnection.session().delete("alertMessage");
        webConnection.session().delete("alertColour");
    }


    private static void changelooks(Template template, WebConnection webConnection) {
        // https://www.habbo.com/habbo-imaging/avatarimage?figure={{ wardrobeFigure1 }}&size=s&direction=4&head_direction=4&crr=0&gesture=sml&frame=1
        // System.out.println("lol 123");
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (!playerDetails.hasClubSubscription()) {
            webConnection.redirect("/");
            return;
        }


        List<Wardrobe> wardrobeList = WardrobeDao.getWardrobe(playerDetails.getId());

        Wardrobe wardrobeSlot1 = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == 1).findFirst().orElse(null);
        Wardrobe wardrobeSlot2 = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == 2).findFirst().orElse(null);
        Wardrobe wardrobeSlot3 = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == 3).findFirst().orElse(null);
        Wardrobe wardrobeSlot4 = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == 4).findFirst().orElse(null);
        Wardrobe wardrobeSlot5 = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == 5).findFirst().orElse(null);

        template.set("wardrobe1", false);
        template.set("wardrobe2", false);
        template.set("wardrobe3", false);
        template.set("wardrobe4", false);
        template.set("wardrobe5", false);
        
        if (wardrobeSlot1 != null) {
            template.set("wardrobe1", true);
            template.set("wardrobeUrl1", HtmlUtil.createFigureLink(wardrobeSlot1.getFigure(), wardrobeSlot1.getSex()));
            template.set("wardrobeFigure1", wardrobeSlot1.getFigure());
            template.set("wardrobeSex1", wardrobeSlot1.getSex());
        }

        if (wardrobeSlot2 != null) {
            template.set("wardrobe2", true);
            template.set("wardrobeUrl2", HtmlUtil.createFigureLink(wardrobeSlot2.getFigure(), wardrobeSlot2.getSex()));
            template.set("wardrobeFigure2", wardrobeSlot2.getFigure());
            template.set("wardrobeSex2", wardrobeSlot2.getSex());
        }

        if (wardrobeSlot3 != null) {
            template.set("wardrobe3", true);
            template.set("wardrobeUrl3", HtmlUtil.createFigureLink(wardrobeSlot3.getFigure(), wardrobeSlot3.getSex()));
            template.set("wardrobeFigure3", wardrobeSlot3.getFigure());
            template.set("wardrobeSex3", wardrobeSlot3.getSex());
        }

        if (wardrobeSlot4 != null) {
            template.set("wardrobe4", true);
            template.set("wardrobeUrl4", HtmlUtil.createFigureLink(wardrobeSlot4.getFigure(), wardrobeSlot4.getSex()));
            template.set("wardrobeFigure4", wardrobeSlot4.getFigure());
            template.set("wardrobeSex4", wardrobeSlot4.getSex());
        }

        if (wardrobeSlot5 != null) {
            template.set("wardrobe5", true);
            template.set("wardrobeUrl5", HtmlUtil.createFigureLink(wardrobeSlot5.getFigure(), wardrobeSlot5.getSex()));
            template.set("wardrobeFigure5", wardrobeSlot5.getFigure());
            template.set("wardrobeSex5", wardrobeSlot5.getSex());
        }

        if (!playerDetails.hasClubSubscription()) {
            int validateFigureCode = FigureManager.getInstance().validateFigureCode(playerDetails.getFigure(), playerDetails.getSex(), false);

            if (validateFigureCode == 6) {
                template.set("figureHasClub", true);
            } else {
                template.set("figureHasClub", false);
            }
        } else {
            template.set("figureHasClub", false);
        }
    }

    public static void passwordupdate(WebConnection webConnection) throws Exception {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        Template template = webConnection.template("profile/change_password");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        boolean logout = false;

        if (webConnection.post().getString("currentpassword").isBlank() ||
                webConnection.post().getString("newpassword").isBlank() ||
                webConnection.post().getString("newpasswordconfirm").isBlank() ||
                webConnection.post().getString("captcha").isBlank()) {

            webConnection.session().set("alertMessage", "Please enter all fields");
            webConnection.session().set("alertColour", "red");
        } else {
            String currentPassword = webConnection.post().getString("currentpassword");
            String newPassword = webConnection.post().getString("newpassword");
            String newPasswordConfirm = webConnection.post().getString("newpasswordconfirm");
            String captcha = webConnection.post().getString("captcha");

            if (!PlayerDao.login(playerDetails, playerDetails.getName(), currentPassword)) {
                webConnection.session().set("alertMessage", "Your current password is invalid");
                webConnection.session().set("alertColour", "red");
            } else if (newPassword.length() < 6) {
                webConnection.session().set("alertMessage", "Password is too short, 6 characters minimum");
                webConnection.session().set("alertColour", "red");
            } /*else if (newPassword.length() > 25) {
                webConnection.session().set("alertMessage", "Password is too long, 25 characters maximum");
                webConnection.session().set("alertColour", "red");
            } */else if (!newPassword.equals(newPasswordConfirm)) {
                webConnection.session().set("alertMessage", "The passwords don't match");
                webConnection.session().set("alertColour", "red");
            } else if (!webConnection.session().getString("captcha-text").equals(captcha)) {
                webConnection.session().set("alertMessage", "The security code was invalid, please try again.");
                webConnection.session().set("alertColour", "red");
            } else {
                webConnection.session().set("alertMessage", "Your password has been changed successfully. You will need to login again.");
                webConnection.session().set("alertColour", "green");

                PlayerDao.setPassword(playerDetails.getId(), PlayerManager.getInstance().createPassword(newPassword));
                logout = true;
            }
        }


        template.set("randomNumber", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        template.render();

        webConnection.session().delete("alertMessage");
        webConnection.session().delete("alertColour");
        webConnection.session().delete("captcha-text");

        if (logout) {
            SessionUtil.logout(webConnection);
        }
    }

    public static void emailupdate(WebConnection webConnection) throws AddressException {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (webConnection.post().getString("password").isBlank() ||
                webConnection.post().getString("captcha").isBlank()) {

            webConnection.session().set("alertMessage", "Please enter all fields");
            webConnection.session().set("alertColour", "red");
        } else {
            String currentPassword = webConnection.post().getString("password");
            String email = webConnection.post().getString("email");
            String captcha = webConnection.post().getString("captcha");

            if (!PlayerDao.login(playerDetails, playerDetails.getName(), currentPassword)) {
                webConnection.session().set("alertMessage", "Your current password is invalid");
                webConnection.session().set("alertColour", "red");
            } else if (!EmailValidator.getInstance().isValid(email)) {
                webConnection.session().set("alertMessage", "The email you entered is invalid");
                webConnection.session().set("alertColour", "red");
            } else if (!webConnection.session().getString("captcha-text").equals(captcha)) {
                webConnection.session().set("alertMessage", "The security code was invalid, please try again.");
                webConnection.session().set("alertColour", "red");
            } else {
                webConnection.session().set("alertMessage", "Your email has been changed successfully.");
                webConnection.session().set("alertColour", "green");

                if (!playerDetails.getEmail().equals(email)) {
                    var activationCode = UUID.randomUUID().toString();

                    if (GameConfiguration.getInstance().getBoolean("email.smtp.enable")) {
                        if (EmailUtil.send(webConnection, email, "Activate your account at Classic Habbo",
                                EmailUtil.renderActivate(
                                        playerDetails.getId(),
                                        playerDetails.getName(),
                                        email,
                                        activationCode
                                )
                        )) {
                            PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.ACTIVATION_CODE, activationCode);

                            if (GameConfiguration.getInstance().getBoolean("trade.email.verification")) {
                                if (playerDetails.isTradeEnabled()) {
                                    SessionDao.saveTrade(playerDetails.getId(), false);

                                    RconUtil.sendCommand(RconHeader.REFRESH_TRADE_SETTING, new HashMap<>() {{
                                        put("userId", playerDetails.getId());
                                        put("tradeEnabled", "0");
                                    }});
                                }

                            }

                            PlayerDao.setEmail(playerDetails.getId(), email);
                        }
                    }
                }
            }
        }


        webConnection.redirect("/profile?tab=3");
        webConnection.session().delete("captcha-text");
    }

    public static void characterupdate(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }


        String[] expectedFields = new String[] { "figureData", "newGender" };

        for (String field : expectedFields) {
            if (!webConnection.post().queries().containsKey(field) || webConnection.post().queries().get(field).isEmpty()) {
                webConnection.redirect("/profile");
                return;
            }
        }
        
        String newFigure = webConnection.post().getString("figureData");
        String newGender = webConnection.post().getString("newGender");

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
        int validateFigureCode = FigureManager.getInstance().validateFigureCode(newFigure, newGender, playerDetails.hasClubSubscription());
        if (validateFigureCode > 0) {
            webConnection.redirect("/profile");/*?errorCode=" + validateFigureCode);*/
            return;
        }

        if (newGender.charAt(0) != 'M' && newGender.charAt(0) != 'F') {
            webConnection.redirect("/profile");
            return;
        }


        PlayerDao.saveDetails(playerDetails.getId(), newFigure, playerDetails.getPoolFigure(), newGender);

        if (playerDetails.isOnline()) {
            RconUtil.sendCommand(RconHeader.REFRESH_LOOKS, new HashMap<>() {{
                put("userId", playerDetails.getId());
            }});
        }

        webConnection.session().set("settings.saved.successfully", "");
        webConnection.redirect("/profile");
    }

    public static void action(WebConnection webConnection) {
        webConnection.redirect("/profile");
    }

    public static void club(WebConnection webConnection) {
        webConnection.session().set("page", "me");
        ClubController.renderclub(webConnection);
    }

    public static void preferences(Template template, WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

            template.set("onlineStatusEnabled", "");
            template.set("onlineStatusDisabled", "");
            //template.set("hasFlashWarningEnabled", "");
            //template.set("hasFlashWarningDisabled", "");
            template.set("followFriendEnabled", "");
            template.set("followFriendDisabled", "");
            template.set("profileVisibleEnabled", "");
            template.set("profileVisibleDisabled", "");
            template.set("allowFriendRequests", "");
            template.set("allowFriendRequests", "");
            template.set("wordFilterSetting", "");
            template.set("wordFilterSetting", "");
            template.set("onlineStatusEnabled", "");
            template.set("onlineStatusDisabled", "");

        if (playerDetails.isOnlineStatusVisible()) {
            template.set("onlineStatusEnabled", "checked=\"checked\"");
        } else {
            template.set("onlineStatusDisabled", "checked=\"checked\"");
        }

        /*
        if (playerDetails.hasFlashWarning()) {
            template.set("hasFlashWarningEnabled", "checked=\"checked\"");
        } else {
            template.set("hasFlashWarningDisabled", "checked=\"checked\"");
        }
         */

        if (playerDetails.doesAllowStalking()) {
            template.set("followFriendEnabled", "checked=\"checked\"");
        } else {
            template.set("followFriendDisabled", "checked=\"checked\"");
        }

        if (playerDetails.isProfileVisible()) {
            template.set("profileVisibleEnabled", "checked=\"checked\"");
        } else {
            template.set("profileVisibleDisabled", "checked=\"checked\"");
        }

        if (playerDetails.isAllowFriendRequests()) {
            template.set("allowFriendRequests", "checked=\"true\"");
        } else {
            template.set("allowFriendRequests", "");
        }

        if (playerDetails.isWordFilterEnabled()) {
            template.set("wordFilterSetting", "");
        } else {
            template.set("wordFilterSetting", "checked=\"true\"");
        }
    }

    public static void profileupdate(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        String motto = webConnection.post().getString("motto");
        boolean profileVisibility = webConnection.post().getString("visibility").equals("EVERYONE");
        boolean onlineStatusVisibility = webConnection.post().getString("showOnlineStatus").equals("true");
        boolean wordFilterEnabled = !webConnection.post().getString("wordFilterSetting").equals("false");
        boolean allowFriendRequests = webConnection.post().getString("allowFriendRequests").equals("true");
        boolean allowFriendStalking = webConnection.post().getString("followFriendSetting").equals("true");

        if (motto.length() > 32) {
            motto = motto.substring(0, 32);
        }

        SessionDao.savePreferences(motto, profileVisibility, onlineStatusVisibility, wordFilterEnabled, allowFriendRequests, allowFriendStalking, userId);
        
        webConnection.session().set("settings.saved.successfully", "true");
        webConnection.redirect("/profile?tab=2");
    }

    public static void wardrobeStore(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        if (!StringUtils.isNumeric( webConnection.post().getString("slot"))) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int slotId = webConnection.post().getInt("slot");

        String figure = StringUtil.filterInput(webConnection.post().getString("figure"), true);
        String sex = StringUtil.filterInput(webConnection.post().getString("gender"), true);

        if (sex.isBlank()) {
            sex = "M";
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(userId);

        if (!FigureManager.getInstance().validateFigure(figure, sex, playerDetails.hasClubSubscription())) {
            webConnection.redirect("/");
            return;
        }

        if (slotId < 1 || slotId > 5) {
            webConnection.redirect("/");
            return;
        }

        List<Wardrobe> wardrobeList = WardrobeDao.getWardrobe(userId);
        Wardrobe wardrobeData = wardrobeList.stream().filter(wardrobe -> wardrobe.getSlotId() == slotId).findFirst().orElse(null);

        if (wardrobeData == null) {
            WardrobeDao.addWardrobe(userId, slotId, figure, sex.toUpperCase());
        } else {
            WardrobeDao.updateWardrobe(userId, slotId, figure, sex.toUpperCase());
        }

        FullHttpResponse httpResponse = ResponseBuilder.create("application/json", "{\"slot\":\"" + slotId + "\",\"u\":\"" + HtmlUtil.createFigureLink(figure, sex) + "\",\"f\":\"" + figure + "\",\"g\":77}");
        webConnection.send(httpResponse);
    }

    public static void profile_flash(Template template, WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        var statistics = new PlayerStatisticManager(userId, PlayerStatisticsDao.getStatistics(userId));

        webConnection.session().set("page", "me");
        int tab = 0;

        if (webConnection.get().contains("tab")) {
            String value = webConnection.get().getString("tab");

            if (StringUtils.isNumeric(value)) {
                tab = Integer.parseInt(value);
            }
        };

        // Template template = webConnection.template("profile/change_looks_flash");
        changelooks(template, webConnection);

        if (template != null) {
            // template.set("accountActivated", isActivated(statistics.getValue(PlayerStatistic.ACTIVATION_CODE)));

            if (webConnection.session().contains("settings.saved.successfully")) {
                template.set("settingsSavedAlert", "true");
            }

            template.set("randomNumber", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
            template.render();
        }
        webConnection.session().delete("settings.saved.successfully");

        webConnection.session().delete("alertMessage");
        webConnection.session().delete("alertColour");
    }
}

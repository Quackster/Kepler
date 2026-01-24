package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.*;
import net.h4bbo.kepler.game.alerts.AlertType;
import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.player.statistics.PlayerStatisticManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.GroupDiscussionDao;
import net.h4bbo.http.kepler.game.account.BeginnerGiftManager;
import net.h4bbo.http.kepler.game.friends.FriendsFeed;
import net.h4bbo.http.kepler.game.news.NewsArticle;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.*;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AccountController {
    public static void submit(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        String username = HtmlUtil.removeHtmlTags(webConnection.post().getString("username"));
        String password = HtmlUtil.removeHtmlTags(webConnection.post().getString("password"));

        if (SessionUtil.login(webConnection, username, password, true)) {
            webConnection.redirect("/security_check");
        } else {
            boolean rememberMe = webConnection.post().getString("_login_remember_me").equals("true");

            var template = webConnection.template("account/submit");
            template.set("rememberMe", rememberMe ? "true" : "false");
            template.set("username", username);
            template.render();
        }
    }

    public static void securityCheck(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            //System.out.println("webconnection " + webConnection.getIpAddress() + " is not authenticated after submitting");
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("/security_check");
        template.set("redirectPath", webConnection.session().contains("lastBrowsedPage") ? webConnection.session().getString("lastBrowsedPage") : "/me");
        template.render();
    }

    public static void me(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("me");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        webConnection.session().set("page", "me"); // Set current page
        webConnection.session().delete("captcha.invalid"); // Wipe any trace of making /register remember

        /*if (CacheManager.useCachePage(webConnection, "me")) {
            webConnection.send(CacheManager.getPage(webConnection, "me"));
            return;
        }*/

        if (playerDetails.hasClubSubscription()) {
            template.set("hcDays", TimeUnit.SECONDS.toDays(playerDetails.getClubExpiration() - DateUtil.getCurrentTimeSeconds()));
        }

        NewsArticle[] articles = new NewsArticle[5];

        int i = 0;

        boolean includeUnpublished = template.get("playerDetails") != null && ((PlayerDetails)template.get("playerDetails")).getRank().getRankId() > 1;
        List<NewsArticle> articleList = includeUnpublished ? Watchdog.NEWS_STAFF : Watchdog.NEWS;

        if (articleList == null) {
            articleList = List.of();
        }

        for (var article : articleList) {
            articles[i++] = article;
        }

        if (articles[0] == null) {
            articles[0] = new NewsArticle(0, "Installation Complete", 0, "", "Welcome to your brand new Web installation!", "", DateUtil.getCurrentTimeSeconds(), "Rel22_commu_topstory_300x187.gif", "", "", "0", true, 0, false);
        }

        for (i = 0; i < 5; i++) {
            if (articles[i] == null) {
                articles[i] = new NewsArticle(0, "No news", 0, "", "", "", DateUtil.getCurrentTimeSeconds(), "attention_topstory.png", "", "", "0", true, 0, false);
            }

            template.set("article" + (i + 1), articles[i]);
        }

        var alerts = AlertsDao.getAlerts(playerDetails.getId());
        var statisticsValues = PlayerStatisticsDao.getStatistics(playerDetails.getId());

        if (statisticsValues.isEmpty()) {
            PlayerStatisticsDao.newStatistics(playerDetails.getId(), UUID.randomUUID().toString());
            statisticsValues = PlayerStatisticsDao.getStatistics(playerDetails.getId());
        }

        var statistics = new PlayerStatisticManager(playerDetails.getId(), statisticsValues);

        template.set("newbieRoomLayout", statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT));
        template.set("newbieNextGift", statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT));

        if (statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT) > 0 && statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT) > 0) {
            int seconds = statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT_TIME) - DateUtil.getCurrentTimeSeconds();

            if (BeginnerGiftManager.progress(playerDetails, statistics)) {
                seconds = statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT_TIME) - DateUtil.getCurrentTimeSeconds();
            }

            if (seconds < 0) {
                seconds = 0;
            }

            template.set("newbieNextGift", statistics.getIntValue(PlayerStatistic.NEWBIE_GIFT));
            template.set("newbieGiftSeconds", seconds);
        }

        if (playerDetails.hasClubSubscription()) {
            if (alerts.stream().anyMatch(alert -> alert.getAlertType() == AlertType.HC_EXPIRED)) {
                AlertsDao.deleteAlerts(playerDetails.getId(), AlertType.HC_EXPIRED);
            }
        } else if (alerts.stream().noneMatch(alert -> alert.getAlertType() == AlertType.HC_EXPIRED)) {
            if (playerDetails.getFirstClubSubscription() > 0) {
                AlertsDao.createAlert(playerDetails.getId(), AlertType.HC_EXPIRED, "");
            }
        }

        if (playerDetails.getSelectedRoomId() == -1 && statistics.getIntValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT) != -1) {
            statistics.setLongValue(PlayerStatistic.NEWBIE_ROOM_LAYOUT, -1);
        }

        if (playerDetails.formatJoinDate("MM/dd").equalsIgnoreCase(DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "MM/dd")) &&
            !(playerDetails.formatJoinDate("MM/dd/yyyy").equalsIgnoreCase(DateUtil.getDate(DateUtil.getCurrentTimeSeconds(), "MM/dd/yyy")))) {
            LocalDateTime birthday = DateUtil.getDateTimeFromTimestamp(playerDetails.getJoinDate());
            LocalDateTime now = DateUtil.getDateTimeFromTimestamp(DateUtil.getCurrentTimeSeconds());

            Period period = Period.between(birthday.toLocalDate(), now.toLocalDate());

            template.set("hasBirthday", true);
            template.set("birthdayAge", period.getYears());

            if (String.valueOf(period.getYears()).endsWith("1")) {
                template.set("birthdayPrefix", "st");
            } else if (String.valueOf(period.getYears()).endsWith("2")) {
                template.set("birthdayPrefix", "nd");
            } else if (String.valueOf(period.getYears()).endsWith("3")) {
                template.set("birthdayPrefix", "rd");
            } else {
                template.set("birthdayPrefix", "th");
            }
        } else {
            template.set("hasBirthday", false);
        }

        template.set("tags", TagDao.getUserTags(playerDetails.getId()));
        template.set("lastOnline", DateUtil.getFriendlyDate(playerDetails.getLastOnline()));
        template.set("tagRandomQuestion", TagUtil.getRandomQuestion());
        template.set("events", Watchdog.EVENTS);//EventsDao.getEvents());
        template.set("groups", GroupDao.getJoinedGroups(webConnection.session().getInt("user.id")));
        template.set("alerts", AlertsDao.getAlerts(playerDetails.getId()).stream().filter(alert -> !alert.isDisabled()).collect(Collectors.toList()));
        template.set("recommendedGroups", Watchdog.RECOMMENDED_GROUPS);//RecommendedDao.getRecommendedGroups(false));
        template.set("staffPickGroups", Watchdog.STAFF_PICK_GROUPS);

        FriendsFeed.createFriendsOnline(webConnection, template);
        MinimailController.appendMessages(webConnection, template, true, false, false, false, false, false);

        var pendingDetails = GroupMemberDao.getPendingMembers(playerDetails.getId());
        template.set("pendingMembers", pendingDetails.getKey());
        template.set("pendingGroups", pendingDetails.getValue());

        var newGroupPosts = GroupDiscussionDao.getNewGroupMessages(playerDetails.getId(), playerDetails.getLastOnline());
        template.set("newPostsAmount", newGroupPosts.getKey());
        template.set("newPosts", newGroupPosts.getValue());
        template.set("unreadGuestbookMessages", statistics.getIntValue(PlayerStatistic.GUESTBOOK_UNREAD_MESSAGES));
        template.render();

        ClubSubscription.countMemberDays(playerDetails, statistics);
        //CacheManager.savePage(webConnection, "me", ((TwigTemplate)template).renderHTML(), (int) TimeUnit.SECONDS.toSeconds(15));
        //webConnection.send(CacheManager.getPage(webConnection, "me"));

        var ipAddress = webConnection.getIpAddress();
        var latestIpAddress = PlayerDao.getLatestIp(playerDetails.getId());

        if (latestIpAddress == null || !latestIpAddress.equals(ipAddress)) {
            PlayerDao.logIpAddress(playerDetails.getId(), ipAddress);
        }

        if (!webConnection.cookies().exists(SessionUtil.MACHINE_ID) || !webConnection.cookies().get(SessionUtil.MACHINE_ID).equals(playerDetails.getMachineId())) {
            if (!playerDetails.getMachineId().isBlank()) {
                webConnection.cookies().set(SessionUtil.MACHINE_ID, playerDetails.getMachineId().replace("#", ""), 2, TimeUnit.DAYS);
            }
        }
    }

    public static void welcome(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("welcome");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var pair = playerDetails.isBanned();

        if (pair != null) {
            webConnection.redirect("/account/banned");
            return;
        }

        if (!playerDetails.canSelectRoom()) {
            webConnection.redirect("/me");
            return;
        }

        // Set current page
        webConnection.session().set("page", "welcome");
        template.render();
    }

    public static void reauthenticate(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        if (webConnection.post().queries().size() > 0) {
            PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

            String username = playerDetails.getName();
            String password = webConnection.post().getString("password");

            if (SessionUtil.login(webConnection, username, password, false)) {
                webConnection.redirect(webConnection.session().contains("clientRequest") ? webConnection.session().getString("clientRequest") : "/me");
                return;
            }
        }

        // Set current page
        webConnection.session().set("page", "reauthenticate");

        var template = webConnection.template("account/reauthenticate");
        template.render();

        // Delete alert after it's been rendered
        webConnection.session().delete("alertMessage");

    }

    public static void login_popup(WebConnection webConnection) {
        // Set current page
        webConnection.session().set("page", "login_popup");

        var template = webConnection.template("account/login");
        template.render();

        // Delete alert after it's been rendered
        webConnection.session().delete("alertMessage");

    }

    public static void banned(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        webConnection.session().delete("lastBrowsedPage");

        var template = webConnection.template("account/banned");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var pair = playerDetails.isBanned();

        if (pair == null) {
            webConnection.redirect("/me");
            return;
        }

        // Set current page
        webConnection.session().set("page", "banned");

        String bannedMessage = String.format("You have been banned from %s. The reason for the ban is \"%s\". The ban will expire at %s.",
                GameConfiguration.getInstance().getString("site.name"),
                pair.getKey(),
                DateUtil.getDate(pair.getValue(), DateUtil.LONG_DATE));

        template.set("bannedMsg", bannedMessage);
        template.render();

        if (!webConnection.cookies().exists(SessionUtil.MACHINE_ID) || !webConnection.cookies().get(SessionUtil.MACHINE_ID).equals(playerDetails.getMachineId())) {
            if (!playerDetails.getMachineId().isBlank()) {
                webConnection.cookies().set(SessionUtil.MACHINE_ID, playerDetails.getMachineId().replace("#", ""), 2, TimeUnit.DAYS);
            }
        }

        // Delete user login session
        SessionUtil.logout(webConnection);

        //webConnection.session().delete("user.id");
        //webConnection.session().delete("authenticated");
    }

    public static void logout(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        SessionUtil.logout(webConnection);

        // Set current page
        webConnection.session().set("page", "logout");

        var template = webConnection.template("account/logout");
        template.render();
    }
}

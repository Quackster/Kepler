package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.messenger.Messenger;
import net.h4bbo.kepler.game.messenger.MessengerUser;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.http.kepler.dao.MinimailDao;
import net.h4bbo.http.kepler.game.minimail.MinimailMessage;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MinimailController {
    public static void loadMessages(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("habblet/minimail/minimail_messages");
        appendMessages(webConnection, template, false, false, false, false, false, false);
        template.render();
    }

    public static void appendMessages(WebConnection webConnection, Template template, boolean isPageLoad, boolean messageSent, boolean messageDeleted, boolean messageUndeleted, boolean trashEmptied, boolean isMuted) {
        XSSUtil.clear(webConnection);

        String label = webConnection.post().getString("label");

        if (label.isBlank()) {
            if (!webConnection.session().contains("minimailLabel")) {
                label = "inbox";
            } else {
                label = webConnection.session().getString("minimailLabel");

                if (label.equals("conversation") && !webConnection.post().contains("conversationId")) {
                    label = "inbox";
                }
            }
        }

        var startNumber = 0;
        boolean unreadOnly = false;

        try {
            startNumber = webConnection.post().getInt("start");
        } catch (Exception ex) {
            webConnection.send("");
            return;
        }

        try {
            unreadOnly = webConnection.post().getBoolean("unreadOnly");
        } catch (Exception ex) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        int pageNumber = 0;
        if (startNumber > 0) {
            pageNumber = startNumber / 10;
        }

        webConnection.session().set("minimailLabel", label);
        template.set("minimailLabel", label);

        List<MinimailMessage> entireMessageList = new ArrayList<>();

        if (label.equalsIgnoreCase("inbox")) {
            entireMessageList = MinimailDao.getMessages(userId);
        }

        if (label.equalsIgnoreCase("sent")) {
            entireMessageList = MinimailDao.getMessagesSent(userId);
        }

        if (label.equalsIgnoreCase("trash")) {
            entireMessageList = MinimailDao.getMessageTrash(userId);
        }

        if (label.equalsIgnoreCase("conversation")) {
            int conversationId = 0;

            try {
                conversationId = webConnection.post().getInt("conversationId");
            } catch (Exception ex) {

            }

            entireMessageList = MinimailDao.getMessagesConversation(userId, conversationId);
        }
        
        if (unreadOnly) {
            entireMessageList.removeIf(MinimailMessage::isRead);
        }

        template.set("unreadOnly", unreadOnly);

        entireMessageList.sort(Comparator.comparingLong(MinimailMessage::getDateSent).reversed());
        var paginatedMessages = StringUtil.paginate(entireMessageList, 10, true);
        var minimailMessages = paginatedMessages.get(pageNumber);

        template.set("showOlder", false);
        template.set("showOldest", false);

        if (paginatedMessages.containsKey(pageNumber + 1)) {
            template.set("showOlder", true);
        }

        if (paginatedMessages.containsKey(pageNumber + 2)) {
            template.set("showOldest", true);
        }

        if (paginatedMessages.containsKey(pageNumber - 1)) {
            template.set("showNewer", true);
        }

        if (paginatedMessages.containsKey(pageNumber - 2)) {
            template.set("showNewest", true);
        }

        template.set("minimailMessages", minimailMessages);
        template.set("totalMessages", entireMessageList.size());
        template.set("minimailClient", false);

        var endPage = 10;
        var startPage = startNumber;

        if (startNumber != 0) {
            startPage++;
            endPage = startNumber + 10;
        } else {
            startPage = 1;
        }

        if (endPage > entireMessageList.size()) {
            endPage = entireMessageList.size();
        }

        template.set("startPage", startPage);
        template.set("endPage", endPage);

        Map<Integer, PlayerDetails> playerDetailsMap = new HashMap<>();

        for (MinimailMessage minimailMessage : minimailMessages) {
            if (!playerDetailsMap.containsKey(minimailMessage.getToId())) {
                playerDetailsMap.put(minimailMessage.getToId(), PlayerDao.getDetails(minimailMessage.getToId()));
            }

            if (!playerDetailsMap.containsKey(minimailMessage.getSenderId())) {
                playerDetailsMap.put(minimailMessage.getSenderId(), PlayerDao.getDetails(minimailMessage.getSenderId()));
            }


            minimailMessage.setAuthor(playerDetailsMap.get(minimailMessage.getSenderId()));
            minimailMessage.setTarget(playerDetailsMap.get(minimailMessage.getToId()));
        }

        if (!isPageLoad) {
            if (messageSent) {
                if (isMuted) {
                    webConnection.headers().put("X-JSON", "{\"message\":\"You are muted and cannot send messages.\",\"totalMessages\":" + entireMessageList.size() + "}");
                } else {
                    webConnection.headers().put("X-JSON", "{\"message\":\"Message sent successfully.\",\"totalMessages\":" + entireMessageList.size() + "}");
                }
            } else if (messageDeleted) {
                webConnection.headers().put("X-JSON", "{\"message\":\"The message has been moved to the trash. You can undelete it, if you wish\",\"totalMessages\":" + entireMessageList.size() + "}");
            } else if (messageUndeleted) {
                webConnection.headers().put("X-JSON", "{\"message\":\"Message undeleted\",\"totalMessages\":" + entireMessageList.size() + "}");
            } else if (trashEmptied) {
                webConnection.headers().put("X-JSON", "{\"message\":\"The trash has been emptied. Good Job!\",\"totalMessages\":" + entireMessageList.size() + "}");
            } else {
                webConnection.headers().put("X-JSON", "{\"totalMessages\":" + entireMessageList.size() + "}");
            }
        }
    }

    public static void recipients(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
        Messenger messenger = new Messenger(playerDetails);

        StringBuilder recipients = new StringBuilder();

        int i = 0;
        for (MessengerUser messengerUser  : new ArrayList<>(messenger.getFriends())) {
            i++;

            recipients.append("{\"id\":").append(messengerUser.getUserId()).append(",\"name\":\"").append(messengerUser.getUsername()).append("\"}");

            if (messenger.getFriends().size() > i) {
                recipients.append(",");
            }
        }

        webConnection.send("/*-secure-\n[" + recipients.toString() + "]\n */");
    }

    public static void preview(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        webConnection.send(BBCode.format(HtmlUtil.escape(webConnection.post().getString("body")), false));
    }

    public static void sendMessage(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        List<MinimailMessage> minimailMessageList = new ArrayList<>();
        String message = webConnection.post().getString("body");

        var template = webConnection.template("habblet/minimail/minimail_messages");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        var muteExpireTime = PlayerStatisticsDao.getStatisticLong(playerDetails.getId(), PlayerStatistic.MUTE_EXPIRES_AT);
        var isMuted = muteExpireTime > 0 && muteExpireTime > DateUtil.getCurrentTimeSeconds();

        if (!isMuted) {
            if (webConnection.post().contains("recipientIds")) {
                String[] recipients = webConnection.post().getString("recipientIds").split(",");
                String subject = webConnection.post().getString("subject");

                Messenger messenger = new Messenger(playerDetails);

                for (String data : recipients) {
                    if (!StringUtils.isNumeric(data)) {
                        continue;
                    }

                    int toId = Integer.parseInt(data);

                    if (!messenger.hasFriend(toId)) {
                        continue;
                    }

                    if (WordfilterManager.filterSentence(message).equals(message)) {
                        minimailMessageList.add(new MinimailMessage(-1, webConnection.session().getInt("user.id"), toId, webConnection.session().getInt("user.id"), false, subject, message, 0, 0, false));
                        minimailMessageList.add(new MinimailMessage(-1, toId, toId, webConnection.session().getInt("user.id"), false, subject, message, 0, 0, false));
                    }

                }
            } else if (webConnection.post().contains("messageId")) {
                int messageId = 0;
                int userId = webConnection.session().getInt("user.id");

                try {
                    messageId = webConnection.post().getInt("messageId");
                } catch (Exception e) {

                }

                MinimailMessage minimailMessage = MinimailDao.getMessage(messageId, userId);

                if (minimailMessage != null) {
                    minimailMessage.setConversationId(messageId);
                    MinimailDao.updateMessage(minimailMessage);

                    if (WordfilterManager.filterSentence(message).equals(message)) {
                        minimailMessageList.add(new MinimailMessage(-1, webConnection.session().getInt("user.id"), minimailMessage.getSenderId(), webConnection.session().getInt("user.id"), false, "Re: " + minimailMessage.getSubject(), message, 0, minimailMessage.getConversationId(), false));
                        minimailMessageList.add(new MinimailMessage(-1, minimailMessage.getSenderId(), minimailMessage.getSenderId(), webConnection.session().getInt("user.id"), false, "Re: " + minimailMessage.getSubject(), message, 0, minimailMessage.getConversationId(), false));
                    }
                }
            }

            MinimailDao.createMessages(minimailMessageList);
        }

        appendMessages(webConnection, template, false, true, false, false, false, isMuted);
        template.render();
    }

    public static void loadMessage(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int messageId = 0;

        try {
            messageId = webConnection.get().getInt("messageId");
        } catch (Exception ex) {
            webConnection.send("1");
            return;
        }

        if (!(messageId > 0)) {
            webConnection.send("2");
            return;
        }

        MinimailMessage minimailMessage = MinimailDao.getMessage(messageId, userId);

        if (minimailMessage == null) {
            webConnection.send("");
            return;
        }

        boolean canSetUnread = false;

        if (webConnection.session().getString("minimailLabel").equalsIgnoreCase("conversation")) {
            if (userId != minimailMessage.getTargetId() && userId != minimailMessage.getSenderId()) {
                webConnection.send("");
                return;
            }
        } else if (webConnection.session().getString("minimailLabel").equalsIgnoreCase("sent")) {
            if (userId != minimailMessage.getTargetId() && userId != minimailMessage.getSenderId()) {
                webConnection.send("");
                return;
            }
        }

        canSetUnread = true;

        minimailMessage.setTarget(PlayerDao.getDetails(minimailMessage.getToId()));
        minimailMessage.setAuthor(PlayerDao.getDetails(minimailMessage.getSenderId()));

        if (canSetUnread && !minimailMessage.isRead()) {
            minimailMessage.setRead(true);
            MinimailDao.updateMessage(minimailMessage);
        }


        var template = webConnection.template("habblet/minimail/minimail_load_message");
        template.set("minimailLabel", webConnection.session().getString("minimailLabel"));
        template.set("minimailMessage", minimailMessage);
        template.render();
    }

    public static void deleteMessage(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int messageId = 0;

        try {
            messageId = webConnection.post().getInt("messageId");
        } catch (Exception ex) {
            webConnection.send("");
            return;
        }

        if (!(messageId > 0)) {
            webConnection.send("");
            return;
        }

        MinimailMessage minimailMessage = MinimailDao.getMessage(messageId, userId);

        if (minimailMessage == null) {
            webConnection.send("");
            return;
        }

        if (!minimailMessage.isTrash()) {
            minimailMessage.setTrash(true);
            MinimailDao.updateMessage(minimailMessage);
        } else {
            MinimailDao.deleteMessage(minimailMessage);
        }

        var template = webConnection.template("habblet/minimail/minimail_messages");
        appendMessages(webConnection, template, false, false, true, false, false, false);
        template.render();
    }

    public static void undeleteMessage(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        int messageId = 0;

        try {
            messageId = webConnection.post().getInt("messageId");
        } catch (Exception ex) {
            webConnection.send("");
            return;
        }

        if (!(messageId > 0)) {
            webConnection.send("");
            return;
        }

        MinimailMessage minimailMessage = MinimailDao.getMessage(messageId, userId);

        if (minimailMessage == null) {
            webConnection.send("");
            return;
        }

        minimailMessage.setTrash(false);
        MinimailDao.updateMessage(minimailMessage);

        var template = webConnection.template("habblet/minimail/minimail_messages");
        appendMessages(webConnection, template, false, false, false, true, false, false);
        template.render();
    }

    public static void emptyTrash(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        MinimailDao.emptyTrash(userId);

        var template = webConnection.template("habblet/minimail/minimail_messages");
        appendMessages(webConnection, template, false, false, false, false, true, false);
        template.render();
    }
}

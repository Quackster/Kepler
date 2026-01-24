package net.h4bbo.http.kepler.controllers.homes;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.*;
import net.h4bbo.kepler.game.badges.Badge;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerRank;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.dao.HomeEditDao;
import net.h4bbo.http.kepler.dao.HomesDao;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Home;
import net.h4bbo.http.kepler.game.homes.Widget;
import net.h4bbo.http.kepler.game.stickers.StickerCategory;
import net.h4bbo.http.kepler.game.stickers.StickerManager;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import net.h4bbo.http.kepler.util.HomeUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HomesController {
    public static void home(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().contains("authenticated")) {
            return;
        }

        Template template = webConnection.template("home");
        PlayerDetails playerDetails = null;

        if (webConnection.session().contains("authenticated")) {
            playerDetails = (PlayerDetails) template.get("playerDetails");
        }

        String username = null;

        if (webConnection.getMatches().size() > 0) {

            username = webConnection.getMatches().get(0);
        }
        else {
            if (webConnection.get().contains("tag")) {
                username = webConnection.get().getString("tag");
            }
        }

        if (webConnection.getRouteRequest().endsWith("/id")) {
            try {
                int userId = Integer.parseInt(username);
                username = PlayerDao.getName(userId);
            } catch (Exception ex) {
                
            }
        }

        if (username == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        PlayerDetails user = PlayerDao.getDetails(username);

        if (user == null) {
            webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
            return;
        }

        if (playerDetails == null || playerDetails.getRank().getRankId() < PlayerRank.MODERATOR.getRankId()) {
            if (!user.isProfileVisible() || user.isBanned() != null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return;
            }
        }

        Home home = HomesDao.getHome(user.getId());

        boolean defultWidgets = false;

        if (home == null) {
            home = new Home(user.getId(), "bg_pattern_abstract2");
            defultWidgets = true;
            //StickerManager.getInstance().createHome(user.getId());
            //home = HomesDao.getHome(user.getId());
        }

        boolean canAddFriend = false;

        int userId = -1;//webConnection.session().getInt("user.id");
        long sessionTime = -1;//HomeEditDao.getSession(webConnection.session().getInt("user.id"));

        if (webConnection.session().getBoolean("authenticated")) {
            userId = webConnection.session().getInt("user.id");
            sessionTime = HomeEditDao.getSession(webConnection.session().getInt("user.id"));

            if (sessionTime != -1 && userId == user.getId()) {
                webConnection.session().delete("groupEditSession");
                webConnection.session().set("homeEditSession", user);
            }

            if (userId != user.getId() && !MessengerDao.friendExists(userId, user.getId())) {
                canAddFriend = true;
            }
        }

        List<Widget> widgets = null;

        if (defultWidgets) {
            widgets = StickerManager.getInstance().getDefaultWidgets(user.getId());
        } else {
            widgets = WidgetDao.getHomeWidgets(user.getId(), true);
        }

        var enabledBadges = BadgeDao.getBadges(user.getId()).stream().filter(Badge::isEquipped).sorted(Comparator.comparingInt(Badge::getSlotId)).collect(Collectors.toList());
        var guestbook = WidgetDao.getHomeWidgets(user.getId()).stream().filter(w -> w.getProduct().getData().equalsIgnoreCase("guestbookwidget")).findFirst().orElse(null);

        webConnection.session().set("page", "me");

        template.set("user", user);
        template.set("tags", TagDao.getUserTags(user.getId()));
        template.set("hasBadge", enabledBadges.size() > 0);
        template.set("editMode", sessionTime != -1 && userId == user.getId());
        template.set("stickers", widgets);
        template.set("homeBannerAd", HomeUtil.getRandomAd());
        template.set("home", home);
        template.set("canAddFriend", canAddFriend);
        template.set("guestbookSetting", guestbook != null ? guestbook.getGuestbookState() : "public");
        template.set("stickerLimit", HomeUtil.getStickerLimit(user.hasClubSubscription()));
        template.set("tagCloud", new ArrayList<String>());

        if (enabledBadges.size() > 0) {
            template.set("badgeCode", enabledBadges.get(0).getBadgeCode());
        }

        template.set("hasFavouriteGroup", false);

        if (user.getFavouriteGroupId() > 0) {
            Group group = GroupDao.getGroup(user.getFavouriteGroupId());

            if (group != null) {
                template.set("hasFavouriteGroup", true);
                template.set("group", group);
            }
        }

        if (webConnection.session().getBoolean("authenticated")) {
            if (user.getId() == userId) {
                PlayerStatisticsDao.updateStatistic(userId, PlayerStatistic.GUESTBOOK_UNREAD_MESSAGES, 0);
                //HomesDao.resetUnreadMessages(userId);
            }
        }

        template.render();
    }

    public static void inventory(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var tpl = webConnection.template("homes/inventory/inventory");
        PlayerDetails playerDetails = (PlayerDetails) tpl.get("playerDetails");

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        List<StickerCategory> categories = StickerManager.getInstance().getCategories(playerDetails.getRank().getRankId());
        var stickerCategories = categories.stream().filter(category -> category.getCategoryType() == StickerCategory.STICKER_BACKGROUND_TYPE).sorted(Comparator.comparing(StickerCategory::getName)).collect(Collectors.toList());
        var backgroundCategories = categories.stream().filter(category -> category.getCategoryType() == StickerCategory.BACKGROUND_CATEGORY_TYPE).sorted(Comparator.comparing(StickerCategory::getName)).collect(Collectors.toList());

        List<Widget> widgetList = WidgetDao.getInventoryWidgets(playerDetails.getId(), 1);
        List<Widget> inventoryWidgets = new ArrayList<>();

        for (Widget widget : widgetList) {
            if (inventoryWidgets.stream().anyMatch(w -> w.getStickerId() == widget.getStickerId())) {
                Widget w = inventoryWidgets.stream().filter(inv -> inv.getStickerId() == widget.getStickerId()).findFirst().get();
                w.setAmount(w.getAmount() + 1);
            } else {
                inventoryWidgets.add(widget);
            }
        }

        inventoryWidgets.sort(Comparator.comparingInt(Widget::getId).reversed());

        int emptyBoxes = 0;

        if (widgetList.size() > 0) {
            Widget widget = widgetList.get(0);
            webConnection.headers().put("X-JSON", "[[\"Inventory\",\"Web Store\"],[\"" + widget.getProduct().getCssClass() + "\",\"" + widget.getProduct().getData() + "\",\"" + widget.getProduct().getName() + "\",\"Stickers\",null,1]]");
        } else {
            webConnection.headers().put("X-JSON", "[[\"Inventory\",\"Web Store\"],[\"\",\"\",\"\",\"Stickers\",null,1]]");
        }

        if (widgetList.size() > 20) {
            emptyBoxes = (int) (Math.ceil(widgetList.size()/4.0) * 4);
        } else {
            emptyBoxes = 20 - widgetList.size();
        }

        List<Object> emptyBox = new ArrayList<>();

        if (emptyBoxes > 0) {
            for (int i = 0; i < emptyBoxes; i++) {
                emptyBox.add(null);
            }
        }

        tpl.set("stickerCategories", stickerCategories);
        tpl.set("backgroundCategories", backgroundCategories);
        tpl.set("emptyBoxes", emptyBox);
        tpl.set("widgets", inventoryWidgets);
        tpl.render();
    }

    public static void inventoryItems(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }


        int typeId = 0;
        String type = webConnection.post().getString("type");

        if (type.equalsIgnoreCase("stickers")) {
            typeId = 1;
        }

        if (type.equalsIgnoreCase("backgrounds")) {
            typeId = 4;
        }

        if (type.equalsIgnoreCase("notes")) {
            typeId = 3;
        }

        if (!type.equalsIgnoreCase("widgets")) {
            List<Widget> widgetList = WidgetDao.getInventoryWidgets(webConnection.session().getInt("user.id"), typeId);
            List<Widget> inventoryWidgets = new ArrayList<>();

            for (Widget widget : widgetList) {
                if (inventoryWidgets.stream().anyMatch(w -> w.getStickerId() == widget.getStickerId())) {
                    Widget w = inventoryWidgets.stream().filter(inv -> inv.getStickerId() == widget.getStickerId()).findFirst().get();
                    w.setAmount(w.getAmount() + 1);
                } else {
                    inventoryWidgets.add(widget);
                }
            }

            inventoryWidgets.sort(Comparator.comparingInt(Widget::getId).reversed());

            int emptyBoxes = 0;

            if (widgetList.size() > 20) {
                emptyBoxes = (int) (Math.ceil(widgetList.size() / 4.0) * 4);
            } else {
                emptyBoxes = 20 - widgetList.size();
            }

            List<Object> emptyBox = new ArrayList<>();

            if (emptyBoxes > 0) {
                for (int i = 0; i < emptyBoxes; i++) {
                    emptyBox.add(null);
                }
            }

            var tpl = webConnection.template("homes/inventory/inventory_items");
            tpl.set("emptyBoxes", emptyBox);
            tpl.set("widgets", inventoryWidgets);
            tpl.set("widgetMode", false);
            tpl.render();
        } else {
            List<Widget> widgetList = new ArrayList<>();

            if (webConnection.session().contains("groupEditSession")) {
                int groupId = webConnection.session().getInt("groupEditSession");
                widgetList = WidgetDao.getGroupWidgets(groupId);
                widgetList = widgetList.stream().filter(widget -> widget.getProduct().getType() == StickerType.GROUP_WIDGET).collect(Collectors.toList());
            } else {
                int userId = webConnection.session().getInt("user.id");
                widgetList = WidgetDao.getHomeWidgets(userId);
                widgetList = widgetList.stream().filter(widget -> widget.getProduct().getType() == StickerType.HOME_WIDGET).collect(Collectors.toList());
                widgetList.removeIf(widget -> widget.getProduct().getData().equalsIgnoreCase("profilewidget"));
            }

            var tpl = webConnection.template("homes/inventory/inventory_items");
            tpl.set("widgetMode", true);
            tpl.set("widgets", widgetList);
            tpl.render();
        }
    }

    public static void inventoryPreview(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int itemId = webConnection.post().getInt("itemId");
        String type = webConnection.post().getString("type");
        int typeId = 0;

        if (type.equalsIgnoreCase("stickers")) {
            typeId = 1;
        }

        if (type.equalsIgnoreCase("backgrounds")) {
            typeId = 4;
        }

        if (type.equalsIgnoreCase("notes")) {
            typeId = 3;
        }

        if (type.equalsIgnoreCase("widgets")) {
            typeId = webConnection.session().contains("groupEditSession") ? StickerType.GROUP_WIDGET.getTypeId() : StickerType.HOME_WIDGET.getTypeId();
        }

        Widget widget = null;

        if (typeId == StickerType.GROUP_WIDGET.getTypeId()) {
            int groupId = webConnection.session().getInt("groupEditSession");
            List<Widget> widgetList = WidgetDao.getGroupWidgets(groupId);
            widget = widgetList.stream().filter(w -> w.getId() == itemId).findFirst().orElse(null);
        } else if (typeId == StickerType.HOME_WIDGET.getTypeId()) { ;
            int userId = webConnection.session().getInt("user.id");
            List<Widget> widgetList = WidgetDao.getHomeWidgets(userId);
            widget = widgetList.stream().filter(w -> w.getId() == itemId).findFirst().orElse(null);
        } else {
            List<Widget> widgetList = WidgetDao.getInventoryWidgets(webConnection.session().getInt("user.id"), typeId);
            widget = widgetList.stream().filter(w -> w.getId() == itemId).findFirst().orElse(null);
        }

        if (widget != null && typeId == 1) {
            webConnection.headers().put("X-JSON", "[\"" + widget.getProduct().getCssClass() + "\",\"" + widget.getProduct().getData() + "\",\"" + widget.getProduct().getName() + "\",\"Sticker\",null,1]");
        } else if (widget != null && typeId == 4) {
            webConnection.headers().put("X-JSON", String.format("[\"%s\",\"b_%s\",\"%s\",\"%s\",null,1]", widget.getProduct().getCssClass(), widget.getProduct().getData(), widget.getProduct().getName(), "Background"));
        } else if (widget != null && typeId == 3) {
            webConnection.headers().put("X-JSON", "[\"commodity_stickienote_pre\",null,\"Notes\",\"WebCommodity\",null,1]");
        } else if (widget != null && (typeId == StickerType.GROUP_WIDGET.getTypeId() || typeId == StickerType.HOME_WIDGET.getTypeId())) {
            webConnection.headers().put("X-JSON", "[\"" + widget.getProduct().getCssClass() + "\",null,\"\",\"Widget\",\"true\",1]");
        } else {
            webConnection.headers().put("X-JSON", "[\"\",\"\",\"\",\"Sticker\",null,1]");
        }

        var tpl = webConnection.template("homes/inventory/inventory_preview");
        tpl.render();
    }

    public static void startEditingSession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        String match = webConnection.getMatches().get(0);

        if (!StringUtils.isNumeric(match)) {
            webConnection.redirect("/me");
            return;
        }

        int targetId = Integer.parseInt(match);

        if (targetId != userId) {
            webConnection.redirect("/me");
            return;
        }

        Home home = HomesDao.getHome(targetId);

        if (home == null) {
            StickerManager.getInstance().createHome(targetId);
        }

        if (!HomeEditDao.hasSession(userId)) {
            HomeEditDao.createSession(userId);
            webConnection.session().set("homeEditSession", userId);
            webConnection.session().delete("groupEditSession");
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
        webConnection.redirect("/home/" + playerDetails.getName());
    }

    public static void cancelEditingSession(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");
        String match = webConnection.getMatches().get(0);

        if (!StringUtils.isNumeric(match)) {
            webConnection.redirect("/me");
            return;
        }

        int targetId = Integer.parseInt(match);

        if (targetId != userId) {
            webConnection.redirect("/me");
            return;
        }

        if (HomeEditDao.hasSession(userId)) {
            HomeEditDao.delete(userId);
            webConnection.session().delete("homeEditSession");
            webConnection.session().delete("groupEditSession");
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));
        webConnection.redirect("/home/" + playerDetails.getName());
    }

    public static void save(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (playerDetails == null) {
            webConnection.session().delete("user.id");
            webConnection.session().delete("authenticated");
            webConnection.redirect("/");
            return;
        }

        int userId = webConnection.session().getInt("user.id");

        if (!HomeEditDao.hasSession(userId)) {
            webConnection.send("");
            return;
        }

        Home home = HomesDao.getHome(userId);
        List<Widget> homeWidgets = WidgetDao.getHomeWidgets(userId, true);

        try {
            if (webConnection.post().contains("background")) {
                int backgroundId = Integer.parseInt(webConnection.post().getString("background").split(":")[0]);


                List<Widget> widgetList = WidgetDao.getInventoryWidgets(playerDetails.getId());
                Widget widget = widgetList.stream().filter(w -> w.getId() == backgroundId).findFirst().orElse(null);

                if (widget != null) {
                    home.setBackground(widget.getProduct().getData());
                    home.saveBackground();
                }
            }

            if (webConnection.post().contains("stickers")) {
                String[] stickerData = webConnection.post().getString("stickers").split(Pattern.quote("/"));

                if (stickerData.length >= HomeUtil.getStickerLimit(playerDetails.hasClubSubscription())) {
                    webConnection.send("");
                    return;
                }

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = homeWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }

            if (webConnection.post().contains("widgets")) {
                String[] stickerData = webConnection.post().getString("widgets").split(Pattern.quote("/"));

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = homeWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }

            if (webConnection.post().contains("stickienotes")) {
                String[] stickerData = webConnection.post().getString("stickienotes").split(Pattern.quote("/"));

                for (String sticker : stickerData) {
                    int stickerId = Integer.parseInt(sticker.split(":")[0]);
                    String[] coordData = sticker.replace(stickerId + ":", "").split(",");

                    int x = Integer.parseInt(coordData[0]);
                    int y = Integer.parseInt(coordData[1]);
                    int z = Integer.parseInt(coordData[2]);

                    Widget widget = homeWidgets.stream().filter(w -> w.getId() == stickerId).findFirst().orElse(null);

                    if (widget != null) {
                        widget.setX(x);
                        widget.setY(y);
                        widget.setZ(z);
                        widget.save();
                    }
                }
            }
        } catch (Exception ex) {

        }

        webConnection.send("<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                "waitAndGo('" + GameConfiguration.getInstance().getString("site.path") + "/home/" + playerDetails.getName() + "');\n" +
                "</script>\n");


        HomeEditDao.delete(userId);
        webConnection.session().delete("homeEditSession");
    }

    public static void tagList(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("");
            return;
        }

        int accountId = webConnection.post().getInt("accountId");

        var template = webConnection.template("homes/widget/habblet/taglist");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            webConnection.send("");
            return;
        }

        List<String> tags = TagDao.getUserTags(accountId);

        template.set("tags", tags);
        template.set("user", playerDetails);
        template.render();
    }
}

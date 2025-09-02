package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.TagDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.tags.HabboTag;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.HtmlUtil;
import net.h4bbo.http.kepler.util.RconUtil;
import net.h4bbo.http.kepler.util.TagUtil;
import net.h4bbo.http.kepler.util.XSSUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagController {
    public static void tag(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        int pageId = webConnection.get().getInt("pageNumber");

        List<HabboTag> tags = new ArrayList<>();
        Map<Integer, List<HabboTag>> paginatedUsers = StringUtil.paginate(tags, 5);

        if (!paginatedUsers.containsKey(pageId - 1)) {
            pageId = 1;
        }

        if (paginatedUsers.get(pageId - 1) == null) {
            paginatedUsers.put(pageId - 1, new ArrayList<>());
        }

        var tpl = webConnection.template("tag");
        tpl.set("tagList", paginatedUsers.get(pageId - 1));
        tpl.set("pageId", pageId);
        tpl.set("totalCount", tags.size());
        tpl.set("tagCloud", Watchdog.TAG_CLOUD_10);
        tpl.set("tagSearchAdd", "");
        tpl.set("showOlder", false);
        tpl.set("showOldest", false);
        tpl.set("showNewer", false);
        tpl.set("showNewest", false);
        tpl.set("showFirst", false);
        tpl.set("showLast", false);
        tpl.set("showFirstPage", 1);
        webConnection.session().set("page", "community");
        tpl.render();
    }

    public static void search(WebConnection webConnection) {
        String tag = null;
        int pageId = 1;//

        try {
            pageId = webConnection.get().getInt("pageNumber");
        } catch (Exception ex) {

        }

        if (webConnection.get().contains("tag")) {
            tag = webConnection.get().getString("tag");
        } else {
            tag = webConnection.getMatches().get(0);
        }

        respondWithSearch(webConnection, tag, pageId, "tag");
    }


    public static void tagsearch(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        int pageId = 0;
        String tag = webConnection.post().getString("tag");

        respondWithSearch(webConnection, tag, pageId, "base/tag_search");
    }

    private static void respondWithSearch(WebConnection webConnection, String tag, int pageId, String template) {
        XSSUtil.clear(webConnection);

        try {
            tag = URLDecoder.decode(tag, StandardCharsets.UTF_8);
        } catch (Exception ex) {

        }

        List<HabboTag> tags = tag.isBlank() ? new ArrayList<>() : TagDao.getTagInfoList(tag);
        Map<Integer, List<HabboTag>> paginatedUsers = StringUtil.paginate(tags, 5);

        if (!paginatedUsers.containsKey(pageId - 1)) {
            pageId = 1;
        }

        tag = StringUtils.normalizeSpace(HtmlUtil.removeHtmlTags(tag));
        var tpl = webConnection.template(template);

        tpl.set("tagSearchAdd", "");

        if (webConnection.session().getBoolean("authenticated")) {
            int userId = webConnection.session().getInt("user.id");

            var temporaryTag = StringUtil.isValidTag(tag, userId, 0, 0);
            boolean isValidTag = temporaryTag != null;

            if (isValidTag) {
                tpl.set("tagSearchAdd", tag);
            }
        }

        tpl.set("showOlder", false);
        tpl.set("showOldest", false);
        tpl.set("showNewer", false);
        tpl.set("showNewest", false);
        tpl.set("showFirst", false);
        tpl.set("showFirstPage", 1);
        tpl.set("showLast", false);

        int codePage = pageId - 1;
        
        if (paginatedUsers.containsKey(codePage - 1)) {
            tpl.set("showOlder", true);
        }

        if (paginatedUsers.containsKey(codePage - 2)) {
            tpl.set("showOldest", true);
        }

        if (paginatedUsers.containsKey(codePage + 1)) {
            tpl.set("showNewer", true);
        }

        if (paginatedUsers.containsKey(codePage + 2)) {
            tpl.set("showNewest", true);
        }

        if (paginatedUsers.containsKey(codePage + 3)) {
            tpl.set("showLast", true);
            tpl.set("showLastPage", paginatedUsers.size());
        }

        if (paginatedUsers.containsKey(codePage - 3)) {
            tpl.set("showFirst", true);
            tpl.set("showFirstPage", 1);
        }

        tpl.set("tagList", paginatedUsers.get(pageId - 1));
        tpl.set("totalTagUsers", paginatedUsers);
        tpl.set("tag", tag);
        tpl.set("pageId", pageId);
        tpl.set("totalCount", tags.size());
        tpl.set("tagCloud", Watchdog.TAG_CLOUD_10);
        tpl.set("lastPage", tags.size());
        webConnection.session().set("page", "community");
        tpl.render();
    }

    public static void add(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            return;
        }

        var tagList = TagDao.getUserTags(userId);

        if (tagList.size() >= GameConfiguration.getInstance().getInteger("max.tags.users")) {
            webConnection.send("taglimit");
            return;
        }

        String tag = StringUtil.isValidTag(webConnection.post().getString("tagName"), userId, 0, 0);

        if (tag == null) {
            webConnection.send("invalidtag");
            return;
        }

        if (WordfilterManager.filterSentence(tag).equals(tag)) {
            StringUtil.addTag(tag, userId, 0, 0);
        }

        webConnection.send("valid");

        RconUtil.sendCommand(RconHeader.REFRESH_TAGS, new HashMap<>() {{
            put("userId", userId);
        }});
    }


    public static void remove(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("");
            return;
        }

        var template = webConnection.template("homes/widget/habblet/taglist");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            webConnection.send("");
            return;
        }

        String tag = webConnection.post().getString("tagName");
        TagDao.removeTag(userId, 0, 0, tag);

        List<String> tags = TagDao.getUserTags(userId);

        template.set("tags", tags);
        template.set("user", playerDetails);
        template.render();

        RconUtil.sendCommand(RconHeader.REFRESH_TAGS, new HashMap<>() {{
            put("userId", userId);
        }});
    }

    public static void mytaglist(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            return;
        }

        var template = webConnection.template("habblet/myTagList");
        template.set("tags", TagDao.getUserTags(userId));
        template.set("tagRandomQuestion", TagUtil.getRandomQuestion());
        template.render();
    }

    public static void tagfight(WebConnection webConnection) {
        /*			<div id="fightResultCount" class="fight-result-count">
				Tie!<br />
			    test
			(1) hits
            <br/>
                alex
            (1) hits
		    </div>
			<div class="fight-image">
					<img src="http://habbowave.zapto.org/web-gallery/images/tagfight/tagfight_end_0.gif" alt="" name="fightanimation" id="fightanimation" />
                <a id="tag-fight-button-new" href="#" class="new-button" onclick="TagFight.newFight(); return false;"><b>New Fight</b><i></i></a>
                <a id="tag-fight-button" href="#" style="display:none" class="new-button" onclick="TagFight.init(); return false;"><b>Fight</b><i></i></a>
            </div>
*/
        String firstTag = HtmlUtil.removeHtmlTags(webConnection.post().getString("tag1"));
        String secondTag = HtmlUtil.removeHtmlTags(webConnection.post().getString("tag2"));

        int firstCount = TagDao.countTag(firstTag);
        int secondCount = TagDao.countTag(secondTag);
        int imageNumber = 0;

        String result = "Tie!";

        if (secondCount > firstCount) {
            imageNumber = 1;
            result = "The winner is:";
        }

        if (secondCount < firstCount) {
            imageNumber = 2;
            result = "The winner is:";
        }

        var template = webConnection.template("habblet/tagFightResult");
        template.set("result", result);
        template.set("resultTag1", firstTag);
        template.set("resultTag2", secondTag);
        template.set("resultHits1", firstCount);
        template.set("resultHits2", secondCount);
        template.set("tagFightImage", imageNumber);
        template.render();
    }

    public static void tagmatch(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        var template = webConnection.template("habblet/tagMatch");
        PlayerDetails playerDetails = (PlayerDetails) template.get("playerDetails");

        if (playerDetails == null) {
            webConnection.redirect("/");
            return;
        }

        String friendName = webConnection.post().getString("friendName");
        String errorMessage = "";

        if (!MessengerDao.friendExists(playerDetails.getId(), PlayerDao.getId(friendName))) {
            errorMessage = "Friend not found. Are you sure that they really exist?";
        }

        template.set("errorMsg", errorMessage);
        template.render();
    }

    public static void remove_all_tags(WebConnection webConnection) {
        int userId = webConnection.session().getInt("user.id");

        if (userId < 1) {
            webConnection.send("Please login to remove all your tags.");
            return;
        }

        var myTagList =  TagDao.getUserTags(userId);
        TagDao.removeTags(userId, 0, 0);
        webConnection.send("All tags removed!<br><br>The tags removed: " + String.join(", ", myTagList));
    }
}

package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;

import java.util.List;

public class MemberWidgetController {
    public static void membersearchpaging(WebConnection webConnection) {
        int widgetId = webConnection.post().getInt("widgetId");
        int pageNumber = 1;

        try {
            pageNumber = webConnection.post().getInt("pageNumber");
        } catch (Exception ex) {

        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        String searchString = webConnection.post().getString("searchString");

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null) {
            webConnection.send("");
            return;
        }

        var pages = widget.getMembersPages();
        List<GroupMember> memberList = widget.getMembersList(searchString, pageNumber);

        var template = webConnection.template("homes/widget/habblet/membersearchpaging");
        template.set("sticker", widget);
        template.set("pages", pages);
        template.set("members", widget.getMembersAmount());
        template.set("membersList", memberList);
        template.set("currentPage", pageNumber);
        template.set("group", GroupDao.getGroup(widget.getGroupId()));
        template.render();
    }
}

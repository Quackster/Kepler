package net.h4bbo.http.kepler.util;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class GroupUtil {
    public static void refreshGroup(int groupId) {
        RconUtil.sendCommand(RconHeader.REFRESH_GROUP, new HashMap<>() {{
            put("groupId", String.valueOf(groupId));
        }});
    }

    public static Group resolve(WebConnection webConnection) {
        String match = webConnection.getMatches().get(0);

        String groupAlias = null;
        Group group = null;

        if (StringUtils.isNumeric(match) && webConnection.getRouteRequest().endsWith("/id/discussions")) {
            group = GroupDao.getGroup(Integer.parseInt(match));

            if (group == null) {
                webConnection.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, webConnection));
                return null;
            }

            if (!group.getAlias().isBlank()) {
                webConnection.redirect("/groups/" + group.getAlias() + "/discussions");
                return null;
            }

        } else if (!webConnection.getRouteRequest().endsWith("/id/discussions")){
            groupAlias = match;
            group = GroupDao.getGroupByAlias(groupAlias);
        }

        return group;
    }
}

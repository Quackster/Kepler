package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;

public class RoomSelectionController {
    public static void confirm(WebConnection webConnection) {
        var template = webConnection.template("habblet/roomselectionConfirm");
        template.render();
    }

    public static void create(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated") || !webConnection.post().contains("roomType")) {
            webConnection.send("");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (!playerDetails.canSelectRoom()) {
            webConnection.send("");
            return;
        }

        int roomType = Integer.parseInt(webConnection.post().getString("roomType"));

        if (roomType < 0 || roomType > 5) {
            webConnection.send("");
            return;
        }
        
        webConnection.send("");
    }

    public static void hide(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.send("");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        if (!playerDetails.canSelectRoom()) {
            webConnection.send("");
            return;
        }

        playerDetails.setSelectedRoomId(-1);

        PlayerDao.saveSelectedRoom(playerDetails.getId(), -1);
        PlayerStatisticsDao.updateStatistic(playerDetails.getId(), PlayerStatistic.NEWBIE_ROOM_LAYOUT, -1);

        webConnection.send("");
    }
}

package net.h4bbo.http.kepler.controllers.homes.widgets;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.dao.mysql.SongMachineDao;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.song.Song;
import net.h4bbo.http.kepler.dao.WidgetDao;
import net.h4bbo.http.kepler.game.homes.Widget;
import net.h4bbo.http.kepler.game.stickers.StickerType;
import org.apache.commons.lang3.StringUtils;

public class TraxController {
    public static void selectSong(WebConnection webConnection) {
        if (!webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/");
            return;
        }

        PlayerDetails playerDetails = PlayerDao.getDetails(webConnection.session().getInt("user.id"));

        int widgetId = -1;
        int songId = -1;

        try {
            widgetId = webConnection.post().getInt("widgetId");
        } catch (Exception ex) {

        }

        try {
            songId = webConnection.post().getInt("songId");
        } catch (Exception ex) {

        }

        Widget widget = WidgetDao.getWidget(widgetId);

        if (widget == null || !widget.getProduct().getData().toLowerCase().equals("traxplayerwidget")) {
            webConnection.send("");
            return;
        }

        boolean canSelect = false;

        if (widget.getProduct().getType() == StickerType.GROUP_WIDGET) {
            canSelect = (GroupDao.getGroupOwner(widget.getGroupId()) == playerDetails.getId());
        } else if (widget.getProduct().getType() == StickerType.HOME_WIDGET) {
            canSelect = (widget.getUserId() == playerDetails.getId());
        }

        if (!canSelect) {
            webConnection.send("");
            return;
        }

        var songList = widget.getSongs();
        Song song = SongMachineDao.getSong(songId);

        if (songId == 0 || song == null || songList.stream().noneMatch(s -> s.getId() == song.getId())) {
            widget.setExtraData("");
        } else {
            widget.setExtraData("" + song.getId());
        }

        WidgetDao.save(widget);

        Template template = webConnection.template("homes/widget/habblet/trax_song");
        template.set("sticker", widget);
        template.render();
    }

    public static void getSong(WebConnection webConnection) {
        String songData = null;

        try {
            songData = webConnection.getMatches().get(0);
        } catch (Exception ex) {

        }

        if (!StringUtils.isNumeric(songData)) {
            webConnection.send("");
            return;
        }

        try {
            Song song = SongMachineDao.getSong(Integer.parseInt(songData));
            String data = song.getData().substring(0, song.getData().length() - 1);

            String trackData = data;
            trackData = trackData.replace(":4:", "&track4=");
            trackData = trackData.replace(":3:", "&track3=");
            trackData = trackData.replace(":2:", "&track2=");
            trackData = trackData.replace("1:", "&track1=");

            String author = PlayerDao.getName(song.getUserId());
            webConnection.send("status=0&name=" + song.getTitle() + "&author=" + author + trackData);
        } catch (Exception ex) {
            webConnection.send("");
        }
    }
}

package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.util.XSSUtil;

public class SiteController {
    public static void pixels(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (!webConnection.session().contains("authenticated")) {
            return;
        }

        var template = webConnection.template("pixels");
        webConnection.session().set("page", "credits");
        template.render();
    }

}

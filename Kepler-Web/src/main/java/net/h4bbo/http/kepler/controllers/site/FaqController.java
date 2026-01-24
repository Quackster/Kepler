package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.http.kepler.util.XSSUtil;

public class FaqController {
    public static void faq(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        var template = webConnection.template("faq");
        template.render();
    }
}

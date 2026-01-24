package net.h4bbo.http.kepler.controllers.site;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.util.HomeUtil;
import net.h4bbo.http.kepler.util.HtmlUtil;
import net.h4bbo.http.kepler.util.XSSUtil;

public class HomepageController {
    public static void homepage(WebConnection webConnection) {
        XSSUtil.clear(webConnection);

        if (webConnection.session().getBoolean("authenticated")) {
            webConnection.redirect("/me");
            return;
        }

        boolean rememberMe = webConnection.get().getString("rememberme").equals("true");
        String username = HtmlUtil.removeHtmlTags(webConnection.get().getString("username"));

        var template = webConnection.template(GameConfiguration.getInstance().getString("homepage.template.file"));
        template.set("rememberMe", rememberMe);
        template.set("username", username);
        template.set("tagCloud", Watchdog.TAG_CLOUD_20);

        boolean isValentinesMonth = Integer.parseInt(DateUtil.getCurrentDate("M")) == 2 && Integer.parseInt(DateUtil.getCurrentDate("DD")) <= 16;
        template.set("isValentinesMonth", isValentinesMonth);
        template.set("randomValentinesImage", HomeUtil.getRandomValentinesImage());

        template.render();

        // Delete alert after it's been rendered
        webConnection.session().delete("alertMessage");
    }

    public static void maintenance(WebConnection webConnection) {
        var template = webConnection.template("maintenance");
        template.render();
    }
}

package org.alexdev.http.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;
import org.alexdev.kepler.util.config.GameConfiguration;

public class SiteBinder implements TemplateBinder {
    private String siteName;
    private String sitePath;
    private String staticContentPath;

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        this.siteName = GameConfiguration.getInstance().getString("site.name");
        this.sitePath = GameConfiguration.getInstance().getString("site.path");
        this.staticContentPath = GameConfiguration.getInstance().getString("static.content.path");
        template.set("site", this);
    }
}

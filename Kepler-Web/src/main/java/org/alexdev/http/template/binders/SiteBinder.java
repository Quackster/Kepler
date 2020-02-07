package org.alexdev.http.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;

public class SiteBinder implements TemplateBinder {
    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        template.set("site", this);
    }
}

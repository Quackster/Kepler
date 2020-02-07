package org.alexdev.http.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;

public class SessionBinder implements TemplateBinder {
    private boolean loggedIn;
    private String currentPage;

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        this.currentPage = webConnection.session().getString("page");

        if (webConnection.session().getBoolean("authenticated")) {
            this.loggedIn = true;
        }

        template.set("session", this);
    }
}

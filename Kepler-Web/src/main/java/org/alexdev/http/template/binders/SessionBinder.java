package org.alexdev.http.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;
import org.alexdev.http.util.SessionUtil;

public class SessionBinder implements TemplateBinder {
    private boolean loggedIn;
    private String currentPage;

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        this.currentPage = webConnection.session().getString("page");

        if (webConnection.session().getBoolean(SessionUtil.LOGGED_IN)) {
            this.loggedIn = true;
        }

        template.set("session", this);
    }
}

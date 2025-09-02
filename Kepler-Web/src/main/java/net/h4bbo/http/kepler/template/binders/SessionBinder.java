package net.h4bbo.http.kepler.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;

public class SessionBinder implements TemplateBinder {
    private boolean loggedIn;
    private String currentPage;

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        if (webConnection != null) {
            this.currentPage = webConnection.session().getString("page");

            if (webConnection.session().getBoolean("authenticated")) {
                this.loggedIn = true;
            }
        }

        template.set("session", this);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getCurrentPage() {
        return currentPage;
    }
}

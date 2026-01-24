package net.h4bbo.http.kepler.template.binders;

import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.template.Template;
import org.alexdev.duckhttpd.template.TemplateBinder;

public class AlertBinder implements TemplateBinder {
    private boolean hasAlert;
    private String message;
    private String colour;

    public AlertBinder(String message, String colour) {
        if (message != null) {
            this.hasAlert = true;
            this.message = message;
            this.colour = colour;
        }
    }

    @Override
    public void onRegister(Template template, WebConnection webConnection) {
        template.set("alert", this);
    }

    public boolean isHasAlert() {
        return hasAlert;
    }

    public String getMessage() {
        return message;
    }

    public String getColour() {
        return colour;
    }
}

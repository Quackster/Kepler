package net.h4bbo.http.kepler.controllers.habblet;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public class NavigationComponent {
    public static void navigation(WebConnection webConnection) {
        webConnection.send("");
    }
}

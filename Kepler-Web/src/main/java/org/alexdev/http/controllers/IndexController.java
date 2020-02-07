package org.alexdev.http.controllers;

import org.alexdev.duckhttpd.server.connection.WebConnection;

public class IndexController {
    public static void homepage(WebConnection webConnection) {
        var tpl = webConnection.template("index");
        tpl.render();
    }
}

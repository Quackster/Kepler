package org.alexdev.http;

import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.http.controllers.IndexController;

public class Routes {
    public static void register() {
        RouteManager.addRoute("/", IndexController::homepage);
        RouteManager.addRoute("/index", IndexController::homepage);
    }
}

package net.h4bbo.http.kepler.controllers;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.alexdev.duckhttpd.routes.Route;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.http.kepler.Routes;
import net.h4bbo.http.kepler.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController implements Route {
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Override
    public void handleRoute(WebConnection webConnection) throws Exception {
        /*if (!webConnection.request().headers().isEmpty()) {
            for (String name: webConnection.request().headers().names()) {
                for (String value: webConnection.request().headers().getAll(name)) {
                    System.out.println("HEADER: " + name + " = " + value);
                }
            }
            System.out.println();
        }*/

        if (webConnection.isRequestHandled()) {
            if (GameConfiguration.getInstance().getBoolean("maintenance") && !webConnection.getRouteRequest().startsWith("/api")) {
                if (!webConnection.getRouteRequest().startsWith("/maintenance") && !webConnection.getRouteRequest().startsWith("/" + Routes.HOUSEKEEPING_PATH)) {
                    webConnection.redirect("/maintenance");
                    return;
                }
            }
        }

        if (!webConnection.getRouteRequest().startsWith("/api")) {
            if (!webConnection.request().headers().isEmpty()) {
                String host = webConnection.request().headers().get(HttpHeaderNames.HOST);

                if (webConnection.request().headers().contains("X-Forwarded-Proto")) {
                    String request = webConnection.request().headers().get("X-Forwarded-Proto");

                    if (host != null && request.equalsIgnoreCase("http")) {
                        String targetUrl = "https://" + host;
                        String requestUri = webConnection.request().uri();

                        if (!requestUri.startsWith("/")) {
                            targetUrl += "/";
                        }

                        targetUrl += requestUri;

                        webConnection.movedpermanently(targetUrl);
                        return;
                    }
                }
            }
        }

        if (webConnection.isRequestHandled()) {
            /*if (!(webConnection.getRouteRequest().equals("/register")
                    || webConnection.getRouteRequest().equals("/client")
                    || webConnection.getRouteRequest().equals("/login_popup")
                    || webConnection.getRouteRequest().startsWith("/clientlog")
                    || webConnection.getRouteRequest().equals("/client")
                    || webConnection.getRouteRequest().equals("/security_check")
                    || webConnection.getRouteRequest().startsWith("/account/")
                    || webConnection.getRouteRequest().startsWith("/api/")
                    || webConnection.getRouteRequest().startsWith("/habblet/")
                    || webConnection.getRouteRequest().startsWith("/groups/actions")
                    || webConnection.getRouteRequest().startsWith("/myhabbo/"))) {
                webConnection.session().set("lastBrowsedPage", webConnection.getRouteRequest());
            }*/

            if (webConnection.session().getBoolean("authenticated")) {
                this.handleAuthenticatedRoute(webConnection);
            } else {
                SessionUtil.checkCookie(webConnection);
            }
        }
    }

    private void handleAuthenticatedRoute(WebConnection webConnection) {
        if (webConnection.getRouteRequest().equals("/client")) {
            webConnection.session().set("lastRequest", String.valueOf(DateUtil.getCurrentTimeSeconds() + SessionUtil.REAUTHENTICATE_TIME));
        }

        if (webConnection.session().contains("lastRequest")) {
            long lastRequest = webConnection.session().getLongOrElse("lastRequest", 0);

            if (DateUtil.getCurrentTimeSeconds() > lastRequest) {
                //if (webConnection.cookies().exists(SessionUtil.REMEMEBER_TOKEN_NAME)) {
                webConnection.session().set("clientAuthenticate", true);
                //}
            }
        } else {
            webConnection.session().set("clientAuthenticate", false);

        }

        /*StringBuilder postRequest = new StringBuilder("(");
        StringBuilder getRequest = new StringBuilder();

        for (var entry : webConnection.post().getValues().entrySet()) {
            postRequest.append(entry.getKey());
            postRequest.append(" = ");
            postRequest.append(entry.getValue());
            postRequest.append(", ");
        }

        for (var entry : webConnection.get().getValues().entrySet()) {
            getRequest.append(entry.getKey());
            getRequest.append(" = ");
            getRequest.append(entry.getValue());
            getRequest.append(", ");
        }

        getRequest.append(")");
        postRequest.append(")");

        logger.info("Request: " + webConnection.getUriRequest() + " from " + webConnection.getIpAddress() + " with payload POST: " + postRequest + ", GET: " + getRequest);*/
    }
}

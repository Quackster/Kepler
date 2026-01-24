package net.h4bbo.http.kepler.util;

import org.alexdev.duckhttpd.server.connection.WebConnection;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class XSSUtil {
    public static final String XSSKey = "xssKey";
    public static final String XSSSeed = "xssSeed";
    public static final String XSSRequested = "xssRequested";

    public static boolean verifyKey(WebConnection connection, String verifyRouteRequest) {
        if (!connection.session().contains(XSSKey) ||
            !connection.session().contains(XSSSeed) ||
            !connection.session().contains(XSSRequested)) {
            clear(connection);
            return false;
        }

        var expectedRouteRequest = connection.session().getString(XSSRequested);

        if (!verifyRouteRequest.equalsIgnoreCase(expectedRouteRequest)) {
            clear(connection);
            return false;
        }

        var random = new Random(connection.session().getInt(XSSSeed));
        var key = random.nextInt();

        if (key != connection.session().getInt(XSSKey)) {
            clear(connection);
            return false;
        }

        clear(connection);
        return true;
    }

    public static void createKey(WebConnection connection, String expectedRouteRequest) {
        clear(connection);

        var seed = ThreadLocalRandom.current().nextInt();
        var key = new Random(seed).nextInt();

        connection.session().set(XSSKey, key);
        connection.session().set(XSSSeed, seed);
        connection.session().set(XSSRequested, expectedRouteRequest);
    }

    public static void clear(WebConnection connection) {
        if (connection.session().contains(XSSKey) ||
            connection.session().contains(XSSSeed) ||
            connection.session().contains(XSSRequested)) {
            connection.session().delete(XSSSeed);
            connection.session().delete(XSSKey);
            connection.session().delete(XSSRequested);
        }
    }
}

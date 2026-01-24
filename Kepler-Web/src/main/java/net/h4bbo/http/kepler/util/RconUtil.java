package net.h4bbo.http.kepler.util;

import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.http.kepler.KeplerWeb;
import net.h4bbo.http.kepler.util.rcon.RconTask;

import java.util.Map;

public class RconUtil {
    public static void sendCommand(RconHeader header, Map<String, Object> parameters) {
        RconTask rconTask = new RconTask(header, parameters);
        KeplerWeb.getExecutor().execute(rconTask);
    }
}

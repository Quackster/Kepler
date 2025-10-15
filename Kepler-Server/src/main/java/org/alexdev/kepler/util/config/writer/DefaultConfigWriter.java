package org.alexdev.kepler.util.config.writer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DefaultConfigWriter implements ConfigWriter {
    @Override
    public Map<String, String>  setConfigurationDefaults() {
        Map<String, String> config = new HashMap<>();
        // DEFAULT settings
        config.put("server.bind", "127.0.0.1");
        config.put("server.port", "12321");

        config.put("mus.bind", "127.0.0.1");
        config.put("mus.port", "12322");

        config.put("rcon.bind", "127.0.0.1");
        config.put("rcon.port", "12309");

        config.put("log.connections", "true");
        config.put("log.sent.packets", "false");
        config.put("log.sent.packets.hex", "false");
        config.put("log.received.packets", "false");

        config.put("mysql.hostname", "127.0.0.1");
        config.put("mysql.port", "3306");
        config.put("mysql.user", "kepler");
        config.put("mysql.password", "verysecret");
        config.put("mysql.database", "kepler");

        config.put("debug", "false");
        return config;
    }

    @Override
    public void setConfigurationData(Map<String, String> config, PrintWriter writer) {
        writer.println("[Server]");
        writer.println("server.bind=" + config.get("server.bind"));
        writer.println("server.port=" + config.get("server.port"));
        writer.println("");
        writer.println("[Rcon]");
        writer.println("rcon.bind=" + config.get("rcon.bind"));
        writer.println("rcon.port=" + config.get("rcon.port"));
        writer.println("");
        writer.println("[Mus]");
        writer.println("mus.bind=" + config.get("mus.bind"));
        writer.println("mus.port=" + config.get("mus.port"));
        writer.println("");
        writer.println("[Database]");
        writer.println("mysql.hostname=" + config.get("mysql.hostname"));
        writer.println("mysql.port=" + config.get("mysql.port"));
        writer.println("mysql.user=" + config.get("mysql.user"));
        writer.println("mysql.password=" + config.get("mysql.password"));
        writer.println("mysql.database=" + config.get("mysql.database"));
        writer.println("");
        writer.println("[Logging]");
        writer.println("log.received.packets=" + config.get("log.received.packets"));
        writer.println("log.sent.packets=" + config.get("log.sent.packets"));
        writer.println("");
        writer.println("[Console]");
        writer.print("debug=" + config.get("debug"));
        writer.flush();
        writer.close();
    }
}

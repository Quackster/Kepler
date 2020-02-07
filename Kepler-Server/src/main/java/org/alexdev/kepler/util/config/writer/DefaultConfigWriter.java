package org.alexdev.kepler.util.config.writer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DefaultConfigWriter implements ConfigWriter {
    @Override
    public Map<String, String>  setConfigurationDefaults() {
        Map<String, String> config = new HashMap<>();
        // DEFAULT settings
        config.put("bind", "127.0.0.1");
        config.put("server.port", "12321");
        config.put("server.limit.bandwidth", "false");//String.valueOf(40*1024));
        config.put("server.limit.bandwidth.amount", String.valueOf(40*1024));
        config.put("mus.port", "12322");
        config.put("rcon.bind", "127.0.0.1");
        config.put("rcon.port", "12309");

        config.put("log.connections", "true");
        config.put("log.sent.packets", "false");
        config.put("log.received.packets", "false");

        config.put("mysql.hostname", "127.0.0.1");
        config.put("mysql.port", "3306");
        config.put("mysql.username", "kepler");
        config.put("mysql.password", "verysecret");
        config.put("mysql.database", "kepler");

        config.put("v9.version.port", "0");
        config.put("v14.version.port", "12321");
        config.put("v15.version.port", "0");
        config.put("v21.version.port", "0");
        config.put("v26.version.port", "0");

        config.put("debug", "false");
        return config;
    }

    @Override
    public void setConfigurationData(Map<String, String> config, PrintWriter writer) {
        writer.println("[Global]");
        writer.println("bind=" + config.get("bind"));
        writer.println("v9.version.port=" + config.get("v9.version.port"));
        writer.println("v14.version.port=" + config.get("v14.version.port"));
        writer.println("v15.version.port=" + config.get("v15.version.port"));
        writer.println("v21.version.port=" + config.get("v21.version.port"));
        writer.println("v26.version.port=" + config.get("v26.version.port"));
        writer.println("");
        writer.println("[Server]");
        writer.println("server.port=" + config.get("server.port"));
        writer.println("server.limit.bandwidth=" + config.get("server.limit.bandwidth"));
        writer.println("server.limit.bandwidth.amount=" + config.get("server.limit.bandwidth.amount"));
        writer.println("");
        writer.println("[Rcon]");
        writer.println("rcon.bind=" + config.get("rcon.bind"));
        writer.println("rcon.port=" + config.get("rcon.port"));
        writer.println("");
        writer.println("[Mus]");
        writer.println("mus.port=" + config.get("mus.port"));
        writer.println("");
        writer.println("[Database]");
        writer.println("mysql.hostname=" + config.get("mysql.hostname"));
        writer.println("mysql.port=" + config.get("mysql.port"));
        writer.println("mysql.username=" + config.get("mysql.username"));
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

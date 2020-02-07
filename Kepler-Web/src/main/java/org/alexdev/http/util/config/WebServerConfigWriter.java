package org.alexdev.http.util.config;

import org.alexdev.kepler.util.config.writer.ConfigWriter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class WebServerConfigWriter implements ConfigWriter {
    @Override
    public Map<String, String>  setConfigurationDefaults() {
        Map<String, String> config = new HashMap<>();
        // DEFAULT settings
        config.put("site.directory", "tools/www");

        config.put("bind.ip", "127.0.0.1");
        config.put("bind.port", "80");

        config.put("rcon.ip", "127.0.0.1");
        config.put("rcon.port", "12309");

        config.put("mysql.hostname", "127.0.0.1");
        config.put("mysql.port", "3306");
        config.put("mysql.username", "root");
        config.put("mysql.password", "verysecret");
        config.put("mysql.database", "root");

        config.put("template.directory", "tools/www-tpl");
        config.put("template.name", "default");
        return config;
    }

    @Override
    public void setConfigurationData(Map<String, String> config, PrintWriter writer) {
        writer.println("[Site]");
        writer.println("site.directory=" + config.get("site.directory"));
        writer.println("");
        writer.println("[Global]");
        writer.println("bind.ip=" + config.get("bind.ip"));
        writer.println("bind.port=" + config.get("bind.port"));
        writer.println("");
        writer.println("[Database]");
        writer.println("mysql.hostname=" + config.get("mysql.hostname"));
        writer.println("mysql.port=" + config.get("mysql.port"));
        writer.println("mysql.username=" + config.get("mysql.username"));
        writer.println("mysql.password=" + config.get("mysql.password"));
        writer.println("mysql.database=" + config.get("mysql.database"));
        writer.println("");
        writer.println("[Template]");
        writer.println("template.directory=" + config.get("template.directory"));
        writer.println("template.name=" + config.get("template.name"));
        writer.flush();
        writer.close();
    }
}

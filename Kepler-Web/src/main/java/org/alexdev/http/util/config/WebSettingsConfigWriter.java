package org.alexdev.http.util.config;

import org.alexdev.kepler.util.config.writer.ConfigWriter;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class WebSettingsConfigWriter implements ConfigWriter {
    @Override
    public Map<String, String>  setConfigurationDefaults() {
        Map<String, String> config = new HashMap<>();
        config.put("site.name", "Habbo");
        config.put("site.path", "http://localhost");
        config.put("static.content.path", "http://localhost");

        config.putAll(new GameConfigWriter().setConfigurationDefaults());
        return config;
    }

    @Override
    public void setConfigurationData(Map<String, String> config, PrintWriter writer) {

    }
}

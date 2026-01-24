package net.h4bbo.kepler.util.config.writer;

import java.io.PrintWriter;
import java.util.Map;

public interface ConfigWriter {
    public Map<String, String> setConfigurationDefaults();
    public void setConfigurationData(Map<String, String> config, PrintWriter writer);
}

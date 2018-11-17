package org.alexdev.kepler.util.config;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    public static Map<String, String> load(String configPath) throws IOError, IOException, ConfigurationException {
        Map<String, String> config = new ConcurrentHashMap<>();
        Path path = Paths.get(configPath);

        INIConfiguration ini = new INIConfiguration();
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        ini.read(reader);

        Set<String> sectionNames = ini.getSections();
        //System.out.printf("Section names: %s", sectionNames.toString());

        for (String sectionName : sectionNames) {
            SubnodeConfiguration section = ini.getSection(sectionName);

            if (section != null) {
                Iterator<String> keys = section.getKeys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = section.getString(key);

                    if (value != null) {
                        key = key.replace("..", "."); // TODO: find a better way than this hack
                        config.put(key, value);
                    }
                }
            }
        }

        reader.close();

        return config;
    }

    /**
     * Create config file
     * @throws IOException the exception if the file couldn't be read/written to
     */
    public static PrintWriter createConfigurationFile(String configPath) throws IOException {
        File file = new File(configPath);

        if (!file.isFile() && file.createNewFile()) {
            return new PrintWriter(file.getAbsoluteFile());
        }

        return null;
    }

}

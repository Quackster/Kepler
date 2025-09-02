package net.h4bbo.kepler.util.config;

import net.h4bbo.kepler.util.config.writer.ConfigWriter;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfiguration {
    private static Logger log = LoggerFactory.getLogger(ServerConfiguration.class);
    private static Map<String, String> config = new ConcurrentHashMap<>();
    private static ConfigWriter writer;

    public static void load(String configPath) throws IOError, IOException, ConfigurationException {
        config = writer.setConfigurationDefaults();

        var configurationFile = Configuration.createConfigurationFile(configPath);

        if (configurationFile != null) {
            writer.setConfigurationData(config, configurationFile);
        }

        config = Configuration.load(configPath);
    }

    private static void setConfigurationDefaults() {
        // DEFAULT settings
        config.put("version", "26");
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

        config.put("debug", "false");
    }

    private static void loadEnvironmentConfiguration() {
        String envBind = System.getenv("KEPLER_BIND");

        if (envBind != null) {
            try {
                config.put("bind", InetAddress.getByName(envBind).getHostAddress());
            } catch (UnknownHostException e) {
                log.warn("Could not use {} as bind for game server, reverting to default {}", envBind, config.get("bind"));
            }
        }

        String envPort = System.getenv("KEPLER_PORT");

        if (envPort != null) {
            int parsedPort = Integer.parseUnsignedInt(envPort);
            if (parsedPort > 0) {
                config.put("server.port", Integer.toString(parsedPort));
            }
        }

        String envMusPort = System.getenv("KEPLER_MUS_PORT");

        if (envMusPort != null) {
            int parsedPort = Integer.parseUnsignedInt(envMusPort);
            if (parsedPort > 0) {
                config.put("mus.port", Integer.toString(parsedPort));
            }
        }

        String envRconBind = System.getenv("KEPLER_RCON_BIND");

        if (envRconBind != null) {
            try {
                config.put("rcon.bind", InetAddress.getByName(envRconBind).getHostAddress());
            } catch (UnknownHostException e) {
                // Ignore, will revert to default
                log.warn("Could not use {} as bind for RCON server, reverting to default {}", envRconBind, config.get("rcon.bind"));
            }
        }

        String envRconPort = System.getenv("KEPLER_RCON_PORT");

        if (envRconPort != null) {
            int parsedPort = Integer.parseUnsignedInt(envRconPort);
            if (parsedPort > 0) {
                config.put("rcon.port", Integer.toString(parsedPort));
            }
        }

        String envMysqlHost = System.getenv("MYSQL_HOST");

        if (envMysqlHost != null) {
            try {
                config.put("mysql.hostname", InetAddress.getByName(envMysqlHost).getHostAddress());
            } catch (UnknownHostException e) {
                log.warn("Could not use {} as MariaDB host, reverting to default {}", envMysqlHost, config.get("mysql.hostname"));
            }
        }

        String envMysqlPort = System.getenv("MYSQL_PORT");

        if (envMysqlPort != null) {
            int parsedPort = Integer.parseUnsignedInt(envMysqlPort);
            if (parsedPort > 0) {
                config.put("mysql.port", Integer.toString(parsedPort));
            }
        }

        String envMysqlUser = System.getenv("MYSQL_USER");

        if (envMysqlUser != null) {
            config.put("mysql.username", envMysqlUser);
        }

        String envMysqlDatabase = System.getenv("MYSQL_DATABASE");

        if (envMysqlDatabase != null) {
            config.put("mysql.database", envMysqlDatabase);
        }

        String envMysqlPassword = System.getenv("MYSQL_PASSWORD");

        if (envMysqlPassword != null) {
            config.put("mysql.password", envMysqlPassword);
        }
    }

    /**
     * Writes default server configuration
     *
     * @param writer - {@link PrintWriter} the file writer
     */
    private static void setConfigurationData(PrintWriter writer) {
        writer.println("[Global]");
        writer.println("bind=" + config.get("bind"));
        writer.println("version=" + config.get("version"));
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


    /**
     * Get key from configuration and cast to an Boolean
     *
     * @param key the key to use
     * @return value as boolean
     */
    public static boolean getBoolean(String key) {
        String val = config.getOrDefault(key, "false");

        if (val.equalsIgnoreCase("true")) {
            return true;
        }

        if (val.equals("1")) {
            return true;
        }

        return val.equalsIgnoreCase("yes");

    }

    /**
     * Helper method to get value from environment variable if exists
     * Converts the key by replacing dots with underscores and converting to uppercase 
     * to match common environment variable naming conventions.
     *
     * @param key the key to use
     * @return value from environment variable or null if not found
     */
    private static String getEnvOrNull(String key) {
        String envKey = key.replace('.', '_').toUpperCase();
        String envValue = System.getenv(envKey);

        if(envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        return null;
    }

    /**
     * Get value from configuration
     *
     * @param key the key to use
     * @return value
     */
    public static String getString(String key) {
        String envValue = getEnvOrNull(key);

        if(envValue != null) {
            return envValue;
        }

        return config.getOrDefault(key, key);
    }

    /**
     * Get value from configuration
     *
     * @param key the key to use
     * @return value
     */
    public static String getStringOrDefault(String key, String value) {
        String envValue = getEnvOrNull(key);

        if(envValue != null) {
            return envValue;
        }

        return config.getOrDefault(key, value);
    }

    /**
     * Get value from configuration and cast to an Integer
     *
     * @param key the key to use
     * @return value as int
     */
    public static int getInteger(String key) {
        String envValue = getEnvOrNull(key);

        if(envValue != null) {
            try {
                return Integer.parseInt(envValue);
            } catch (NumberFormatException e) {
                // Handle the case where the env value is not a valid integer
                System.err.println("Environment variable for key " + key + " is not a valid integer: " + envValue);
                // Using default value below
            }
        }

        return Integer.parseInt(config.getOrDefault(key, "0"));
    }


    /**
     * Get if key exists.
     *
     * @param key to check
     * @return true if exists
     */
    public static boolean exists(String key) {
        return config.containsKey(key);
    }

    public static void setWriter(ConfigWriter writer) {
        ServerConfiguration.writer = writer;
    }
}

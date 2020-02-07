package org.alexdev.http;

import com.google.gson.Gson;
import io.netty.util.ResourceLeakDetector;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.http.log.Log;
import org.alexdev.http.server.ServerResponses;
import org.alexdev.http.server.Watchdog;
import org.alexdev.http.template.TwigTemplate;
import org.alexdev.http.util.config.WebLoggingConfiguration;
import org.alexdev.http.util.config.WebServerConfigWriter;
import org.alexdev.http.util.config.WebSettingsConfigWriter;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KeplerWeb {
    private static Logger logger = LoggerFactory.getLogger(KeplerWeb.class);
    private static final Gson gson = new Gson();
    private static ScheduledExecutorService scheduler;

    public static void main(String[] args) throws Exception {
        WebLoggingConfiguration.checkLoggingConfig();

        ServerConfiguration.setWriter(new WebServerConfigWriter());
        ServerConfiguration.load("webserver-config.ini");

        logger.info("HavanaWeb by Quackster");
        logger.info("Loading configuration..");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        Settings settings = Settings.getInstance();
        settings.setSiteDirectory(ServerConfiguration.getString("site.directory"));
        settings.setDefaultResponses(new ServerResponses());
        settings.setTemplateBase(TwigTemplate.class);

        if (!Storage.connect()) {
            Log.getErrorLogger().error("Could not connect to MySQL");
            return;
        }

        GameConfiguration.getInstance(new WebSettingsConfigWriter());

        scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        scheduler.scheduleWithFixedDelay(new Watchdog(), 1, 1, TimeUnit.SECONDS);

        logger.info("Registering web routes..");
        Routes.register();
        logger.info("Registered " + RouteManager.getRoutes().size() + " route(s)!");

        int port = ServerConfiguration.getInteger("bind.port");
        logger.info("Starting http service on port " + port);

        setupTemplateSystem();
        WebServer instance = new WebServer(port);
        instance.start();
    }

    /**
     * Boots up JTwig engine.
     */
    private static void setupTemplateSystem() {
        var template = JtwigTemplate.inlineTemplate("test");
        var model = JtwigModel.newModel();
        model.with("test", "HavanaWeb");
        template.render(model);
    }
}

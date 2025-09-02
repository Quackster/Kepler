package net.h4bbo.http.kepler;

import com.google.gson.Gson;
import io.netty.util.ResourceLeakDetector;
import net.h4bbo.kepler.dao.Storage;
import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.config.GameConfiguration;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.WebServer;
import org.alexdev.duckhttpd.util.config.Settings;
import net.h4bbo.http.kepler.game.news.NewsManager;
import net.h4bbo.http.kepler.game.stickers.StickerManager;
import net.h4bbo.http.kepler.log.Log;
import net.h4bbo.http.kepler.server.ServerResponses;
import net.h4bbo.http.kepler.server.Watchdog;
import net.h4bbo.http.kepler.template.TwigTemplate;
import net.h4bbo.http.kepler.util.config.WebLoggingConfiguration;
import net.h4bbo.http.kepler.util.config.WebServerConfigWriter;
import net.h4bbo.http.kepler.util.config.WebSettingsConfigWriter;
import net.h4bbo.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KeplerWeb {
    private static Logger logger = LoggerFactory.getLogger(KeplerWeb.class);

    private static final Gson gson = new Gson();
    private static ScheduledExecutorService scheduler;
    private static ExecutorService executor;

    public static void main(String[] args) throws Exception {
        WebLoggingConfiguration.checkLoggingConfig();
        ServerConfiguration.setWriter(new WebServerConfigWriter());
        ServerConfiguration.load("webserver-config.ini");

        logger.info("KeplerWeb by Quackster");
        logger.info("Loading configuration..");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        Settings settings = Settings.getInstance();
        settings.setSiteDirectory(ServerConfiguration.getString("site.directory"));
        settings.setDefaultResponses(new ServerResponses());
        settings.setTemplateBase(TwigTemplate.class);
        settings.setSaveSessions(true);

        if (ServerConfiguration.getString("page.encoding").length() > 0) {
            settings.setPageEncoding(ServerConfiguration.getString("page.encoding"));
        }

        // Spammers
        /*Settings.getInstance().getBlockIpv4().add("192.190");
        Settings.getInstance().getBlockIpv4().add("79.108");
        Settings.getInstance().getBlockIpv4().add("194.59");
        Settings.getInstance().getBlockIpv4().add("185.189");
        Settings.getInstance().getBlockIpv4().add("212.8");
        Settings.getInstance().getBlockIpv4().add("104.250");
        */

        if (!Storage.connect()) {
            Log.getErrorLogger().error("Could not connect to MySQL");
            return;
        }

        GameConfiguration.getInstance(new WebSettingsConfigWriter());

        /*byte[] pw = "lol123".getBytes(StandardCharsets.UTF_8);
        byte[] outputHash = new byte[PwHash.STR_BYTES];
        PwHash.Native pwHash = (PwHash.Native) PlayerDao.LIB_SODIUM;
        boolean success = pwHash.cryptoPwHashStr(
                outputHash,
                pw,
                pw.length,
                PwHash.OPSLIMIT_INTERACTIVE,
                PwHash.MEMLIMIT_INTERACTIVE
        );
        System.out.println(new String(outputHash));*/

        WordfilterManager.getInstance();
        StickerManager.getInstance();
        ItemManager.getInstance();
        NewsManager.getInstance();

        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        scheduler.scheduleWithFixedDelay(new Watchdog(), 1, 1, TimeUnit.SECONDS);

        logger.info("Registering web routes..");
        //logger.info(EmailUtil.renderRegistered("Alex", "01/01/1970", UUID.randomUUID().toString()));

        Routes.register();
        logger.info("Registered " + RouteManager.getRoutes().size() + " route(s)!");

        int port = ServerConfiguration.getInteger("bind.port");
        logger.info("Starting http service on port " + port);

        WebServer instance = new WebServer(port);
        instance.start();
    }

    /**
     * Boots up JTwig engine.
     */
    /*private static void setupTemplateSystem() {
        var template = JtwigTemplate.inlineTemplate("test");
        var model = JtwigModel.newModel();
        model.with("test", "HavanaWeb");
        template.render(model);
    }*/

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static Gson getGson() {
        return gson;
    }

    public static long hashSpriteName(String name) {
        name = name.toUpperCase();
        long hash = 0;
        for (int index = 0; index < name.length(); index++) {
            hash = hash * 61 + name.charAt(index) - 32;
            hash = hash + (hash >> 56) & 0xFFFFFFFFFFFFFFL;
        }

        return hash;
    }
}

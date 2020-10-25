package org.alexdev.kepler;

import io.netty.util.ResourceLeakDetector;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.catalogue.RareManager;
import org.alexdev.kepler.game.commands.CommandManager;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.fuserights.FuserightsManager;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.moderation.ChatManager;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.recycler.RecyclerManager;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.models.RoomModelManager;
import org.alexdev.kepler.game.room.public_rooms.walkways.WalkwaysManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.MessageHandler;
import org.alexdev.kepler.server.mus.MusServer;
import org.alexdev.kepler.server.netty.NettyServer;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.LoggingConfiguration;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.alexdev.kepler.util.config.writer.DefaultConfigWriter;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;

public class Kepler {

    private static long startupTime;

    private static String serverIP;
    private static int serverPort;

    private static String musServerIP;
    private static int musServerPort;

    private static String rconIP;
    private static int rconPort;

    private static boolean isShutdown;
    
    private static NettyServer server;
    private static MusServer musServer;

    private static Logger log;

    /**
     * Main call of Java application
     * @param args System arguments
     */
    public static void main(String[] args) {
        startupTime = DateUtil.getCurrentTimeSeconds();

        try {
            LoggingConfiguration.checkLoggingConfig();

            ServerConfiguration.setWriter(new DefaultConfigWriter());
            ServerConfiguration.load("server.ini");

            log = LoggerFactory.getLogger(Kepler.class);
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

            System.out.println("  _  __          _           \n" +
                    " | |/ /___ _ __ | | ___ _ __ \n" +
                    " | ' // _ \\ '_ \\| |/ _ \\ '__|\n" +
                    " | . \\  __/ |_) | |  __/ |   \n" +
                    " |_|\\_\\___| .__/|_|\\___|_|   \n" +
                    "          |_|                ");

            log.info("Kepler - Habbo Hotel Emulation (max version supported: v26)");

            if (!Storage.connect()) {
                return;
            }

            log.info("Setting up game");
            //log.info(REGISTER.createPassword("lol"));

            GameConfiguration.getInstance(new GameConfigWriter());
            WalkwaysManager.getInstance();
            ItemManager.getInstance();
            CatalogueManager.getInstance();
            RareManager.getInstance();
            RoomModelManager.getInstance();
            RoomManager.getInstance();
            PlayerManager.getInstance();
            BotManager.getInstance();
            EventsManager.getInstance();
            FuserightsManager.getInstance();
            NavigatorManager.getInstance();
            ChatManager.getInstance();
            GameScheduler.getInstance();
            GameManager.getInstance();
            CommandManager.getInstance();
            MessageHandler.getInstance();
            TextsManager.getInstance();
            RecyclerManager.getInstance();
            InfobusManager.getInstance();

            // Update players online back to 0
            SettingsDao.updateSetting("players.online", "0");

            setupMus();
            setupServer();

            /*Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                sqlConnection = Storage.getStorage().getConnection();
                preparedStatement = Storage.getStorage().prepare("SELECT * FROM items_definitions WHERE sprite LIKE '%arabian%'", sqlConnection);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getString("behaviour").contains("wall_item")) {
                        System.out.print("wallitem_");
                    } else {
                        System.out.println("furni");
                    }

                    System.out.println("_" + resultSet.getString("sprite") + "_name=" + resultSet.getString("name"));

                    if (resultSet.getString("behaviour").contains("wall_item")) {
                        System.out.print("wallitem");
                    } else {
                        System.out.println("furni");
                    }

                    System.out.println("_" + resultSet.getString("sprite") + "_desc=" + resultSet.getString("description"));
                }
            } catch (Exception e) {
                Storage.logError(e);
            } finally {
                Storage.closeSilently(resultSet);
                Storage.closeSilently(preparedStatement);
                Storage.closeSilently(sqlConnection);
            }*/

            Runtime.getRuntime().addShutdownHook(new Thread(Kepler::dispose));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int bitLeft(int n, int s) {
        return (int) (n * Math.pow(2, Math.floorMod(s, 32)));
    }

    private static void setupServer() throws UnknownHostException {
        String serverIP = ServerConfiguration.getString("bind");

        if (serverIP.length() == 0) {
            log.error("Game server bind address is not provided");
            return;
        }

        serverPort = ServerConfiguration.getInteger("server.port");

        if (serverPort == 0) {
            log.error("Game server port not provided");
            return;
        }

        server = new NettyServer(serverIP, serverPort);
        server.createSocket();
        server.bind();
    }

    private static void setupMus() throws UnknownHostException {
        String musBind = ServerConfiguration.getString("bind");

        if (musBind.length() == 0) {
            log.error("Multi User Server (MUS) bind address is not provided");
            return;
        }

        musServerPort = ServerConfiguration.getInteger("mus.port");

        if (musServerPort == 0) {
            log.error("Multi User Server (MUS) port not provided");
            return;
        }

        musServer = new MusServer(musBind, musServerPort);
        musServer.createSocket();
        musServer.bind();
    }

    private static void dispose() {
        try {

            log.info("Shutting down server!");
            isShutdown = true;

            // TODO: all the managers
            ChatManager.getInstance().performChatSaving();
            
            GameScheduler.getInstance().performItemSaving();
            GameScheduler.getInstance().performItemDeletion();

            PlayerManager.getInstance().dispose();

            server.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the interface to the server handler
     * 
     * @return {@link NettyServer} interface
     */
    public static NettyServer getServer() {
        return server;
    }

    /**
     * Returns the interface to the server handler
     *
     * @return {@link NettyServer} interface
     */


    /**
     * Gets the server IPv4 IP address it is currently (or attempting to) listen on
     * @return IP as string
     */
    public static String getServerIP() {
        return serverIP;
    }

    /**
     * Gets the server port it is currently (or attempting to) listen on
     * @return string of IP
     */
    public static int getServerPort() {
        return serverPort;
    }

    /**
     * Gets the rcon IPv4 IP address it is currently (or attempting to) listen on
     * @return IP as string
     */
    public static String getRconIP() {
        return rconIP;
    }

    /**
     * Gets the rcon port it is currently (or attempting to) listen on
     * @return string of IP
     */
    public static int getRconPort() {
        return rconPort;
    }

    /**
     * Gets the startup time.
     *
     * @return the startup time
     */
    public static long getStartupTime() {
        return startupTime;
    }

    /**
     * Are we shutting down?
     *
     * @return boolean yes/no
     */
    public static boolean isShuttingdown() {
        return isShutdown;
    }
}
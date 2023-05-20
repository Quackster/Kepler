package org.alexdev.kepler;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import io.netty.util.ResourceLeakDetector;
import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.ads.AdManager;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.commandqueue.CommandQueue;
import org.alexdev.kepler.game.commandqueue.CommandQueueManager;
import org.alexdev.kepler.game.commandqueue.CommandType;
import org.alexdev.kepler.game.commands.CommandManager;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.snowstorm.SnowStormMapsManager;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.moderation.ChatManager;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.recycler.RecyclerManager;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.handlers.walkways.WalkwaysManager;
import org.alexdev.kepler.game.room.models.RoomModelManager;
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
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Kepler {

    private static long startupTime;

    private static String serverIP;
    private static int serverPort;

    private static String musServerIP;
    private static int musServerPort;

    private static boolean isShutdown;

    private static NettyServer server;
    private static MusServer musServer;
    private static Logger log;

    private static LazySodiumJava LIB_SODIUM;

    public static final String SERVER_VERSION = "v2";

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

            System.out.println("   ___   ______   _____  ____  _____  \n" +
                    " .'   `.|_   _ `.|_   _||_   \\|_   _| \n" +
                    "/  .-.  \\ | | `. \\ | |    |   \\ | |   \n" +
                    "| |   | | | |  | | | |    | |\\ \\| |   \n" +
                    "\\  `-'  /_| |_.' /_| |_  _| |_\\   |_  \n" +
                    " `.___.'|______.'|_____||_____|\\____| \n" +
                    "                                      ");

            log.info("Odin - Habbo Hotel Emulation (revision " + SERVER_VERSION + ")");

            if (!Storage.connect()) {
                return;
            }

            log.info("Setting up game");

            GameConfiguration.getInstance(new GameConfigWriter());
            AdManager.getInstance();
            WalkwaysManager.getInstance();
            ItemManager.getInstance();
            CatalogueManager.getInstance();
            RoomModelManager.getInstance();
            RoomManager.getInstance();
            PlayerManager.getInstance();
            BotManager.getInstance();
            EventsManager.getInstance();
            NavigatorManager.getInstance();
            ChatManager.getInstance();
            SnowStormMapsManager.getInstance();
            GameScheduler.getInstance();
            GameManager.getInstance();
            CommandManager.getInstance();
            MessageHandler.getInstance();
            TextsManager.getInstance();
            RecyclerManager.getInstance();
            InfobusManager.getInstance();

            // Update players online back to 0
            SettingsDao.updateSetting("players.online", "0");

            log.info("Using Argon2 password hashing algorithm");
            LIB_SODIUM  = new LazySodiumJava(new SodiumJava());

            setupMus();
            setupServer();
            setupRabbitMQ();

            Runtime.getRuntime().addShutdownHook(new Thread(Kepler::dispose));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupRabbitMQ() {
        try {
            String exchangeName = "commands";
            String rabbitMQServer = ServerConfiguration.getString("rabbitmq.hostname");
            int rabbitMQPort = ServerConfiguration.getInteger("rabbitmq.port");
            String rabbitMQUsername = ServerConfiguration.getString("rabbitmq.username");
            String rabbitMQPassword = ServerConfiguration.getString("rabbitmq.password");

            if(rabbitMQServer.length() == 0 || rabbitMQPort == 0 || rabbitMQUsername.length() == 0 || rabbitMQPassword.length() == 0) {
                log.error("RabbitMQ hostname, port, username or password not provided");
                return;
            }
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitMQServer);
            factory.setPort(rabbitMQPort);
            factory.setUsername(rabbitMQUsername);
            factory.setPassword(rabbitMQPassword);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "direct", true);
            String queueName = channel.queueDeclare("command_queue", false, false, false, null).getQueue();
            log.info("[RabbitMQ] Waiting for messages");

            for (CommandType commandType: CommandType.values()) {
                channel.queueBind(queueName, "commands", commandType.getCommandName());
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                log.info("[RabbitMQ] Received '" + message + "'");
                CommandQueueManager.getInstance().handleCommand(new CommandQueue (delivery.getEnvelope().getRoutingKey(), message));
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch(Exception e) {
            log.error("Failed to setup RabbitMQ", e);
        }

    }

    private static void setupServer() {
        String serverIP = ServerConfiguration.getString("server.bind");

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

    private static void setupMus() {
        musServerIP = ServerConfiguration.getString("mus.bind");

        if (musServerIP.length() == 0) {
            log.error("Multi User Server (MUS) bind address is not provided");
            return;
        }

        musServerPort = ServerConfiguration.getInteger("mus.port");

        if (musServerPort == 0) {
            log.error("Multi User Server (MUS) port not provided");
            return;
        }

        musServer = new MusServer(musServerIP, musServerPort);
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

    public static LazySodiumJava getLibSodium() {
        return LIB_SODIUM;
    }
}

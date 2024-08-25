package org.alexdev.kepler.server.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HabboActivityQueueSingleton {
    private static final Logger log = LoggerFactory.getLogger(HabboActivityQueueSingleton.class);
    private static HabboActivityQueueSingleton instance;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private static final String exchangeName = "habbo_activity";
    private static final String queueName = "activity";

    private HabboActivityQueueSingleton() {
        try {
            String rabbitMQServer = ServerConfiguration.getString("rabbitmq.hostname");
            int rabbitMQPort = ServerConfiguration.getInteger("rabbitmq.port");
            String rabbitMQUsername = ServerConfiguration.getString("rabbitmq.username");
            String rabbitMQPassword = ServerConfiguration.getString("rabbitmq.password");

            if (rabbitMQServer.isEmpty() || rabbitMQPort == 0 || rabbitMQUsername.isEmpty() || rabbitMQPassword.isEmpty()) {
                log.error("RabbitMQ hostname, port, username or password not provided");
                throw new IllegalArgumentException("RabbitMQ configuration is incomplete");
            }

            factory = new ConnectionFactory();
            factory.setHost(rabbitMQServer);
            factory.setPort(rabbitMQPort);
            factory.setUsername(rabbitMQUsername);
            factory.setPassword(rabbitMQPassword);
            Map<String, Object> clientProperties = new HashMap<>();
            clientProperties.put("connection_name", "HabboActivityQueueSingleton");
            factory.setClientProperties(clientProperties);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "direct", true);
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, exchangeName, "chat");
        } catch (Exception e) {
            log.error("Failed to initialize RabbitMQ connection factory", e);
        }
    }

    public static synchronized HabboActivityQueueSingleton getInstance() {
        if (instance == null) {
            instance = new HabboActivityQueueSingleton();
        }
        return instance;
    }

    public void publishMessage(String routingKey, String message) {
        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Failed to publish message to RabbitMQ", e);
        }
    }

    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            log.error("Failed to close RabbitMQ connection/channel", e);
        }
    }
}
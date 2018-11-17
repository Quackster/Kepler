package org.alexdev.kepler.server.rcon;

import io.grpc.Server;

import io.grpc.netty.NettyServerBuilder;
import org.alexdev.kepler.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RconServer {
    final private static Logger log = LoggerFactory.getLogger(RconServer.class);

    private String ip;
    private int port;
    private Server server;

    public RconServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void listen() throws IOException {
        this.server = NettyServerBuilder.forAddress(new InetSocketAddress(this.ip, this.port))
                .addService(new RconImpl())
                .build()
                .start();

        log.info("Remote control (RCON) server is listening on " + ip + ":" + port);
    }

    public void dispose() throws InterruptedException {
        this.server.shutdownNow();
        this.server.awaitTermination();
    }
}

package org.alexdev.kepler.server.netty.connections;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.MessageHandler;
import org.alexdev.kepler.messages.outgoing.handshake.HELLO;
import org.alexdev.kepler.dao.mysql.BanDao;
import org.alexdev.kepler.messages.outgoing.moderation.USER_BANNED;
import org.alexdev.kepler.server.netty.NettyPlayerNetwork;
import org.alexdev.kepler.server.netty.NettyServer;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConnectionHandler extends SimpleChannelInboundHandler<NettyRequest> {
    final private static Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    final private NettyServer server;

    public ConnectionHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        int maxConnectionsPerIp = GameConfiguration.getInstance().getInteger("max.connections.per.ip");
        String ipAddress = NettyPlayerNetwork.getIpAddress(ctx.channel());



        if (maxConnectionsPerIp > 0) {
            int count = 0;

            for (Channel channel : this.server.getChannels()) {
                String connectedIpAddress = NettyPlayerNetwork.getIpAddress(channel);

                if (connectedIpAddress.equals(ipAddress)) {
                    count++;
                }
            }

            if (count >= maxConnectionsPerIp) {
                log.info("Kicking off connection from " + ipAddress + " to make room for new connection");
                ctx.channel().close();
            }
        }

        Player player = new Player(new NettyPlayerNetwork(ctx.channel(), this.server.getConnectionIds().getAndIncrement()));
        ctx.channel().attr(Player.PLAYER_KEY).set(player);

        if (!this.server.getChannels().add(ctx.channel()) || Kepler.isShuttingdown()) {
            Log.getErrorLogger().error("Could not accept connection from {}", NettyPlayerNetwork.getIpAddress(ctx.channel()));
            ctx.close();
            return;
        }

        // TODO: IP ban checking
        var ipBanCheck = BanDao.hasBan(ipAddress, 0);

        if (ipBanCheck != null && ipBanCheck.getBanType() == BanType.IP_ADDRESS) {
            player.send(new USER_BANNED(ipBanCheck.getReason()));
        }

        player.send(new HELLO());

        //if (ServerConfiguration.getBoolean("log.connections")) {
        log.info("[{}] Connection from {} ", player.getNetwork().getConnectionId(), NettyPlayerNetwork.getIpAddress(ctx.channel()));
        //}
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.server.getConnectionIds().getAndDecrement(); // Decrement because we don't want it to reach Integer.MAX_VALUE
        this.server.getChannels().remove(ctx.channel());

        Player player = ctx.channel().attr(Player.PLAYER_KEY).get();

        if (player != null) {
            player.dispose();

            //if (ServerConfiguration.getBoolean("log.connections")) {
            log.info("[{}] Disconnection from {} ", player.getNetwork().getConnectionId(), NettyPlayerNetwork.getIpAddress(ctx.channel()));
            //}
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NettyRequest message) throws Exception {
        try {
            Player player = ctx.channel().attr(Player.PLAYER_KEY).get();

            if (player == null) {
                Log.getErrorLogger().error("Player was null from {}", ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0]);
                return;
            }

            if (message == null) {
                Log.getErrorLogger().error("Receiving message was null from {}", ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0]);
                return;
            }

            MessageHandler.getInstance().handleRequest(player, message);
        } catch (Exception ex) {
            Log.getErrorLogger().error("Exception occurred when handling (" + message.getHeaderId() + "): ", ex);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //if (cause instanceof Exception) {
        if (!(cause instanceof IOException)) {
            Log.getErrorLogger().error("Netty error occurred: ", cause); //ctx.close();
        }
        //}
    }
}
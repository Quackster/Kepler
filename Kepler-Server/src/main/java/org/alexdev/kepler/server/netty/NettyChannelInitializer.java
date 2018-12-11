package org.alexdev.kepler.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import org.alexdev.kepler.server.netty.codec.NetworkDecoder;
import org.alexdev.kepler.server.netty.codec.NetworkEncoder;
import org.alexdev.kepler.server.netty.connections.ConnectionHandler;
import org.alexdev.kepler.server.netty.connections.IdleConnectionHandler;
import org.alexdev.kepler.util.config.GameConfiguration;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final NettyServer nettyServer;
    //private final long readLimit = 40*1024;
    //private final long writeLimit = 25*1024;

    public NettyChannelInitializer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        if (GameConfiguration.getInstance().getBoolean("server.limit.bandwidth")) {
            long bandwidthLimit = GameConfiguration.getInstance().getLong("server.limit.bandwidth.amount");
            pipeline.addLast("trafficShapingHandler", new ChannelTrafficShapingHandler(bandwidthLimit, bandwidthLimit));
        }

        pipeline.addLast("gameEncoder", new NetworkEncoder());
        pipeline.addLast("gameDecoder", new NetworkDecoder());
        pipeline.addLast("handler", new ConnectionHandler(this.nettyServer));
        pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
        pipeline.addLast("idleHandler", new IdleConnectionHandler());
    }
}

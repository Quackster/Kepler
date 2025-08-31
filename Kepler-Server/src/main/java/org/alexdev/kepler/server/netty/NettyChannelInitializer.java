package org.alexdev.kepler.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.alexdev.kepler.server.netty.codec.NetworkDecoder;
import org.alexdev.kepler.server.netty.codec.NetworkEncoder;
import org.alexdev.kepler.server.netty.connections.ConnectionHandler;
import org.alexdev.kepler.server.netty.connections.IdleConnectionHandler;

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
        pipeline.addLast("gameEncoder", new NetworkEncoder());
        pipeline.addLast("gameDecoder", new NetworkDecoder());
        pipeline.addLast("handler", new ConnectionHandler(this.nettyServer));
        pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
        pipeline.addLast("idleHandler", new IdleConnectionHandler());
    }
}

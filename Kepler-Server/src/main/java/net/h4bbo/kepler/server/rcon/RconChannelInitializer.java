package net.h4bbo.kepler.server.rcon;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.h4bbo.kepler.server.rcon.codec.RconNetworkDecoder;

public class RconChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final RconServer musServer;

    public RconChannelInitializer(RconServer musServer) {
        this.musServer = musServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("gameDecoder", new RconNetworkDecoder());
        pipeline.addLast("handler", new RconConnectionHandler(this.musServer));
    }
}

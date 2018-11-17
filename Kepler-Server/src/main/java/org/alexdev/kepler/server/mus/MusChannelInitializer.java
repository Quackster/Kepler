package org.alexdev.kepler.server.mus;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.alexdev.kepler.server.mus.codec.MusNetworkDecoder;
import org.alexdev.kepler.server.mus.codec.MusNetworkEncoder;

public class MusChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final MusServer musServer;

    public MusChannelInitializer(MusServer musServer) {
        this.musServer = musServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1683226630, 2, 4, 0, 0));
        pipeline.addLast("gameDecoder", new MusNetworkDecoder());
        pipeline.addLast("gameEncoder", new MusNetworkEncoder());
        pipeline.addLast("handler", new MusConnectionHandler(this.musServer));
    }
}

package org.alexdev.kepler.server.mus;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.alexdev.kepler.server.mus.codec.MusNetworkDecoder;
import org.alexdev.kepler.server.mus.codec.MusNetworkEncoder;
import org.alexdev.kepler.server.netty.codec.websocket.ProtocolDetector;
import org.alexdev.kepler.server.netty.codec.websocket.WebSocketBinaryFrameCodec;

public class MusChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final MusServer musServer;

    public MusChannelInitializer(MusServer musServer) {
        this.musServer = musServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("protocolDetector", new ProtocolDetector(
                this::configureWebSocket,
                this::configureNative
        ));
    }

    private void configureWebSocket(ChannelPipeline pipeline) {
        pipeline.addLast("httpCodec", new HttpServerCodec());
        pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("wsProtocol", new WebSocketServerProtocolHandler("/", null, true, 65536));
        pipeline.addLast("wsCodec", new WebSocketBinaryFrameCodec());
        configureNative(pipeline);
    }

    private void configureNative(ChannelPipeline pipeline) {
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1683226630, 2, 4, 0, 0));
        pipeline.addLast("gameDecoder", new MusNetworkDecoder());
        pipeline.addLast("gameEncoder", new MusNetworkEncoder());
        pipeline.addLast("handler", new MusConnectionHandler(this.musServer));
    }
}

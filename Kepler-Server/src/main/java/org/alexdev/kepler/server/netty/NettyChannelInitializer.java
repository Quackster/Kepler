package org.alexdev.kepler.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.alexdev.kepler.server.netty.codec.NetworkDecoder;
import org.alexdev.kepler.server.netty.codec.NetworkEncoder;
import org.alexdev.kepler.server.netty.codec.websocket.ProtocolDetector;
import org.alexdev.kepler.server.netty.codec.websocket.WebSocketBinaryFrameCodec;
import org.alexdev.kepler.server.netty.connections.ConnectionHandler;
import org.alexdev.kepler.server.netty.connections.IdleConnectionHandler;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final NettyServer nettyServer;

    public NettyChannelInitializer(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
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
        pipeline.addLast("gameEncoder", new NetworkEncoder());
        pipeline.addLast("gameDecoder", new NetworkDecoder());
        pipeline.addLast("handler", new ConnectionHandler(this.nettyServer));
        pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
        pipeline.addLast("idleHandler", new IdleConnectionHandler());
    }
}

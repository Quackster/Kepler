package org.alexdev.kepler.server.netty.codec.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebSocketBinaryFrameCodec extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketBinaryFrameCodec.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
        if (frame instanceof BinaryWebSocketFrame) {
            out.add(frame.content().retain());
        } else {
            log.warn("Received unexpected WebSocket frame type: {}", frame.getClass().getSimpleName());
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        out.add(new BinaryWebSocketFrame(msg.retain()));
    }
}

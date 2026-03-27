package org.alexdev.kepler.server.netty.codec.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class ProtocolDetector extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(ProtocolDetector.class);

    // "GET " in big-endian: 0x47 0x45 0x54 0x20
    private static final int HTTP_GET_MAGIC = 0x47455420;

    private final Consumer<ChannelPipeline> webSocketConfigurer;
    private final Consumer<ChannelPipeline> nativeConfigurer;

    public ProtocolDetector(Consumer<ChannelPipeline> webSocketConfigurer,
                            Consumer<ChannelPipeline> nativeConfigurer) {
        this.webSocketConfigurer = webSocketConfigurer;
        this.nativeConfigurer = nativeConfigurer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }

        int magic = in.getInt(in.readerIndex());
        ChannelPipeline pipeline = ctx.pipeline();

        if (magic == HTTP_GET_MAGIC) {
            log.info("WebSocket connection detected from {}", ctx.channel().remoteAddress());
            webSocketConfigurer.accept(pipeline);
        } else {
            nativeConfigurer.accept(pipeline);
        }

        pipeline.remove(this);
    }
}

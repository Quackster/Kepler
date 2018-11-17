package org.alexdev.kepler.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.encoding.Base64Encoding;

import java.util.List;

public class NetworkDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (buffer.readableBytes() < 5) {
            // If the incoming data is less than 5 bytes, it's junk.
            return;
        }

        buffer.markReaderIndex();
        int length = Base64Encoding.decode(new byte[]{buffer.readByte(), buffer.readByte(), buffer.readByte()});

        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        if (length < 0) {
            return;
        }

        out.add(new NettyRequest(buffer.readBytes(length)));
    }
}
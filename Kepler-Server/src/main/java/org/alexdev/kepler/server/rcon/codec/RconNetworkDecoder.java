package org.alexdev.kepler.server.rcon.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alexdev.kepler.server.rcon.messages.RconMessage;
import org.alexdev.kepler.util.StringUtil;

import java.util.HashMap;
import java.util.List;

public class RconNetworkDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (buffer.readableBytes() < 8) {
            // If the incoming data is less than 8 bytes, it's junk.
            return;
        }

        buffer.markReaderIndex();
        int length = buffer.readInt();

        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        ByteBuf buf = buffer.readBytes(length);
        String header = readString(buf);

        if (header == null) {
            clear(buf);
            return;
        }

        int parameterCount = buf.readInt();
        HashMap<String, String> parameters = new HashMap<>(parameterCount);

        for (int i = 0; i < parameterCount; i++) {
            String key = readString(buf);
            String value = readString(buf);

            if (key == null || value == null) {
                clear(buf);
                return;
            }

            parameters.put(key, value);
        }

        clear(buf);

        // Send new rcon message
        out.add(new RconMessage(header, parameters));
    }

    private void clear(ByteBuf buf) {
        if (buf.refCnt() > 0) {
            buf.release();
        }
    }

    /**
     * Release buffer on failure.
     * @param buffer the buffer
     */
    private void tryRelease(ByteBuf buffer) {
        try {
            buffer.release();
        } catch (Exception ignored) {

        }
    }

    /**
     * Read string from byte buffer.
     *
     * @param buffer the buffer to read from
     * @return the string
     */
    public String readString(ByteBuf buffer) {
        int length = buffer.readInt();
        byte[] data = this.readBytes(buffer, length);

        if (data == null) {
            return null;
        }

        return new String(data, StringUtil.getCharset());
    }

    /**
     * Read bytes of byte buffer.
     *
     * @param buf the buffer to read the bytes from
     * @param len the amount of bytes to read
     * @return the bytes returned
     */
    public byte[] readBytes(ByteBuf buf, int len) {
        if (buf.readableBytes() < len) {
            return null;
        }

        byte[] payload = new byte[len];
        buf.readBytes(payload);
        return payload;
    }

}

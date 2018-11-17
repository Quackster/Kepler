package org.alexdev.kepler.server.netty.streams;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import org.alexdev.kepler.util.encoding.Base64Encoding;
import org.alexdev.kepler.util.encoding.VL64Encoding;

public class NettyRequest {
    final private int headerId;
    final private String header;
    final private ByteBuf buffer;

    public NettyRequest(ByteBuf buffer) {
        this.buffer = buffer;
        this.header = new String(new byte[] { buffer.readByte(), buffer.readByte() });
        this.headerId = Base64Encoding.decode(header.getBytes());
    }

    public Integer readInt() {
        byte[] remaining = this.remainingBytes();

        int length = remaining[0] >> 3 & 7;
        int value = VL64Encoding.decode(remaining);
        readBytes(length);

        return value;
    }

    public int readBase64() {
        return Base64Encoding.decode(new byte[] {
            this.buffer.readByte(),
            this.buffer.readByte()
        });
    }

    public boolean readBoolean()  {
        return this.readInt() == 1;
    }

    public String readString() {
        int length = this.readBase64();
        byte[] data = this.readBytes(length);

        return new String(data);
    }

    public byte[] readBytes(int len) {
        byte[] payload = new byte[len];
        this.buffer.readBytes(payload);

        return payload;
    }

    private byte[] remainingBytes() {
        this.buffer.markReaderIndex();

        byte[] bytes = new byte[this.buffer.readableBytes()];
        buffer.readBytes(bytes);

        this.buffer.resetReaderIndex();
        return bytes;
    }

    public String contents() {
        byte[] remiainingBytes = this.remainingBytes();

        if (remiainingBytes != null) {
            return new String(remiainingBytes);
        }

        return null;
    }

     public String getMessageBody() {
        String consoleText = this.buffer.toString(Charset.defaultCharset());

        for (int i = 0; i < 14; i++) {
            consoleText = consoleText.replace(Character.toString((char)i), "{" + i + "}");
        }

        return this.header + consoleText;
    }

    public String getHeader() {
        return header;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void dispose() {
        this.buffer.release();
    }
}
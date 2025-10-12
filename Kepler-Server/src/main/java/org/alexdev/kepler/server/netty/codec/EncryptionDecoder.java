package org.alexdev.kepler.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alexdev.kepler.server.netty.encryption.RC4;
import org.alexdev.kepler.util.encoding.Base64Encoding;

import java.math.BigInteger;
import java.util.List;

public class EncryptionDecoder extends ByteToMessageDecoder {

    private final RC4 pHeaderDecoder;
    private final RC4 pDecoder;

    public EncryptionDecoder(BigInteger sharedKey) {
        this.pHeaderDecoder = new RC4(sharedKey);
        this.pDecoder = new RC4(sharedKey);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        int pMsgSize;

        buffer.markReaderIndex();

        while (buffer.readableBytes() > 6) {
            byte[] tHeaderMsg = new byte[6];
            buffer.readBytes(tHeaderMsg);
            byte[] tHeader = this.pHeaderDecoder.decipher(tHeaderMsg);

            int tByte1 = ((int) tHeader[3]) & 63;
            int tByte2 = ((int) tHeader[2]) & 63;
            int tByte3 = ((int) tHeader[1]) & 63;
            pMsgSize = (tByte2 * 64) | tByte1;
            pMsgSize = (tByte3 * 64 * 64) | pMsgSize;

            if (buffer.readableBytes() < pMsgSize) {
                buffer.resetReaderIndex();
                return;
            }

            byte[] tBodyMsg = new byte[pMsgSize];
            buffer.readBytes(tBodyMsg);
            byte[] tBodyMsgPlain = this.pDecoder.decipher(tBodyMsg);

            ByteBuf result = Unpooled.buffer();

            result.writeBytes(Base64Encoding.encode(tBodyMsgPlain.length, 3));
            result.writeBytes(tBodyMsgPlain);

            out.add(result);
        }
    }
}

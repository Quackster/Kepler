package org.alexdev.kepler.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alexdev.kepler.server.netty.encryption.BobbaChaChaKey;
import org.alexdev.kepler.server.netty.encryption.BobbaCrypto;
import org.alexdev.kepler.util.encoding.Base64Encoding;

import java.util.List;

/**
 * Reverse Engineered by <a href="https://github.com/UnfamiliarLegacy">Mikee</a>.
 */
public class BobbaDecoder extends ByteToMessageDecoder {

    public static final int PACKET_HEADER_SIZE = 2;

    public static final int PACKET_LENGTH_SIZE_ENCRYPTED = 6;
    public static final int PACKET_LENGTH_SIZE = 3;

    public static final int PACKET_SIZE_MIN = PACKET_HEADER_SIZE + PACKET_LENGTH_SIZE;
    public static final int PACKET_SIZE_MIN_ENCRYPTED = PACKET_HEADER_SIZE + PACKET_LENGTH_SIZE_ENCRYPTED;

    private final BobbaChaChaKey headerKey;
    private final BobbaChaChaKey dataKey;

    private int previousLength = 0;

    public BobbaDecoder(BobbaChaChaKey headerKey, BobbaChaChaKey dataKey) {
        this.headerKey = headerKey;
        this.dataKey = dataKey;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        while (buffer.readableBytes() >= PACKET_SIZE_MIN_ENCRYPTED) {
            int pMsgSize;

            buffer.markReaderIndex();

            if (previousLength == 0) {
                final byte[] tHeaderMsg = new byte[PACKET_LENGTH_SIZE_ENCRYPTED];
                buffer.readBytes(tHeaderMsg);

                final byte[] decData = BobbaCrypto.decrypt(tHeaderMsg, 0, tHeaderMsg.length, this.headerKey);

                if (decData.length < 4) {
                    throw new IllegalStateException("Decrypted packet length is less than 4 bytes.");
                }

                // When a packet has been received that we can't fully read, we need to store the decrypted length.
                // Otherwise, we would keep decrypting the same bytes and mutating the cipher state, messing up the entire state.
                pMsgSize = previousLength = Base64Encoding.decode(new byte[]{decData[1], decData[2], decData[3]});
            } else {
                buffer.skipBytes(PACKET_LENGTH_SIZE_ENCRYPTED);

                pMsgSize = previousLength;
            }

            if (pMsgSize < 0) {
                throw new IllegalStateException("Decoded packet length is negative.");
            }

            if (buffer.readableBytes() < pMsgSize) {
                buffer.resetReaderIndex();
                return;
            }

            final byte[] tBodyMsg = new byte[pMsgSize];
            buffer.readBytes(tBodyMsg);
            final byte[] tBodyMsgPlain = BobbaCrypto.decrypt(tBodyMsg, 0, tBodyMsg.length, this.dataKey);

            final ByteBuf result = ctx.alloc().buffer(PACKET_LENGTH_SIZE + tBodyMsgPlain.length);

            result.writeBytes(Base64Encoding.encode(tBodyMsgPlain.length, 3));
            result.writeBytes(tBodyMsgPlain);

            out.add(result);

            previousLength = 0;
        }
    }
}

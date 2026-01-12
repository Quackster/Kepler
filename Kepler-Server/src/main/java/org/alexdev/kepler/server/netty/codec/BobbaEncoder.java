package org.alexdev.kepler.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.alexdev.kepler.server.netty.encryption.BobbaChaChaKey;
import org.alexdev.kepler.server.netty.encryption.BobbaCrypto;
import org.alexdev.kepler.util.encoding.Base64Encoding;

import java.util.concurrent.ThreadLocalRandom;

public class BobbaEncoder extends MessageToByteEncoder<ByteBuf> {

    private final BobbaChaChaKey headerKey;
    private final BobbaChaChaKey dataKey;

    public BobbaEncoder(BobbaChaChaKey headerKey, BobbaChaChaKey dataKey) {
        this.headerKey = headerKey;
        this.dataKey = dataKey;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, ByteBuf out) throws Exception {
        // Encrypt entire packet.
        final byte[] dataBuffer = new byte[msg.readableBytes()];
        msg.readBytes(dataBuffer);

        final byte[] packet = BobbaCrypto.encrypt(dataBuffer, this.dataKey);

        // Encode header.
        final byte[] newPacketLen = Base64Encoding.encode(packet.length, 3);
        byte[] header = new byte[4];

        header[0] = (byte) ThreadLocalRandom.current().nextInt(1, 127);
        header[1] = newPacketLen[0];
        header[2] = newPacketLen[1];
        header[3] = newPacketLen[2];

        // Encrypt header.
        header = BobbaCrypto.applyChaCha(header, 0, header.length, headerKey);
        header = Base64Encoding.encode(header, 0, header.length);

        out.writeBytes(header);
        out.writeBytes(packet);
    }
}

package org.alexdev.kepler.server.mus.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.alexdev.kepler.server.mus.MusUtil;
import org.alexdev.kepler.server.mus.streams.MusMessage;
import org.alexdev.kepler.server.mus.streams.MusTypes;
import org.alexdev.kepler.server.netty.codec.NetworkEncoder;
import org.alexdev.kepler.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.util.List;

public class MusNetworkEncoder extends MessageToMessageEncoder<MusMessage> {
    final private static Logger log = LoggerFactory.getLogger(NetworkEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MusMessage msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();

        msg.setSenderId("System");
        msg.setReceivers(new String[] { "*" });
        msg.setTimestamp(DateUtil.getCurrentTimeSeconds());

        ByteBuf temporaryBuffer = ctx.alloc().buffer();
        temporaryBuffer.writeInt(msg.getErrorCode());
        temporaryBuffer.writeInt((int) msg.getTimestamp()); // Ugh, I know right? Old protocol...

        MusUtil.writeEvenPaddedString(temporaryBuffer, msg.getSubject());
        MusUtil.writeEvenPaddedString(temporaryBuffer, msg.getSenderId());

        temporaryBuffer.writeInt(msg.getReceivers().length);

        for (int i = 0; i < msg.getReceivers().length; i++) {
            MusUtil.writeEvenPaddedString(temporaryBuffer, msg.getReceivers()[i]);
        }

        temporaryBuffer.writeShort(msg.getContentType());

        // Content
        if (msg.getContentType() != MusTypes.Void)
        {
            if (msg.getContentType() == MusTypes.Integer)
                temporaryBuffer.writeInt(msg.getContentInt());
            else if (msg.getContentType() == MusTypes.String)
                MusUtil.writeEvenPaddedString(temporaryBuffer, msg.getContentString());
            else if (msg.getContentType() == MusTypes.PropList)
                MusUtil.writePropList(temporaryBuffer, msg.getContentPropList());
            else
                System.out.println("Unsupported MusMessage content type " + msg.getContentType() + "!");
        }

        byte[] body = new byte[temporaryBuffer.readableBytes()];

        temporaryBuffer.readBytes(body);
        temporaryBuffer.release();

        buffer.writeByte('r');
        buffer.writeByte(0);
        buffer.writeInt(body.length);
        buffer.writeBytes(body);

        out.add(buffer);
    }
}

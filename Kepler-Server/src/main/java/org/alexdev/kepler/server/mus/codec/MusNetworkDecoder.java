package org.alexdev.kepler.server.mus.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import org.alexdev.kepler.server.mus.MusUtil;
import org.alexdev.kepler.server.mus.streams.MusMessage;
import org.alexdev.kepler.server.mus.streams.MusTypes;

import java.util.List;

public class MusNetworkDecoder extends ByteArrayDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        byte headerTag = buffer.readByte();
        buffer.readByte();

        if (headerTag != 'r') {
            ctx.channel().close();
        } else {
            MusMessage musMessage = new MusMessage();
            musMessage.setSize(buffer.readInt());

            if (buffer.readableBytes() < musMessage.getSize()) {
                buffer.resetReaderIndex();
                return;
            }

            ByteBuf body = buffer.readBytes(musMessage.getSize());

            musMessage.setErrorCode(body.readInt());
            musMessage.setTimestamp(body.readInt());
            musMessage.setSubject(MusUtil.readEvenPaddedString(body));
            musMessage.setSenderId(MusUtil.readEvenPaddedString(body));

            String[] receivers = new String[body.readInt()];

            for (int i = 0; i < receivers.length; i++) {
                receivers[i] = MusUtil.readEvenPaddedString(body);
            }

            if (musMessage.getSubject().equals("Logon")) {
                // Read in remaining data
                byte[] tmpBytes = new byte[body.readableBytes()];
                body.readBytes(tmpBytes);

                // Set fields
                musMessage.setContentType(MusTypes.String);
                musMessage.setContentString(new String(tmpBytes));
            } else {
                musMessage.setContentType(body.readShort());

                if (musMessage.getContentType() == MusTypes.Integer)
                    musMessage.setContentInt(body.readInt());
                else if (musMessage.getContentType() == MusTypes.String)
                    musMessage.setContentString(MusUtil.readEvenPaddedString(body));
                else if (musMessage.getContentType() == MusTypes.PropList)
                    musMessage.setContentPropList(MusUtil.readPropList(body));
            }

            body.release();
            out.add(musMessage);
        }
    }
}

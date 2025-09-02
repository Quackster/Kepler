package net.h4bbo.kepler.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NetworkEncoder extends MessageToMessageEncoder<Object> {
    final private static Logger log = LoggerFactory.getLogger(NetworkEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();

        try {
            if (obj instanceof MessageComposer) {
                MessageComposer msg = (MessageComposer) obj;
                NettyResponse response = new NettyResponse(msg.getHeader(), buffer);

                try {
                    msg.compose(response);
                } catch (Exception ex) {
                    Player player = ctx.channel().attr(Player.PLAYER_KEY).get();

                    String name = "";

                    if (player != null && player.isLoggedIn()) {
                        name = player.getDetails().getName();
                    }

                    Log.getErrorLogger().error("Error occurred when composing (" + response.getHeader() + ") for user (" + name + "):", ex);
                    return;
                }

                if (!response.isFinalised()) {
                    buffer.writeByte(1);
                    response.setFinalised(true);
                }

                if (ServerConfiguration.getBoolean("log.sent.packets")) {
                    log.info("SENT: {} / {}", msg.getHeader(), response.getBodyString());
                }
            }

            if (obj instanceof String) {
                buffer.writeBytes(((String) obj).getBytes());
            }

            out.add(buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
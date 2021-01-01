package org.alexdev.kepler.server.rcon;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.server.rcon.messages.RconMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RconConnectionHandler extends ChannelInboundHandlerAdapter {
    final private static Logger log = LoggerFactory.getLogger(RconConnectionHandler.class);

    private final RconServer server;

    public RconConnectionHandler(RconServer rconServer) {
        this.server = rconServer;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        if (!this.server.getChannels().add(ctx.channel()) || Kepler.isShuttingdown()) {
            //Log.getErrorLogger().error("Could not accept RCON connection from {}", ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0]);
            ctx.close();
        }

        //log.info("[RCON] Connection from {}", ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0]);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        this.server.getChannels().remove(ctx.channel());
        //log.info("[RCON] Disconnection from {}", ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0]);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof RconMessage)) {
            return;
        }

        RconMessage message = (RconMessage) msg;

        //log.info("[RCON] Message received: " + message);

        try {
            switch (message.getHeader()) {
                case REFRESH_LOOKS:
                    Player online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getRoomUser().refreshAppearance();
                    }

                    break;
                case HOTEL_ALERT:
                    String messageSender = message.getValues().get("sender");
                    String hotelAlert = message.getValues().get("message");

                    StringBuilder alert = new StringBuilder();
                    alert.append(hotelAlert).append("<br>");
                    alert.append("<br>");
                    alert.append("- ").append(messageSender);

                    for (Player player : PlayerManager.getInstance().getPlayers()) {
                        player.send(new ALERT(alert.toString()));
                    }
                    break;
                case REFRESH_CLUB:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        PlayerDetails playerDetails = PlayerDao.getDetails(online.getDetails().getId());

                        online.getDetails().setCredits(playerDetails.getCredits());
                        online.getDetails().setClubExpiration(playerDetails.getClubExpiration());
                        online.getDetails().setFirstClubSubscription(playerDetails.getFirstClubSubscription());

                        online.send(new CREDIT_BALANCE(online.getDetails()));
                        online.refreshFuserights();
                        online.refreshClub();
                    }

                    break;
                case REFRESH_HAND:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getInventory().reload();

                        if (online.getRoomUser().getRoom() != null)
                            online.getInventory().getView("new");
                    }

                    break;
                case REFRESH_CREDITS:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getDetails().setCredits(CurrencyDao.getCredits(online.getDetails().getId()));
                        online.send(new CREDIT_BALANCE(online.getDetails()));
                    }

                    break;
            }
        } catch (Exception ex) {
            Log.getErrorLogger().error("[RCON] Error occurred when handling RCON message: ", ex);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof Exception) {
            if (!(cause instanceof IOException)) {
                Log.getErrorLogger().error("[RCON] Error occurred: ", cause);
            }
        }

        ctx.close();
    }
}

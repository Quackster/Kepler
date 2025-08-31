package org.alexdev.kepler.server.rcon;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.game.achievements.AchievementManager;
import org.alexdev.kepler.game.achievements.AchievementType;
import org.alexdev.kepler.game.groups.GroupMember;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.messenger.MessengerUser;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.handshake.RIGHTS;
import org.alexdev.kepler.messages.outgoing.rooms.groups.GROUP_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.groups.GROUP_MEMBERSHIP_UPDATE;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.server.rcon.messages.RconMessage;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

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
        //log.info("[RCON] Message received: " + message.getHeader());

        try {
            switch (message.getHeader()) {
                case DISCONNECT_USER:
                    Player online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getNetwork().disconnect();
                    }

                    break;
                case REFRESH_LOOKS:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

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

                        PlayerDao.saveCurrency(online.getDetails().getId(), playerDetails.getCredits());

                        online.send(new CREDIT_BALANCE(online.getDetails().getCredits()));
                        online.send(new RIGHTS(online.getFuserights()));
                        online.refreshClub();

                        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_HC, online);
                    }

                    break;

                    /*
                case REFRESH_TAGS:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_TAGS, online);

                        if (online.getRoomUser().getRoom() != null) {
                            online.getRoomUser().refreshTags();
                        }
                    }
                    break;*/
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
                        online.send(new CREDIT_BALANCE(online.getDetails().getCredits()));
                    }

                    break;
                case FRIEND_REQUEST:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("friendId")));
                    int requestFrom = Integer.parseInt(message.getValues().get("userId"));

                    if (online != null) {
                        if (!online.getMessenger().hasRequest(requestFrom)) {
                            online.getMessenger().addRequest(new MessengerUser(PlayerManager.getInstance().getPlayerData(requestFrom)));
                        }
                    }

                    break;
                case REFRESH_MESSENGER_CATEGORIES:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getMessenger().getCategories().clear();
                        online.getMessenger().getCategories().addAll(MessengerDao.getCategories(online.getDetails().getId()));

                        // Refresh friends for categories
                        for (MessengerUser dbFriend : MessengerDao.getFriends(online.getDetails().getId()).values()) {
                            MessengerUser friend = online.getMessenger().getFriend(dbFriend.getUserId());

                            if (friend != null) {
                                if (friend.getCategoryId() != dbFriend.getCategoryId()) {
                                    friend.setCategoryId(dbFriend.getCategoryId());
                                    online.getMessenger().queueFriendUpdate(friend);
                                }
                            }
                        }
                    }

                    break;
                    /*
                case REFRESH_TRADE_SETTING:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        boolean oldTradeSetting = online.getDetails().isTradeEnabled();
                        online.getDetails().setTradeEnabled(message.getValues().get("tradeEnabled").equalsIgnoreCase("1"));

                        if (!oldTradeSetting && online.getDetails().isTradeEnabled()) {
                            AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_TRADERPASS, online);

                            if (online.getRoomUser().getRoom() != null &&
                                    !online.getRoomUser().getRoom().isGameArena()) {
                                Position currentPosition = online.getRoomUser().getPosition();

                                online.getRoomUser().getRoom().getEntityManager().enterRoom(online, currentPosition);
                                online.getRoomUser().invokeItem(null, false);
                            }
                        }
                    }
                    break;*/
                case REFRESH_GROUP_PERMS:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.refreshJoinedGroups();

                        PlayerDetails newDetails = PlayerDao.getDetails(online.getDetails().getId());
                        online.getDetails().setFavouriteGroupId(newDetails.getFavouriteGroupId());

                        int newGroup = newDetails.getFavouriteGroupId();

                        if (online.getRoomUser().getRoom() != null) {
                            GroupMember groupMember = null;

                            if (online.getDetails().getFavouriteGroupId() > 0) {
                                groupMember = online.getDetails().getGroupMember();
                            }

                            if (groupMember != null) {
                                online.getRoomUser().getRoom().send(new GROUP_BADGES(new HashMap<>() {{
                                    put(newGroup, online.getJoinedGroup(newGroup).getBadge());
                                }}));
                            }

                            online.getRoomUser().getRoom().send(new GROUP_MEMBERSHIP_UPDATE(online.getRoomUser().getInstanceId(), groupMember == null ? -1 : groupMember.getGroupId(), groupMember == null ? -1 : groupMember.getMemberRank().getClientRank()));
                        }
                    }

                    break;
                case GROUP_DELETED:
                    int groupId = Integer.parseInt(message.getValues().get("groupId"));

                    for (Player player : PlayerManager.getInstance().getPlayers()) {
                        if (player.getDetails().getFavouriteGroupId() == groupId) {
                            player.getDetails().setFavouriteGroupId(0);

                            if (player.getRoomUser().getRoom() != null) {
                                player.getRoomUser().getRoom().send(new GROUP_MEMBERSHIP_UPDATE(player.getRoomUser().getInstanceId(), -1, -1));
                            }

                            player.refreshJoinedGroups();
                        }
                    }
                    break;
                case REFRESH_GROUP:
                    groupId = Integer.parseInt(message.getValues().get("groupId"));

                    for (Player player : PlayerManager.getInstance().getPlayers()) {
                        if (player.getJoinedGroup(groupId) != null) {
                            player.refreshJoinedGroups();
                        }
                    }

                    break;
                /*case REFRESH_ADS:
                    AdManager.getInstance().reset();
                    break;*/
                case INFOBUS_DOOR_STATUS:
                    InfobusManager.getInstance().updateDoorStatus(message.getValues().get("doorStatus").equals("1"));
                    break;
                case INFOBUS_END_EVENT:
                    InfobusManager.getInstance().stopEvent();
                    break;
                case INFOBUS_POLL:
                    int pollId = Integer.parseInt(message.getValues().get("pollId"));
                    InfobusManager.getInstance().startPolling(pollId);
                    break;
                /*case REFRESH_CATALOGUE_FRONTPAGE:
                    GameConfiguration.reset(new GameConfigWriter());

                    for (Player p : PlayerManager.getInstance().getPlayers()) {
                        new GET_CATALOG_INDEX().handle(p, null);
                    }

                    break;*/
                case CLEAR_PHOTO:
                    int itemId = Integer.parseInt(message.getValues().get("itemId"));
                    int userId = Integer.parseInt(message.getValues().get("userId"));

                    Item item = ItemManager.getInstance().resolveItem(itemId);

                    if (item != null) {
                        Room room = item.getRoom();

                        if (room != null) {
                            room.getMapping().removeItem(item);
                        }

                        item.delete();
                        PhotoDao.deleteItem(itemId);

                        TransactionDao.createTransaction(userId, String.valueOf(itemId), "0", 1,
                                "Hidden photo " + itemId, 0, 0, false);
                    }

                    break;
                case REFRESH_STATISTICS:
                    online = PlayerManager.getInstance().getPlayerById(Integer.parseInt(message.getValues().get("userId")));

                    if (online != null) {
                        online.getStatisticManager().reload();
                    }

                    break;

                case REFRESH_ROOM_BADGES:
                    RoomManager.getInstance().reloadBadges();
                    RoomManager.getInstance().giveBadges();
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

package org.alexdev.kepler.messages;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.incoming.games.*;
import org.alexdev.kepler.messages.incoming.club.*;
import org.alexdev.kepler.messages.incoming.inventory.*;
import org.alexdev.kepler.messages.incoming.catalogue.*;
import org.alexdev.kepler.messages.incoming.messenger.*;
import org.alexdev.kepler.messages.incoming.moderation.*;
import org.alexdev.kepler.messages.incoming.navigator.*;
import org.alexdev.kepler.messages.incoming.purse.*;
import org.alexdev.kepler.messages.incoming.register.*;
import org.alexdev.kepler.messages.incoming.rooms.*;
import org.alexdev.kepler.messages.incoming.handshake.*;
import org.alexdev.kepler.messages.incoming.rooms.badges.*;
import org.alexdev.kepler.messages.incoming.rooms.dimmer.*;
import org.alexdev.kepler.messages.incoming.rooms.items.*;
import org.alexdev.kepler.messages.incoming.rooms.moderation.*;
import org.alexdev.kepler.messages.incoming.rooms.pool.*;
import org.alexdev.kepler.messages.incoming.rooms.settings.*;
import org.alexdev.kepler.messages.incoming.rooms.teleporter.*;
import org.alexdev.kepler.messages.incoming.rooms.user.*;
import org.alexdev.kepler.messages.incoming.songs.*;
import org.alexdev.kepler.messages.incoming.trade.*;
import org.alexdev.kepler.messages.incoming.tutorial.*;
import org.alexdev.kepler.messages.incoming.user.*;
import org.alexdev.kepler.messages.incoming.user.settings.GET_ACCOUNT_PREFERENCES;
import org.alexdev.kepler.messages.incoming.welcomingparty.ACCEPT_TUTOR_INVITATION;
import org.alexdev.kepler.messages.incoming.welcomingparty.REJECT_TUTOR_INVITATION;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private ConcurrentHashMap<Integer, MessageEvent> messages;

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private static MessageHandler instance;

    private MessageHandler() {
        this.messages = new ConcurrentHashMap<>();

        registerHandshakePackets();
        registerRegisterPackets();
        registerPursePackets();
        registerUserPackets();
        registerWelcomingPartyPackets();
        registerTutorialPackets();
        registerNavigatorPackets();
        registerRoomPackets();
        registerRoomUserPackets();
        registerRoomBadgesPackets();
        registerRoomPoolPackets();
        registerRoomSettingsPackets();
        registerRoomItemPackets();
        registerRoomTeleporterPackets();
        registerRoomModerationPackets();
        registerGameModerationPackets();
        registerMessengerPackets();
        registerCataloguePackets();
        registerInventoryPackets();
        registerTradePackets();
        registerSongPackets();
        registerGamePackets();
    }

    /**
     * Register handshake packets.
     */
    private void registerHandshakePackets() {
        registerEvent(206, new INIT_CRYPTO());
        registerEvent(202, new GENERATEKEY());
        registerEvent(204, new SSO());
        registerEvent(4, new TRY_LOGIN());
    }

    /**
     * Register handshake packets.
     */
    private void registerRegisterPackets() {
        registerEvent(49, new GDATE());
        registerEvent(42, new APPROVENAME());
        registerEvent(203, new APPROVE_PASSWORD());
        registerEvent(197, new APPROVEEMAIL());
        registerEvent(43, new REGISTER());
    }

    /**
     * Register purse packets.
     */
    private void registerPursePackets() {
        registerEvent(8, new GET_CREDITS());
        registerEvent(127, new GETUSERCREDITLOG());
        registerEvent(129, new REDEEM_VOUCHER());
    }

    /**
     * Register general purpose user packets.
     */
    private void registerUserPackets() {
        registerEvent(7, new GET_INFO());
        registerEvent(228, new GET_ACCOUNT_PREFERENCES());
        registerEvent(196, new PONG());
        registerEvent(26, new GET_CLUB());
        registerEvent(190, new SUBSCRIBE_CLUB());
        registerEvent(44, new UPDATE());
        registerEvent(360, new GET_IGNORE_LIST());
        registerEvent(319, new IGNORE_USER());
        registerEvent(322, new UNIGNORE_USER());
        //registerEvent(315, new TEST_LATENCY());
    }

    /**
     * Register welcoming party packets.
     */
    private void registerWelcomingPartyPackets() {
        registerEvent(357, new ACCEPT_TUTOR_INVITATION());
        registerEvent(358, new REJECT_TUTOR_INVITATION());
    }

    /**
     * Register tutorial packets
     */
    private void registerTutorialPackets() {
        registerEvent(250, new GET_TUTORIAL_CONFIGURATION());
        registerEvent(249, new SET_TUTORIAL_MODE());
    }

    /**
     * Register navigator packets.
     */
    private void registerNavigatorPackets() {
        registerEvent(150, new NAVIGATE());
        registerEvent(16, new SUSERF());
        registerEvent(151, new GETUSERFLATCATS());
        registerEvent(264, new RECOMMENDED_ROOMS());
        registerEvent(17, new SRCHF());
        registerEvent(154, new GETSPACENODEUSERS());
        registerEvent(18, new GETFVRF());
        registerEvent(19, new ADD_FAVORITE_ROOM());
        registerEvent(20, new DEL_FAVORITE_ROOM());
    }

    /**
     * Register room packets.
     */
    private void registerRoomPackets() {
        registerEvent(57, new TRYFLAT());
        registerEvent(59, new GOTOFLAT());
        registerEvent(182, new GETINTEREST());
        registerEvent(2, new ROOM_DIRECTORY());
        registerEvent(126, new GETROOMAD());
        registerEvent(60, new G_HMAP());
        registerEvent(62, new G_OBJS());
        registerEvent(61,  new G_USRS());
        registerEvent(64, new G_STAT());
        registerEvent(63, new G_ITEMS());
        registerEvent(98, new LETUSERIN());
        registerEvent(261, new RATEFLAT());
    }

    /**
     * Register room user packets.
     */
    private void registerRoomUserPackets() {
        registerEvent(53, new QUIT());
        registerEvent(75, new WALK());
        registerEvent(115, new GOAWAY());
        registerEvent(52, new CHAT());
        registerEvent(55, new SHOUT());
        registerEvent(56, new WHISPER());
        registerEvent(317, new USER_START_TYPING());
        registerEvent(318, new USER_CANCEL_TYPING());
        registerEvent(79, new LOOKTO());
        registerEvent(80, new CARRYDRINK());
        registerEvent(87, new CARRYITEM());
        registerEvent(94, new WAVE());
        registerEvent(93, new DANCE());
        registerEvent(88, new STOP());
        registerEvent(229, new SET_SOUND_SETTING());
        registerEvent(117, new IIM());
    }


    /**
     * Register room badges packets;
     */
    private void registerRoomBadgesPackets() {
        registerEvent(157, new GETAVAILABLEBADGES());
        registerEvent(158, new SETBADGE());
    }

    /**
     * Register room settings packets.
     */
    private void registerRoomSettingsPackets() {
        registerEvent(21, new GETFLATINFO());
        registerEvent(29, new CREATEFLAT());
        registerEvent(25, new SETFLATINFO());
        registerEvent(24, new UPDATEFLAT());
        registerEvent(153, new SETFLATCAT());
        registerEvent(152, new GETFLATCAT());
        registerEvent(23, new DELETEFLAT());
    }

    /**
     * Register room item packets.
     */
    private void registerRoomItemPackets() {
        registerEvent(90, new PLACESTUFF());
        registerEvent(73, new MOVESTUFF());
        registerEvent(67, new ADDSTRIPITEM());
        registerEvent(83, new G_IDATA());
        registerEvent(89, new USEITEM());
        registerEvent(84, new SETITEMDATA());
        registerEvent(214, new SETITEMSTATE());
        registerEvent(85, new REMOVEITEM());
        registerEvent(74, new SETSTUFFDATA());
        registerEvent(183, new CONVERT_FURNI_TO_CREDITS());
        registerEvent(76, new THROW_DICE());
        registerEvent(77, new DICE_OFF());
        registerEvent(247, new SPIN_WHEEL_OF_FORTUNE());
        registerEvent(341, new MSG_ROOMDIMMER_GET_PRESETS());
        registerEvent(342, new MSG_ROOMDIMMER_SET_PRESET());
        registerEvent(343, new MSG_ROOMDIMMER_CHANGE_STATE());
        registerEvent(78, new PRESENTOPEN());
    }

    /**
     * Register room teleporter packets
     */
    public void registerRoomTeleporterPackets() {
        registerEvent(81, new INTODOOR());
        registerEvent(28, new GETDOORFLAT());
        registerEvent(82, new DOORGOIN());
        registerEvent(54, new GOVIADOOR());
    }

    /**
     * Register room pool packets.
     */
    private void registerRoomPoolPackets() {
        registerEvent(116, new SWIMSUIT());
        registerEvent(105, new BTCKS());
        registerEvent(106, new DIVE());
        registerEvent(107, new SPLASH_POSITION());
        registerEvent(104, new SIGN());
    }

    /**
     * Register room moderation packets
     */
    private void registerRoomModerationPackets() {
        registerEvent(95, new KICK());
        registerEvent(96, new ASSIGNRIGHTS());
        registerEvent(97, new REMOVERIGHTS());
        registerEvent(155, new REMOVEALLRIGHTS());
    }

    /**
     * Register game moderation packets.
     */
    private void registerGameModerationPackets(){
        registerEvent(200, new MODERATORACTION());
        registerEvent(237, new REQUEST_CFH());
        registerEvent(86, new SUBMIT_CFH());
        registerEvent(48, new PICK_CALLFORHELP());
        registerEvent(199, new MESSAGETOCALLER());
        registerEvent(198, new CHANGECALLCATEGORY());
        registerEvent(238, new DELETE_CRY());
    }

    /**
     * Register room trade packets
     */
    private void registerTradePackets() {
        registerEvent(71, new TRADE_OPEN());
        registerEvent(72, new TRADE_ADDITEM());
        registerEvent(70, new TRADE_CLOSE());
        registerEvent(69, new TRADE_ACCEPT());
        registerEvent(68, new TRADE_UNACCEPT());
    }

    /**
     * Register messenger packets.
     */
    private void registerMessengerPackets() {
        registerEvent(12, new MESSENGERINIT());
        registerEvent(41, new FINDUSER());
        registerEvent(39, new MESSENGER_REQUESTBUDDY());
        registerEvent(38, new MESSENGER_DECLINEBUDDY());
        registerEvent(37, new MESSENGER_ACCEPTBUDDY());
        registerEvent(233, new MESSENGER_GETREQUESTS());
        registerEvent(191, new MESSENGER_GETMESSAGES());
        registerEvent(36, new MESSENGER_ASSIGNPERSMSG());
        registerEvent(40, new MESSENGER_REMOVEBUDDY());
        registerEvent(33, new MESSENGER_SENDMSG());
        registerEvent(32, new MESSENGER_MARKREAD());
        registerEvent(262, new FOLLOW_FRIEND());
        registerEvent(15, new FRIENDLIST_UPDATE());
    }

    /**
     * Register catalogue packets
     */
    private void registerCataloguePackets() {
        registerEvent(101, new GCIX());
        registerEvent(102, new GCAP());
        registerEvent(100, new GRPC());
        registerEvent(215, new GET_ALIAS_LIST());
    }

    /**
     * Register inventory packets.
     */
    private void registerInventoryPackets() {
        registerEvent(65, new GETSTRIP());
        registerEvent(66, new FLATPROPBYITEM());
    }

    /**
     * Register song packets.
     */
    private void registerSongPackets() {
        registerEvent(244, new GET_SONG_LIST());
        registerEvent(246, new GET_SONG_LIST());
        registerEvent(239, new NEW_SONG());
        registerEvent(219, new INSERT_SOUND_PACKAGE());
        registerEvent(220, new EJECT_SOUND_PACKAGE());
        registerEvent(240, new SAVE_SONG_NEW());
        registerEvent(243, new UPDATE_PLAY_LIST());
        registerEvent(221, new GET_SONG_INFO());
        registerEvent(245, new GET_PLAY_LIST());
        registerEvent(248, new DELETE_SONG());
        registerEvent(241, new EDIT_SONG());
        registerEvent(242, new SAVE_SONG_EDIT());
        registerEvent(218, new SAVE_SONG());
    }

    /**
     * Register games packets
     */
    private void registerGamePackets() {
        registerEvent(159, new GETINSTANCELIST());
        registerEvent(160, new OBSERVEINSTANCE());
        registerEvent(161, new UNOBSERVEINSTANCE());
        registerEvent(162, new INITIATECREATEGAME());
        registerEvent(163, new GAMEPARAMETERVALUES());
        registerEvent(165, new INITIATEJOINGAME());
        registerEvent(167, new LEAVEGAME());
        registerEvent(168, new KICKPLAYER());
        registerEvent(169, new WATCHGAME());
        registerEvent(170, new STARTGAME());
        registerEvent(171, new GAMEEVENT());
        registerEvent(172, new GAMERESTART());
        /*registerEvent(173, (player, reader) -> {
            List<GameObject> objects = new ArrayList<>();
            objects.add(new SnowStormSpawnPlayerEvent(player.getRoomUser().getGamePlayer()));

            GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();
            SnowStormGame game = (SnowStormGame) gamePlayer.getGame();

            gamePlayer.getTurnContainer().calculateChecksum(objects);
            gamePlayer.getTurnContainer().getCurrentTurn().incrementAndGet();

            player.send(new SNOWSTORM_GAMESTATUS((SnowStormGame) game, List.of(), gamePlayer));//.compose(response);
        });*/
    }

    /**
     * Register event.
     *
     * @param header the header
     * @param messageEvent the message event
     */
    private void registerEvent(int header, MessageEvent messageEvent) {
        /*if (!this.messages.containsKey(header)) {
            this.messages.put(header, new ArrayList<>());
        }

        this.messages.get(header).add(messageEvent);*/
        this.messages.put(header, messageEvent);
    }

    /**
     * Handle request.
     *
     * @param message the message
     */
    public void handleRequest(Player player, NettyRequest message) {
        if (ServerConfiguration.getBoolean("log.received.packets")) {
            if (this.messages.containsKey(message.getHeaderId())) {
                MessageEvent event = this.messages.get(message.getHeaderId());
                player.getLogger().info("Received ({}): {} / {} ", event.getClass().getSimpleName(), message.getHeaderId(), message.getMessageBody());
            } else {
                player.getLogger().info("Received ({}): {} / {} ", "Unknown", message.getHeaderId(), message.getMessageBody());
            }
        }

        invoke(player, message.getHeaderId(), message);
    }

    /**
     * Invoke the request.
     *
     * @param messageId the message id
     * @param message the message
     */
    private void invoke(Player player, int messageId, NettyRequest message) {
        if (this.messages.containsKey(messageId)) {
            MessageEvent event = this.messages.get(messageId);
            try {
                event.handle(player, message);
            } catch (Exception ex) {
                String name = "";

                if (player != null && player.isLoggedIn()) {
                    name = player.getDetails().getName();
                }


                Log.getErrorLogger().error("Error occurred when handling (" + message.getHeaderId() + ") for user (" + name + "):", ex);
            }
        }

        message.dispose();
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    private ConcurrentHashMap<Integer, MessageEvent> getMessages() {
        return messages;
    }

    /**
     * Get the instance of {@link RoomManager}
     *
     * @return the instance
     */
    public static MessageHandler getInstance() {
        if (instance == null) {
            instance = new MessageHandler();
        }

        return instance;
    }
}

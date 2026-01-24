package net.h4bbo.kepler.messages.incoming.rooms;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.outgoing.games.GAMESTART;
import net.h4bbo.kepler.messages.outgoing.rooms.groups.GROUP_BADGES;
import net.h4bbo.kepler.messages.outgoing.rooms.groups.GROUP_MEMBERSHIP_UPDATE;
import net.h4bbo.kepler.messages.outgoing.rooms.items.DICE_VALUE;
import net.h4bbo.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import net.h4bbo.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import net.h4bbo.kepler.messages.outgoing.rooms.user.YOUARESPECTATOR;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.HashMap;
import java.util.List;

public class G_STAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null && player.getRoomUser().getGamePlayer().isSpectator()) {
            player.send(new YOUARESPECTATOR());

            Game game = player.getRoomUser().getGamePlayer().getGame();

            if (game.isGameStarted()) {
                player.send(new GAMESTART(game.getTotalSecondsLeft().get()));
            }
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null && player.getRoomUser().getGamePlayer().isInGame()) {
            return; // Not needed for game arenas
        }

        Room room = player.getRoomUser().getRoom();

        // Only refresh rights when in private room
        if (!room.isPublicRoom()) {
            room.refreshRights(player);
        }

        room.send(new USER_OBJECTS(player), List.of(player));
        player.send(new USER_OBJECTS(room.getEntities()));

        room.getEntityManager().tryRoomEntry(player);

        if (RoomManager.getInstance().getRoomEntryBadges().containsKey(room.getId())) {
            for (String badge : RoomManager.getInstance().getRoomEntryBadges().get(room.getId())) {
                player.getBadgeManager().tryAddBadge(badge, null);
            }
        }

        player.send(new USER_STATUSES(room.getEntities()));
        player.getRoomUser().setNeedsUpdate(true);

        for (Entity roomEntity : room.getEntities()) {
            if (roomEntity.getDetails().getFavouriteGroupId() > 0 && roomEntity.getDetails().getId() != player.getDetails().getId()) {
                var groupMember = roomEntity.getDetails().getGroupMember();
                player.send(new GROUP_MEMBERSHIP_UPDATE(roomEntity.getRoomUser().getInstanceId(), groupMember.getGroupId(), groupMember.getMemberRank().getClientRank()));
            }
        }

        for (Item item : room.getItems()) {
            if (item.getCurrentProgramValue().length() > 0) {
                player.send(new SHOWPROGRAM(new String[] { item.getCurrentProgram(), item.getCurrentProgramValue() }));
            }

            // If item is requiring an update, apply animations etc
            if (item.getRequiresUpdate()) {
                // For some reason the wheel of fortune doesn't spin when the custom data on initial road equals -1, thus we send it again
                if (item.hasBehaviour(ItemBehaviour.WHEEL_OF_FORTUNE)) {
                    player.send(new STUFFDATAUPDATE(item));
                }

                // Dices use a separate packet for rolling animation
                if (item.hasBehaviour(ItemBehaviour.DICE)) {
                    player.send(new DICE_VALUE(item.getId(), true, 0));
                }
            }
        }

        if (player.getDetails().getFavouriteGroupId() > 0) {
            var groupMember = player.getDetails().getGroupMember();

            room.send(new GROUP_BADGES(new HashMap<>() {{
                put(groupMember.getGroupId(), player.getJoinedGroup(player.getDetails().getFavouriteGroupId()).getBadge());
            }}));

            room.send(new GROUP_MEMBERSHIP_UPDATE(player.getRoomUser().getInstanceId(), groupMember.getGroupId(), groupMember.getMemberRank().getClientRank()));
        }
    }
}

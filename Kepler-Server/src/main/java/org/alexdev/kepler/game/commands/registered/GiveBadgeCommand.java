package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.BadgeDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.badges.BadgeManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import org.alexdev.kepler.util.StringUtil;

import java.util.List;

public class GiveBadgeCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("user");
        this.arguments.add("badge");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        // :givebadge Alex NL1

        // should refuse to give badges that belong to ranks
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        if (args.length == 1) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge code not provided"));
            return;
        }

        PlayerDetails targetUserDetails = PlayerDao.getDetails(args[0]);

        if (targetUserDetails == null) {
            player.send(new ALERT("Could not find user: " + args[0]));
            return;
        }

        String badge = args[1];

        if (badge.startsWith("GL") || badge.startsWith("ACH_") || badge.equalsIgnoreCase("Z64")) {
            return;
        }

        Player targetUser = PlayerManager.getInstance().getPlayerByName(args[0]);

        if (targetUser == null) {
            var badgeManager = new BadgeManager(targetUserDetails.getId());

            // Check if user already owns badge
            if (badgeManager.hasBadge(badge)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "User " + targetUserDetails.getName() + " already owns this badge."));
                return;
            }

            List<String> rankBadges = BadgeDao.getRankBadges();

            // Check if badge code is a rank badge
            if (rankBadges.contains(badge)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "This badge belongs to a certain rank. If you would like to give " + targetUserDetails.getName() + " this badge, increase their rank."));
                return;
            }

            // Add badge
            badgeManager.tryAddBadge(badge, null);
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge " + badge + " added to user " + targetUserDetails.getName()));
        } else {
            // Check if user already owns badge
            if (targetUser.getBadgeManager().hasBadge(badge)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "User " + targetUserDetails.getName() + " already owns this badge."));
                return;
            }

            List<String> rankBadges = BadgeDao.getRankBadges();

            // Check if badge code is a rank badge
            if (rankBadges.contains(badge)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "This badge belongs to a certain rank. If you would like to give " + targetUserDetails.getName() + " this badge, increase their rank."));
                return;
            }

            // Add badge
            targetUser.getBadgeManager().tryAddBadge(badge, null, 0);

            Room targetRoom = targetUser.getRoomUser().getRoom();

            // Let other room users know something changed if targetUser is inside a room
            if (targetRoom != null) {
                targetRoom.send(new FIGURE_CHANGE(targetUser.getRoomUser().getInstanceId(), targetUserDetails));
            }

            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge " + badge + " added to user " + targetUserDetails.getName()));
        }
    }

    @Override
    public String getDescription() {
        return "Add badge to user";
    }
}
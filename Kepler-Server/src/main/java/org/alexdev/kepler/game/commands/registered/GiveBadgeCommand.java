package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.badges.AVAILABLE_BADGES;
import org.alexdev.kepler.messages.outgoing.rooms.badges.USER_BADGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE.ChatMessageType;
import org.alexdev.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import org.apache.commons.lang3.StringUtils;

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

        Player targetUser = PlayerManager.getInstance().getPlayerByName(args[0]);

        if (targetUser == null) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Could not find user: " + args[0]));
            return;
        }

        if (args.length == 1) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge code not provided"));
            return;
        }

        String badge = args[1];

        if (badge.length() != 3) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge codes have a length of three characters."));
            return;
        }

        // Badge should be alphanumeric
        if (!StringUtils.isAlphanumeric(badge)) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge code provided not alphanumeric."));
            return;
        }

        // Check if characters are uppercase
        for (int i=0; i < badge.length(); i++) {
            if (!Character.isUpperCase(badge.charAt(i)) && !Character.isDigit(badge.charAt(i))) {
                player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge code should be uppercase."));
                return;
            }
        }

        PlayerDetails targetDetails = targetUser.getDetails();
        List<String> badges = targetDetails.getBadges();

        // Check if user already owns badge
        if (badges.contains(badge)) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "User " + targetDetails.getName() + " already owns this badge."));
            return;
        }

        List<String> rankBadges = PlayerDao.getAllRankBadges();

        // Check if badge code is a rank badge
        if (rankBadges.contains(badge)) {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "This badge belongs to a certain rank. If you would like to give " + targetDetails.getName() + " this badge, increase their rank."));
            return;
        }

        // Add badge
        badges.add(badge);
        targetDetails.setBadges(badges);

        // Set current badge to newly given
        targetDetails.setCurrentBadge(badge);

        // Set badge to active for display
        targetDetails.setShowBadge(true);

        // Send badges to user
        targetUser.send(new AVAILABLE_BADGES(targetDetails));

        Room targetRoom = targetUser.getRoomUser().getRoom();

        // Let other room users know something changed if targetUser is inside a room
        if (targetRoom != null) {
            targetRoom.send(new USER_BADGE(targetUser.getRoomUser().getInstanceId(), targetDetails));
            targetRoom.send(new FIGURE_CHANGE(targetUser.getRoomUser().getInstanceId(), targetDetails));
        }

        // Persist changes
        PlayerDao.saveCurrentBadge(targetDetails);
        PlayerDao.addBadge(targetDetails.getId(), badge);

        player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge " + badge + " added to user " + targetDetails.getName()));
    }

    @Override
    public String getDescription() {
        return "Add badge to user";
    }
}
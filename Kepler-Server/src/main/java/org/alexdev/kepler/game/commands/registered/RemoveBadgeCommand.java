package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.badges.BadgeManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

public class RemoveBadgeCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.MODERATOR_ACCESS);
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

        PlayerDetails targetUser = PlayerDao.getDetails(args[0]);

        if (targetUser == null) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Could not find user: " + args[0]));
            return;
        }

        if (args.length == 1) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge code not provided"));
            return;
        }

        String badge = args[1];

        if (badge.startsWith("GL") || badge.startsWith("ACH_") || badge.equalsIgnoreCase("Z64")) {
            return;
        }

        var badgeManager = new BadgeManager(targetUser.getId());

        // Check if user already owns badge
        if (!badgeManager.hasBadge(badge)) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "User " + targetUser.getName() + " does not have this badge."));
            return;
        }

        // Remove badge
        badgeManager.removeBadge(badge);
        player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Badge " + badge + " removed from user " + targetUser.getName()));
    }

    @Override
    public String getDescription() {
        return "Remove badge from user";
    }
}
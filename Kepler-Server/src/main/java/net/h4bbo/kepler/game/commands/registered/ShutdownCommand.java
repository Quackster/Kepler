package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.time.Duration;

public class ShutdownCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        /*this.arguments.add("minutes");*/
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        // Abort maintenance shutdown if provided argument is either cancel, off or stop (case insensitive)
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("stop")) {
                PlayerManager.getInstance().cancelMaintenance();
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Cancelled shutdown"));
                return;
            }
        }

        long minutes;

        // Try parsing minutes argument, use default if failed
        try {
            if (args.length > 0) {
                minutes = Long.parseLong(args[0]);
            } else {
                minutes = GameConfiguration.getInstance().getLong("shutdown.minutes");
            }
        } catch (NumberFormatException e) {
            minutes = GameConfiguration.getInstance().getLong("shutdown.minutes");
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Failed to parse minutes provided to shutdown command, defaulting to " + minutes + " minute(s)"));
        }

        // Enqueue maintenance shutdown
        PlayerManager.getInstance().planMaintenance(Duration.ofMinutes(minutes));

        // Let callee know Kepler is shutting down in X minutes
        player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Shutting down in " + minutes + " minute(s)"));
    }

    @Override
    public String getDescription() {
        return "<minutes> - Shutdown Kepler";
    }
}
package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

public class TestPacket implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        Player player = PlayerManager.getInstance().getPlayerById(commandArgs.UserId);

        if(player != null) {

            String packet = String.join(" ", commandArgs.Message);

            for (int i = 0; i < 14; i++) {
                packet = packet.replace("{" + i + "}", Character.toString((char)i));
            }

            // Add ending packet suffix
            packet += Character.toString((char)1);

            player.sendObject(packet);
        }
    }
}

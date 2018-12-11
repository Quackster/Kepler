package org.alexdev.roseau.game.commands.types;

import org.alexdev.roseau.game.commands.Command;
import org.alexdev.roseau.game.player.Player;

public class HelpCommand implements Command {

	@Override
	public void handleCommand(Player player, String message) {
		
		StringBuilder about = new StringBuilder();
		about.append("Commands:\n\n");
		about.append("- :sit\n");
		about.append("- :about\n");
		about.append("- :help.");
		player.sendAlert(about.toString());
	}

}

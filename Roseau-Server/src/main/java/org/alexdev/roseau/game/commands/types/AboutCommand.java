package org.alexdev.roseau.game.commands.types;

import org.alexdev.roseau.game.commands.Command;
import org.alexdev.roseau.game.player.Player;

public class AboutCommand implements Command {

	@Override
	public void handleCommand(Player player, String message) {
		
		StringBuilder about = new StringBuilder();
		about.append("Roseau V1 server written by Quackster\n\n");
		about.append("With the help of:\n\n");
		about.append("- Ascii\n");
		about.append("- lab-hotel\n");
		about.append("- Some stuff taken from office.boy and Nillus,\nthe authors of Blunk v5.");
		player.sendAlert(about.toString());
	}

}

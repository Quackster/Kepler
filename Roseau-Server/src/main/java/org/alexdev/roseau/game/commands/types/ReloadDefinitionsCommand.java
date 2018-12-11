package org.alexdev.roseau.game.commands.types;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.commands.Command;
import org.alexdev.roseau.game.player.Player;

public class ReloadDefinitionsCommand implements Command {

	@Override
	public void handleCommand(Player player, String message) {
		Roseau.getGame().getItemManager().load();
	}

}

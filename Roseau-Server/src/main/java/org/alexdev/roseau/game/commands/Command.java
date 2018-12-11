package org.alexdev.roseau.game.commands;

import org.alexdev.roseau.game.player.Player;

public interface Command {

	public void handleCommand(Player player, String message);
}

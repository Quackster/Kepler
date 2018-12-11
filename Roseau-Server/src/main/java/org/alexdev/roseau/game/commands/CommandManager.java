package org.alexdev.roseau.game.commands;

import java.util.Map;

import org.alexdev.roseau.game.commands.types.AboutCommand;
import org.alexdev.roseau.game.commands.types.HelpCommand;
import org.alexdev.roseau.game.commands.types.SitCommand;
import org.alexdev.roseau.game.player.Player;

import com.google.common.collect.Maps;

public class CommandManager {

	private Map<String, Command> commands;

	public CommandManager() {
		this.commands = Maps.newHashMap();
	}
	
	public void load() {
		this.commands.put("about", new AboutCommand());
		this.commands.put("sit", new SitCommand());
		this.commands.put("help", new HelpCommand());
		//this.commands.put("reloaddef", new ReloadDefinitionsCommand());
	}

	public boolean hasCommand(String message) {

		if (message.startsWith(":") && message.length() > 1) {

			String commandName = message.split(":")[1];

			if (commands.containsKey(commandName)) {
				return true;
			}
		}

		return false;
	}

	public void invokeCommand(Player player, String message) {

		String commandName = message.split(":")[1];

		if (commands.containsKey(commandName)) {
			commands.get(commandName).handleCommand(player, message);
		}
	}
}
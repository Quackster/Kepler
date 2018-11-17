package org.alexdev.kepler.game.commands;

import org.alexdev.kepler.game.commands.clientside.ChooserCommand;
import org.alexdev.kepler.game.commands.clientside.EventsCommand;
import org.alexdev.kepler.game.commands.clientside.FurniCommand;
import org.alexdev.kepler.game.commands.registered.UfosCommand;
import org.alexdev.kepler.game.commands.registered.*;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class CommandManager {
    private Map<String[], Command> commands;

    private static final Logger log = LoggerFactory.getLogger(CommandManager.class);
    private static CommandManager instance;

    public CommandManager() {
        this.commands = new LinkedHashMap<>();
        this.commands.put(new String[] { "help", "commands" }, new HelpCommand());
        this.commands.put(new String[] { "about", "info" }, new AboutCommand());
        this.commands.put(new String[] { "givedrink"}, new GiveDrinkCommand());
        this.commands.put(new String[] { "sit" }, new SitCommand());
        this.commands.put(new String[] { "uptime", "status" }, new UptimeCommand());
        this.commands.put(new String[] { "poof", "update" }, new PoofCommand());
        this.commands.put(new String[] { "coords" }, new CoordsCommand());
        this.commands.put(new String[] { "pickall" }, new PickAllCommand());
        this.commands.put(new String[] { "usersonline", "whosonline" }, new UsersOnlineCommand());
        this.commands.put(new String[] { "rgb", "rainbow" }, new RainbowDimmerCommand());
        this.commands.put(new String[] { "afk", "idle" }, new AfkCommand());
        this.commands.put(new String[] { "motto" }, new ChangeMottoCommand());

        // Staff commands
        this.commands.put(new String[] { "givebadge" }, new GiveBadgeCommand());
        this.commands.put(new String[] { "packet" }, new PacketTestCommand());
        this.commands.put(new String[] { "reload" }, new ReloadCommand());
        this.commands.put(new String[] { "shutdown" }, new ShutdownCommand());
        this.commands.put(new String[] { "setprice" }, new SetItemPriceCommand());
        this.commands.put(new String[] { "setconfig" }, new SetConfigCommand());
        this.commands.put(new String[] { "hotelalert" }, new HotelAlertCommand());
        this.commands.put(new String[] { "ufos" }, new UfosCommand());
        this.commands.put(new String[] { "talk" }, new TalkCommand());

        // Add client-side commands to list
        this.commands.put(new String[] { "chooser" }, new ChooserCommand());
        this.commands.put(new String[] { "furni" }, new FurniCommand());
        this.commands.put(new String[] { "events" }, new EventsCommand());

        log.info("Loaded {} commands", commands.size());
    }

    /**
     * Gets the command.
     *
     * @param commandName the command name
     * @return the command
     */
    private Command getCommand(String commandName) {
        for (Entry<String[], Command> entrySet : commands.entrySet()) {
            for (String name : entrySet.getKey()) {

                if (commandName.equalsIgnoreCase(name)) {
                    return entrySet.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Checks for command.
     *
     * @param entity the player
     * @param message the message
     * @return true, if successful
     */
    public boolean hasCommand(Entity entity, String message) {
        if (message.startsWith(":") && message.length() > 1) {

            String commandName = message.split(":")[1].split(" ")[0];
            Command cmd = this.getCommand(commandName);

            if (cmd != null) {
                return this.hasCommandPermission(entity, cmd);
            }
        }

        return false;
    }

    /**
     * Checks for command permission.
     *
     * @param entity the player
     * @param cmd the command
     * @return true, if successful
     */
    public boolean hasCommandPermission(Entity entity, Command cmd) {
        if (cmd.getPermissions().size() > 0) {
            for (Fuseright permission : cmd.getPermissions()) {
                if (entity.hasFuse(permission)) {
                    return true;
                }
            }
        } else {
            return true;
        }

        return false;
    }

    /**
     * Invoke command.
     *
     * @param entity the player
     * @param message the message
     */
    public void invokeCommand(Entity entity, String message) {
        String commandName = message.split(":")[1].split(" ")[0];
        Command cmd = this.getCommand(commandName);

        String[] args = new String[0];

        if (message.length() > (commandName.length() + 2)) {
            args = message.replace(":" + commandName + " ", "").split(" ");
        }

        if (cmd != null) {
            if (args.length < cmd.getArguments().length) {
                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    player.send(new ALERT(TextsManager.getInstance().getValue("player_commands_no_args")));
                } else {
                    System.out.println(TextsManager.getInstance().getValue("player_commands_no_args"));
                }
                return;
            }
            
            cmd.handleCommand(entity, message, args);
        }
    }

    /**
     * Gets the commands.
     *
     * @return the commands
     */
    public List<Pair<String[], Command>> getCommands() {
        List<Pair<String[], Command>> commandList = new ArrayList<>();

        for (var set : this.commands.entrySet()) {
            commandList.add(Pair.of(set.getKey(), set.getValue()));
        }

        return commandList;
    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }

        return instance;
    }
}
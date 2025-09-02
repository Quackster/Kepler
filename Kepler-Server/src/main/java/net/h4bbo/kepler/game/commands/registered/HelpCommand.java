package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.commands.CommandManager;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class HelpCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        var commands = StringUtil.paginate(CommandManager.getInstance().getCommands(), 10);

        int pageId = 1;

        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            pageId = Integer.parseInt(args[0]);
        }

        if (!commands.containsKey(pageId - 1)) {
            pageId = 1;
        }

        var commandList = commands.get(pageId - 1);

        StringBuilder about = new StringBuilder();
        about.append("Commands ('<' and '>' are optional parameters):<br>").append("<br>");

        for (var commandSet : commandList) {
            String[] commandAlias = commandSet.getKey();
            Command command = commandSet.getValue();

            if (!CommandManager.getInstance().hasCommandPermission(entity, command)) {
                continue;
            }

            about.append(":").append(String.join("/", commandAlias));

            if (command.getArguments().length > 0) {
                if (command.getArguments().length > 1) {
                    about.append(" [").append(String.join("] [", command.getArguments())).append("]");
                } else {
                    about.append(" [").append(command.getArguments()[0]).append("]");
                }
            }

            about.append(" - ").append(command.getDescription()).append("<br>");
        }

        about.append("<br>")
                .append("Page ")
                .append(pageId)
                .append(" out of ")
                .append(commands.size());

        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.send(new ALERT(about.toString()));
        }
    }

    @Override
    public String getDescription() {
        return "<page> - List available commands";
    }
}

package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.dao.mysql.CurrencyDao;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import net.h4bbo.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class GiveCreditsCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("user");
        this.arguments.add("credits");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        // :credits Patrick 300

        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Player targetUser = PlayerManager.getInstance().getPlayerByName(args[0]);

        if (targetUser == null) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Could not find user: " + args[0]));
            return;
        }

        if (args.length == 1) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Credit amount not provided"));
            return;
        }

        String credits = args[1];

        // credits should be numeric
        if (!NumberUtils.isCreatable(credits)) {
            player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Credit amount is not a number."));
            return;
        }

        PlayerDetails targetDetails = targetUser.getDetails();
        Map<PlayerDetails, Integer> playerDetailsToSave = new LinkedHashMap<>() {{
            put(targetDetails, Integer.parseInt(credits));
        }};

        CurrencyDao.increaseCredits(playerDetailsToSave);

        targetUser.send(new CREDIT_BALANCE(targetUser.getDetails().getCredits()));

        player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), credits + " has been added to user " + targetDetails.getName()));
    }

    @Override
    public String getDescription() {
        return "Give credits to user";
    }
}

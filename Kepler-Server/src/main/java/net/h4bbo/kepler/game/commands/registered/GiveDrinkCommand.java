package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.RoomUserStatus;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.texts.TextsManager;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

public class GiveDrinkCommand extends Command {

    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.DEFAULT);
    }

    @Override
    public void addArguments() {
        this.arguments.add("user");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Player targetUser = PlayerManager.getInstance().getPlayerByName(args[0]);

        if (targetUser == null ||
                targetUser.getRoomUser().getRoom() == null ||
                targetUser.getRoomUser().getRoom().getId() != player.getRoomUser().getRoom().getId()) {
            player.send(new ALERT("Could not find user: " + args[0]));
            return;
        }

        if (!player.getRoomUser().containsStatus(StatusType.CARRY_DRINK) && !player.getRoomUser().containsStatus(StatusType.CARRY_FOOD)) {
            player.send(new ALERT("You are not carrying any food or drinks to give."));
            return;
        }

        RoomUserStatus drink = null;

        if (player.getRoomUser().containsStatus(StatusType.CARRY_DRINK)) {
            drink = player.getRoomUser().getStatus(StatusType.CARRY_DRINK);
        }

        if (player.getRoomUser().containsStatus(StatusType.CARRY_FOOD)) {
            drink = player.getRoomUser().getStatus(StatusType.CARRY_FOOD);
        }

        if (drink != null) {
            if (targetUser.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), targetUser.getDetails().getName() + " is sleeping."));
                return;
            }

            // Give drink to user if they're not already having a drink or food, and they're not dancing
            if (targetUser.getRoomUser().containsStatus(StatusType.CARRY_FOOD) ||
                targetUser.getRoomUser().containsStatus(StatusType.CARRY_DRINK)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), targetUser.getDetails().getName() + " is already enjoying a drink."));
                return;
            }

            if (targetUser.getRoomUser().containsStatus(StatusType.DANCE)) {
                player.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "Can't hand drink to " + targetUser.getDetails().getName() + ", because he/she is dancing."));
                return;
            }

            int carryID = Integer.parseInt(drink.getValue());
            targetUser.getRoomUser().carryItem(carryID, null);
            String carryName = TextsManager.getInstance().getValue("handitem" + carryID);

            targetUser.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.WHISPER, targetUser.getRoomUser().getInstanceId(), player.getDetails().getName() + " handed you a " + carryName + "."));

            player.getRoomUser().removeStatus(StatusType.CARRY_DRINK);
            player.getRoomUser().removeStatus(StatusType.CARRY_FOOD);
            player.getRoomUser().setNeedsUpdate(true);
        }
    }

    @Override
    public String getDescription() {
        return "Gives a user your own drink";
    }
}

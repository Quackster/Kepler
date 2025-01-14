package org.alexdev.kepler.game.commandqueue;


public enum CommandType {
    REFRESH_APPEARANCE("refresh_appearance"),
    UPDATE_CREDITS("update_credits"),
    REDUCE_CREDITS("reduce_credits"),
    PURCHASE_FURNI("purchase_furni"),
    ROOM_FORWARD("room_forward"),
    CAMPAIGN("campaign"),
    UPDATE_ROOM("update_room"),
    REMOTE_ALERT("remote_alert"),
    REMOTE_KICK("remote_kick"),
    REMOTE_BAN("remote_ban"),
    TEST_PACKET("test_packet"),
    UPDATE_INFOBUS("update_infobus"),
    RESET_BOTS("reset_bots"),
    BOT_TALK("bot_talk");

    private final String commandName;

    CommandType(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static CommandType findCommandType(String commandName) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getCommandName().equals(commandName)) {
                return commandType;
            }
        }

        return null;
    }
}

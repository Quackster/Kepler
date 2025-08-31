package org.alexdev.kepler.game.games.enums;

import org.alexdev.kepler.util.config.GameConfiguration;

public enum GameType {
    BATTLEBALL("bb_lobby_1", 1),
    SNOWSTORM("snowwar_lobby_1", 0),
    WOBBLE_SQUABBLE("md_a", 1);

    private String lobbyModel;
    private int typeId;

    GameType(String lobbyModel, int typeId) {
        this.lobbyModel = lobbyModel;
        this.typeId = typeId;
    }

    /**
     * Get the cost of tickets required to play each game
     *
     * @return the ticket amount required
     */
    public int getTicketCost() {
        return GameConfiguration.getInstance().getInteger(this.name().toLowerCase() + ".ticket.charge");
    }

    public String getLobbyModel() {
        return lobbyModel;
    }

    public int getTypeId() {
        return typeId;
    }
}

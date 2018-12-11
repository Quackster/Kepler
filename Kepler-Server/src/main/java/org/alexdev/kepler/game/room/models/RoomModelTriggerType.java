package org.alexdev.kepler.game.room.models;

import org.alexdev.kepler.game.room.models.triggers.*;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public enum RoomModelTriggerType {
    FLAT_TRIGGER(new FlatTrigger()),
    BATTLEBALL_LOBBY_TRIGGER(new BattleballLobbyTrigger()),
    SNOWSTORM_LOBBY_TRIGGER(new SnowStormLobbyTrigger()),
    SPACE_CAFE_TRIGGER(new SpaceCafeTrigger()),
    HABBO_LIDO_TRIGGER(new HabboLidoTrigger()),
    ROOFTOP_RUMBLE_TRIGGER(new RooftopRumbleTrigger()),
    DIVING_DECK_TRIGGER(new DivingDeckTrigger()),
    NONE(null);

    private GenericTrigger roomTrigger;

    RoomModelTriggerType(GenericTrigger trigger) {
        this.roomTrigger = trigger;
    }

    public GenericTrigger getRoomTrigger() {
        return roomTrigger;
    }
}

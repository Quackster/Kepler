package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.moderation.actions.*;

public enum ModerationActionType {
    ALERT_USER(0, 0, new ModeratorAlertUserAction()),
    KICK_USER(0, 1,  new ModeratorKickUserAction()),
    BAN_USER(0, 2, new ModeratorBanUserAction()),
    ROOM_ALERT(1, 0,  new ModeratorRoomAlertAction()),
    ROOM_KICK(1, 1, new ModeratorRoomKickAction());

    int targetType;
    int actionType;
    ModerationAction moderationAction;

    ModerationActionType(int targetType, int actionType, ModerationAction moderationAction) {
        this.targetType = targetType;
        this.actionType = actionType;
        this.moderationAction = moderationAction;
    }

    public int getTargetType() {
        return targetType;
    }

    public int getActionType() {
        return actionType;
    }

    public ModerationAction getModerationAction() {
        return moderationAction;
    }
}

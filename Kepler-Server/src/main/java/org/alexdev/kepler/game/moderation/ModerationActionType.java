package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.moderation.actions.ModeratiorAlertUserAction;
import org.alexdev.kepler.game.moderation.actions.ModeratiorKickUserAction;
import org.alexdev.kepler.game.moderation.actions.ModeratiorRoomAlertAction;
import org.alexdev.kepler.game.moderation.actions.ModeratiorRoomKickAction;

public enum ModerationActionType {
    ALERT_USER(0, 0, new ModeratiorAlertUserAction()),
    KICK_USER(0, 1,  new ModeratiorKickUserAction()),
    BAN_USER(0, 2, null),
    ROOM_ALERT(1, 0,  new ModeratiorRoomAlertAction()),
    ROOM_KICK(1, 1, new ModeratiorRoomKickAction());

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

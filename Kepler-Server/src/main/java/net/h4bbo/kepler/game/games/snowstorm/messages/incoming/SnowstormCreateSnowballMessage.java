package org.alexdev.kepler.game.games.snowstorm.messages.incoming;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormPlayers;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormCreateSnowballEvent;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormActivityState;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormConstants;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMessage;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SnowstormCreateSnowballMessage implements SnowStormMessage {
    @Override
    public void handle(NettyRequest request, SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        var attributes = SnowStormPlayers.get(gamePlayer);
        if (!attributes.isWalkable()) {
            return;
        }

        if (attributes.getSnowballs().get() >= SnowStormConstants.MAX_SNOWBALLS) {
            return;
        }

        snowStormGame.getUpdateTask().queueEvent(new SnowStormCreateSnowballEvent(gamePlayer.getObjectId()));

        if (!attributes.isWalkable() || attributes.getHealth().get() == 0) {
            return;
        }

        if (attributes.isWalking()) {
            SnowStormAvatarObject avatar = SnowStormAvatarObject.getAvatar(gamePlayer);
            if (avatar != null) {
                avatar.stopWalking();
            }
        }

        attributes.setActivityState(SnowStormActivityState.ACTIVITY_STATE_CREATING);
        attributes.setActivityTimer(SnowStormConstants.CREATING_TIMER);
    }
}

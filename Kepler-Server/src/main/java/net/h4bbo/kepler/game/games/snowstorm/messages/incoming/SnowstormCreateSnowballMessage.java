package net.h4bbo.kepler.game.games.snowstorm.messages.incoming;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormPlayers;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormCreateSnowballEvent;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormConstants;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMessage;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
